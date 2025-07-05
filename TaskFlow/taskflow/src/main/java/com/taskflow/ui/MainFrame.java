package com.taskflow.ui;

import com.taskflow.model.Category;
import com.taskflow.model.RootGroup;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import com.taskflow.action.*;
import com.taskflow.persistence.XmlDomHandler;
import com.taskflow.persistence.XmlSaxHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

//Fereastră principală care expune metodele necesare acțiunilor și afișează UI-ul.
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final TaskService taskService; // Serviciu pentru gestionarea operațiilor pe task-uri
    private final TaskTableModel taskTableModel; // Modelul utilizat de JTable pentru afișarea task-urilor
    private final CategoryTreeModel categoryTreeModel; // Modelul utilizat de JTree pentru afișarea categoriilor
    private final JTable taskTable; // Componenta de tabel pentru task-uri
    private final JTree categoryTree; // Componenta de arbore pentru categorii și grupuri
    private final JTextField filterField; // Câmp text pentru filtrarea task-urilor
    private final List<Task> masterTasks; // Lista completă de task-uri utilizată pentru resetarea filtrului
    private File lastDir; // Ultimul director folosit pentru Open/Save
    private File currentFile; // Fișierul curent încărcat sau salvat

    public MainFrame(TaskService service) {
        super("TaskFlow"); // Titlul ferestrei principale
        this.taskService       = service;
        this.taskTableModel    = new TaskTableModel(getAllTasks());
        this.masterTasks       = List.copyOf(getAllTasks());
        this.categoryTreeModel = new CategoryTreeModel(taskService.getAllRoots());

        // Inițializează tabelul și sorter-ul
        taskTable = new JTable(taskTableModel);
        taskTable.setRowSorter(new TableRowSorter<>(taskTableModel));

        // Inițializează arborele pentru categorii
        categoryTree = new JTree(categoryTreeModel);
        categoryTree.setRootVisible(false); // Ascunde nodul rădăcină invizibil
        categoryTree.setShowsRootHandles(true); // Afișează iconițele de expand/collapse
        categoryTree.setCellRenderer(new ColorTreeCellRenderer()); // Renderer personalizat pentru culoare

        // Configurare câmp de filtrare
        filterField = new JTextField(15);

        // Drag & drop pe arbore pentru mutarea elementelor
        categoryTree.setDragEnabled(true);
        categoryTree.setDropMode(DropMode.ON);
        categoryTree.setTransferHandler(new TreeTransferHandler(this, taskService));

        // Context menu 1: click pe spațiu liber în arbore pentru a adăuga un root nou
        JPopupMenu emptyPopup = new JPopupMenu();
        emptyPopup.add(new JMenuItem(new RootDialogAction(this, taskService, true)));
        categoryTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { maybeShow(e); }
            public void mouseReleased(MouseEvent e) { maybeShow(e); }
            private void maybeShow(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                TreePath path = categoryTree.getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    emptyPopup.show(categoryTree, e.getX(), e.getY()); // Afișează meniul pentru spațiu liber
                }
            }
        });

        // Context menu 2: click pe RootGroup pentru operatii pe grupuri rădăcină
        JPopupMenu rootPopup = new JPopupMenu();
        rootPopup.add(new JMenuItem(new CategoryDialogAction(this, taskService, true)));
        rootPopup.addSeparator();
        rootPopup.add(new JMenuItem(new RootDialogAction(this, taskService, false)));
        rootPopup.add(new JMenuItem(new DeleteRootAction(this, taskService)));
        categoryTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { maybeShow(e); }
            public void mouseReleased(MouseEvent e) { maybeShow(e); }
            private void maybeShow(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                TreePath path = categoryTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    Object o = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                    if (o instanceof RootGroup) {
                        categoryTree.setSelectionPath(path);
                        rootPopup.show(categoryTree, e.getX(), e.getY()); // Meniu pentru root selectat
                    }
                }
            }
        });

        // Context menu 3: click pe Category pentru operatii pe categorii
        JPopupMenu catPopup = new JPopupMenu();
        catPopup.add(new JMenuItem(new TaskDialogAction(this, true)));
        catPopup.addSeparator();
        catPopup.add(new JMenuItem(new CategoryDialogAction(this, taskService, false)));
        catPopup.add(new JMenuItem(new DeleteCategoryAction(this, taskService)));
        categoryTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { maybeShow(e); }
            public void mouseReleased(MouseEvent e) { maybeShow(e); }
            private void maybeShow(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                TreePath path = categoryTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    Object o = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                    if (o instanceof Category) {
                        categoryTree.setSelectionPath(path);
                        catPopup.show(categoryTree, e.getX(), e.getY()); // Meniu pentru categoria selectată
                    }
                }
            }
        });

        // Context menu 4: click pe Task pentru operatii pe task-uri
        JPopupMenu taskPopup = new JPopupMenu();
        taskPopup.add(new JMenuItem(new TaskDialogAction(this, false)));
        taskPopup.add(new JMenuItem(new DeleteTaskAction(this, taskService)));
        taskPopup.addSeparator();
        taskPopup.add(new JMenuItem(new ToggleCompletedAction(this)));
        categoryTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { maybeShow(e); }
            public void mouseReleased(MouseEvent e) { maybeShow(e); }
            private void maybeShow(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                TreePath path = categoryTree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    Object o = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                    if (o instanceof Task) {
                        categoryTree.setSelectionPath(path);
                        taskPopup.show(categoryTree, e.getX(), e.getY()); // Meniu pentru task selectat
                    }
                }
            }
        });
        
        // Configurare layout cu JSplitPane
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(categoryTree),
            new JScrollPane(taskTable)
        );
        split.setDividerLocation(200);

        // Panou superior cu câmpul de filtrare
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Filter:"));
        top.add(filterField);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // Bara de meniu cu acțiuni de fișier și listă
        JMenuBar mb = new JMenuBar();
        mb.add(createFileMenu());
        mb.add(createListMenu());
        setJMenuBar(mb);

        // Filtrare în timp real la modificarea textului din filterField
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600); // Dimensiune inițială a ferestrei
        setLocationRelativeTo(null); // Centrare pe ecran
        
     // Selectarea din arbore filtrează tabelul
        categoryTree.addTreeSelectionListener(e -> {
            TreePath p = e.getNewLeadSelectionPath();
            if (p==null) return;
            Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
            if      (o instanceof RootGroup) showTasks(getTasksFromRoot((RootGroup)o));
            else if (o instanceof Category ) showTasks(((Category)o).getTasks());
        });

        // Dublu click pe task în arbore: afișează doar acel task
        categoryTree.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                if (e.getClickCount()==2){
                    TreePath p = categoryTree.getPathForLocation(e.getX(),e.getY());
                    if (p!=null){
                        Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
                        if (o instanceof Task) showTasks(List.of((Task)o));
                    }
                }
            }
        });
    }

    private JMenu createFileMenu() {
        JMenu m = new JMenu("File"); // Meniu File cu acțiuni de fișier
        m.add(new OpenAction(this));
        m.add(new SaveAction(this));
        m.addSeparator();
        m.add(new ImportAction(this));
        m.add(new ExportAction(this));
        m.addSeparator();
        m.add(new DeleteFileAction(this));
        return m;
    }

    private JMenu createListMenu() {
        JMenu m = new JMenu("List"); // Meniu List pentru operațiuni rapide
        m.add(new ResetOrderAction(this)); // Reordonează lista de task-uri în ordine inițială
        return m;
    }

    // Încarcă XML din fișier și reîmprospătează arborele și tabela
    public void loadDataFromFile(File f) {
        try {
            List<RootGroup> roots =
                new XmlSaxHandler().loadRootGroups(f); // Parsează fișier XML cu SAX
            taskService.getAllRoots().clear(); // Șterge datele existente
            roots.forEach(taskService::addRoot); // Adaugă noile root-uri
            refreshTreeAll(); // Actualizează UI
        } catch (Exception ex) {
            showError(ex); // Afișează eroarea într-un dialog
        }
    }

    // Salvează structura curentă de root-uri în fișier XML
    public void saveDataToFile(File f) {
        try {
            new XmlDomHandler()
                .saveRootGroups(taskService.getAllRoots(), f); // Salvează XML folosind DOM
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    // Metode expuse pentru acțiuni:

    public TaskService getTaskService() {
        return taskService; // Returnează serviciul de task-uri
    }

    public JTable getTaskTable() {
        return taskTable; // Returnează componenta tabel
    }

    public TaskTableModel getTaskTableModel() {
        return taskTableModel; // Returnează modelul de tabel
    }

    public List<Task> getMasterTasks() {
        return masterTasks; // Returnează lista completă originală de task-uri
    }

    public RootGroup createImportedRoot(List<Category> cats) {
        // Creează un root temporar pentru elemente importate
        RootGroup imp = new RootGroup("Imported", "#0077CC");
        imp.getCategories().addAll(cats);
        return imp;
    }

    public boolean deleteRecursively(File f) {
        // Șterge fișiere sau directoare recursiv
        if (f.isDirectory()) {
            File[] ch = f.listFiles();
            if (ch != null) for (File c : ch) if (!deleteRecursively(c)) return false;
        }
        return f.delete();
    }

    public void refreshTreeAll() {
        // Reîmprospătează arborele și populatează tabela cu toate task-urile
        categoryTreeModel.updateRoots(taskService.getAllRoots());
        showTasks(getAllTasks());
        expandAll();
    }

    public void refreshAfterChange(Category cat) {
        // Actualizează arborele și tabela după modificări asupra unei categorii specifice
        categoryTreeModel.updateRoots(taskService.getAllRoots());
        if (cat != null) showTasks(cat.getTasks());
        else showTasks(getAllTasks());
        expandAll();
    }

    public void showError(Exception ex) {
        // Afișează un dialog de eroare cu mesajul ex.getMessage()
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void applyFilter() {
        // Aplică textul din filterField pentru a filtra task-urile afișate
        String t = filterField.getText().trim().toLowerCase();
        if (t.isEmpty()) refreshTreeAll();
        else taskTableModel.setTasks(
            getAllTasks().stream()
                .filter(x -> x.getTitle().toLowerCase().contains(t)
                          || x.getDescription().toLowerCase().contains(t))
                .toList()
        );
    }

    private void showTasks(List<Task> tasks) {
        // Actualizează lista de task-uri afișate în tabel
        taskTableModel.setTasks(tasks);
    }

    private List<Task> getAllTasks() {
        // Retournează toate task-urile din toate root-urile
        return taskService.getAllRoots().stream()
            .flatMap(r -> r.getCategories().stream())
            .flatMap(c -> c.getTasks().stream())
            .toList();
    }

    private List<Task> getTasksFromRoot(RootGroup r) {
        // Colectează și returnează task-urile pentru un anumit root
        return r.getCategories().stream()
            .flatMap(c->c.getTasks().stream())
            .toList();
    }
    
    public RootGroup getSelectedRoot() {
        // Obține root-ul selectat în arbore sau null
        TreePath p = categoryTree.getSelectionPath();
        if (p == null) return null;
        Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
        return (o instanceof RootGroup) ? (RootGroup)o : null;
    }

    public Category getSelectedCategory() {
        // Obține categoria selectată în arbore sau null
        TreePath p = categoryTree.getSelectionPath();
        if (p == null) return null;
        Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
        return (o instanceof Category) ? (Category)o : null;
    }

    public Task getSelectedTask() {
        // Obține task-ul selectat în arbore sau null
        TreePath p = categoryTree.getSelectionPath();
        if (p == null) return null;
        Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
        return (o instanceof Task) ? (Task)o : null;
    }

    public void expandAll() {
        // Expandează toate nodurile din arbore
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < categoryTree.getRowCount(); i++) {
                categoryTree.expandRow(i);
            }
        });
    }

    public JTree getCategoryTree() {
        return categoryTree; // Returnează componenta arbore pentru acces extern
    }
    
    public File getLastDir() {
        return lastDir;
    }
    
    public void setLastDir(File d) {
        lastDir = d;
    }
    
    public File getCurrentFile() { 
        return currentFile; 
    }
    
    public void setCurrentFile(File f) { 
        currentFile = f; 
    }
}

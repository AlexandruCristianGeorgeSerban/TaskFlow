package com.taskflow.ui;

import com.taskflow.model.Category;
import com.taskflow.model.RootGroup;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import com.taskflow.action.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.util.List;

// Fereastră principală care expune metodele necesare acțiunilor și afișează UI-ul.
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final TaskService taskService; // Serviciu pentru operații pe task-uri
    private final TaskTableModel taskTableModel; // Modelul tabelei de task-uri
    private final CategoryTreeModel categoryTreeModel; // Modelul arborelui de categorii
    private final JTable taskTable; // Tabelul de task-uri
    private final JTree categoryTree; // Arborele de categorii
    private final JTextField filterField; // Câmpul de filtrare
    private final List<Task> masterTasks; // Lista completă de task-uri

    public MainFrame(TaskService service) {
        super("TaskFlow"); // Titlul ferestrei
        this.taskService       = service;
        this.taskTableModel    = new TaskTableModel(getAllTasks());
        this.masterTasks       = List.copyOf(getAllTasks());
        this.categoryTreeModel = new CategoryTreeModel(taskService.getAllRoots());

        // Inițializează componentele
        taskTable = new JTable(taskTableModel);
        taskTable.setRowSorter(new TableRowSorter<>(taskTableModel)); // Sortare în tabel
        categoryTree = new JTree(categoryTreeModel);
        categoryTree.setRootVisible(false);
        categoryTree.setShowsRootHandles(true);
        categoryTree.setCellRenderer(new ColorTreeCellRenderer());
        filterField = new JTextField(15);

        // Drag & drop în arbore
        categoryTree.setDragEnabled(true);
        categoryTree.setDropMode(DropMode.ON);
        categoryTree.setTransferHandler(new TreeTransferHandler(this, taskService));

        // Layout cu split pane
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(categoryTree),
            new JScrollPane(taskTable)
        );
        split.setDividerLocation(200);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Filter:")); top.add(filterField);
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // Meniuri
        JMenuBar mb = new JMenuBar();
        mb.add(createFileMenu());
        mb.add(createRootMenu());
        mb.add(createCategoryMenu());
        mb.add(createTaskMenu());
        setJMenuBar(mb);

        // Filtrare în timp real
        filterField.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){ applyFilter(); }
            public void removeUpdate(DocumentEvent e){ applyFilter(); }
            public void changedUpdate(DocumentEvent e){ applyFilter(); }
        });

        // Selectarea din arbore filtrează tabelul
        categoryTree.addTreeSelectionListener(e -> {
            TreePath p = e.getNewLeadSelectionPath();
            if (p==null) return;
            Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
            if      (o instanceof RootGroup) showTasks(getTasksFromRoot((RootGroup)o));
            else if (o instanceof Category ) showTasks(((Category)o).getTasks());
        });

        // Dublu click pe task în arbore
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

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800,600);
        setLocationRelativeTo(null); // Center window
    }

    // Metode expuse acțiunilor:
    public TaskService getTaskService() {
        return taskService;
    }

    public JTable getTaskTable() {
        return taskTable;
    }
    public TaskTableModel getTaskTableModel() {
        return taskTableModel;
    }
    public List<Task> getMasterTasks() {
        return masterTasks;
    }

    public RootGroup createImportedRoot(List<Category> cats) {
        // Creează root temporar pentru importuri
        RootGroup imp = new RootGroup("Imported", "#0077CC");
        imp.getCategories().addAll(cats);
        return imp;
    }

    public boolean deleteRecursively(File f) {
        // Șterge fișier/director recursiv
        if (f.isDirectory()) {
            File[] ch = f.listFiles();
            if (ch!=null) for (File c: ch) if (!deleteRecursively(c)) return false;
        }
        return f.delete();
    }

    // Reîmprospătează arborele și tabelul cu toate task-urile
    public void refreshTreeAll() {
        categoryTreeModel.updateRoots(taskService.getAllRoots());
        showTasks(getAllTasks());
        expandAll();
    }

    // Reîmprospătează arborele și afișează task-urile din categoria dată (sau toate)
    public void refreshAfterChange(Category cat) {
        categoryTreeModel.updateRoots(taskService.getAllRoots());
        if (cat != null) showTasks(cat.getTasks()); else showTasks(getAllTasks());
        expandAll();
    }

    public void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Meniuri folosind acțiuni:
    private JMenu createFileMenu() {
        JMenu m = new JMenu("File");
        m.add(new ImportAction(this));
        m.add(new ExportAction(this));
        m.addSeparator();
        m.add(new DeleteFileAction(this));
        return m;
    }
    private JMenu createRootMenu() {
        JMenu m = new JMenu("Root");
        m.add(new AddRootAction(this));
        m.add(new EditRootAction(this));
        m.addSeparator();
        m.add(new DeleteRootAction(this, taskService));
        return m;
    }
    private JMenu createCategoryMenu() {
        JMenu m = new JMenu("Category");
        m.add(new AddCategoryAction(this));
        m.add(new EditCategoryAction(this));
        m.addSeparator();
        m.add(new DeleteCategoryAction(this, taskService));
        return m;
    }
    private JMenu createTaskMenu() {
        JMenu m = new JMenu("Task");
        m.add(new AddTaskAction(this));
        m.add(new EditTaskAction(this));
        m.addSeparator();
        m.add(new DeleteTaskAction(this, taskService));
        m.addSeparator();
        m.add(new ToggleCompletedAction(this));
        m.addSeparator();
        m.add(new ResetOrderAction(this));
        return m;
    }

    // Utilitare interne:
    private void applyFilter() {
        String t = filterField.getText().trim().toLowerCase();
        if (t.isEmpty()) refreshTreeAll();
        else taskTableModel.setTasks(
            getAllTasks().stream()
               .filter(x->x.getTitle().toLowerCase().contains(t)
                       || x.getDescription().toLowerCase().contains(t))
               .toList()
        );
    }
    private void showTasks(List<Task> tasks) {
        taskTableModel.setTasks(tasks); // Actualizează tabelul cu lista dată
    }
    private List<Task> getAllTasks() {
        // Colectează toate task-urile din toate categoriile
        return taskService.getAllRoots().stream()
            .flatMap(r->r.getCategories().stream())
            .flatMap(c->c.getTasks().stream())
            .toList();
    }
    private List<Task> getTasksFromRoot(RootGroup r) {
        // Colectează task-urile dintr-un root
        return r.getCategories().stream()
            .flatMap(c->c.getTasks().stream())
            .toList();
    }
    
    // Returnează root-ul selectat în arbore (sau null)
    public RootGroup getSelectedRoot() {
        TreePath p = categoryTree.getSelectionPath();
        if (p == null) return null;
        Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
        return (o instanceof RootGroup) ? (RootGroup)o : null;
    }

    // Returnează categoria selectată în arbore (sau null)
    public Category getSelectedCategory() {
        TreePath p = categoryTree.getSelectionPath();
        if (p == null) return null;
        Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
        return (o instanceof Category) ? (Category)o : null;
    }

    // Returnează task-ul selectat în arbore (sau null)
    public Task getSelectedTask() {
        TreePath p = categoryTree.getSelectionPath();
        if (p == null) return null;
        Object o = ((DefaultMutableTreeNode)p.getLastPathComponent()).getUserObject();
        return (o instanceof Task) ? (Task)o : null;
    }
    
    // Expandează toate nodurile din arbore
    public void expandAll() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < categoryTree.getRowCount(); i++) {
                categoryTree.expandRow(i);
            }
        });
    }
    
    public JTree getCategoryTree() {
        return categoryTree; // Acces la arbore din exterior
    }
}

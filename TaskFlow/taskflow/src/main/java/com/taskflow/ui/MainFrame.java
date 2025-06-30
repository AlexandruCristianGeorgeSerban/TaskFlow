package com.taskflow.ui;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private final List<RootGroup> allRoots;
    private TaskTableModel taskTableModel;
    private CategoryTreeModel categoryTreeModel;
    private JTable taskTable;
    private JTree categoryTree;
    private JTextField filterField;
    private List<Task> masterTasks;

    public MainFrame(List<RootGroup> roots) {
        super("TaskFlow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        this.allRoots = new ArrayList<>(roots);
        
        taskTableModel = new TaskTableModel(getAllTasks());
        masterTasks = new ArrayList<>(getAllTasks());
        categoryTreeModel = new CategoryTreeModel(allRoots);
        
        taskTable = new JTable(taskTableModel);
        TableRowSorter<TaskTableModel> sorter = new TableRowSorter<>(taskTableModel);
        taskTable.setRowSorter(sorter);

        categoryTree = new JTree(categoryTreeModel);
        categoryTree.setRootVisible(false);
        categoryTree.setShowsRootHandles(true);
        categoryTree.setCellRenderer(new ColorTreeCellRenderer());
        
        categoryTree.addTreeSelectionListener(e -> {
        	TreePath path = e.getNewLeadSelectionPath();
            if (path == null) return;
            	Object node = ((DefaultMutableTreeNode)path.getLastPathComponent())
            			.getUserObject();
            	if (node instanceof RootGroup) {
            		showTasks(getTasksFromRoot((RootGroup) node));
            	} else if (node instanceof Category) {
            		showTasks(((Category) node).getTasks());
            	}
        	});
        
        filterField = new JTextField(15);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(categoryTree), new JScrollPane(taskTable));
        splitPane.setDividerLocation(200);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterField);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createRootMenu());
        menuBar.add(createCategoryMenu());
        menuBar.add(createTaskMenu());
        setJMenuBar(menuBar);

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        categoryTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = categoryTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        Object node = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        if (node instanceof Task) {
                        	showTasks(List.of((Task) node));
                        }
                    }
                }
            }
        });
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");

        fileMenu.add(new AbstractAction("Import XML") {
            @Override public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        List<Category> cats = new XmlSaxHandler().loadCategories(file);
                        RootGroup imported = new RootGroup("Imported", "#0077CC");
                        imported.getCategories().addAll(cats);
                        allRoots.clear();
                        allRoots.add(imported);
                        refreshTree();
                    } catch (Exception ex) {
                        showError(ex);
                    }
                }
            }
        });

        fileMenu.add(new AbstractAction("Export XML") {
            @Override public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        List<Category> cats = allRoots.stream()
                            .flatMap(rg -> rg.getCategories().stream())
                            .toList();
                        XmlDomHandler.saveCategories(cats, file);
                    } catch (Exception ex) {
                        showError(ex);
                    }
                }
            }
        });

        fileMenu.addSeparator();

        fileMenu.add(new AbstractAction("Delete File/Folder…") {
            @Override public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (chooser.showDialog(MainFrame.this, "Delete") == JFileChooser.APPROVE_OPTION) {
                    File sel = chooser.getSelectedFile();
                    if (!sel.exists()) {
                        JOptionPane.showMessageDialog(
                            MainFrame.this,
                            "Path does not exist:\n" + sel.getAbsolutePath(),
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        int conf = JOptionPane.showConfirmDialog(
                            MainFrame.this,
                            "Are you sure you want to delete?\n" + sel.getAbsolutePath(),
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (conf == JOptionPane.YES_OPTION) {
                            if (deleteRecursively(sel)) {
                                JOptionPane.showMessageDialog(
                                    MainFrame.this,
                                    "Deleted:\n" + sel.getAbsolutePath(),
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                            } else {
                                JOptionPane.showMessageDialog(
                                    MainFrame.this,
                                    "Failed to delete:\n" + sel.getAbsolutePath(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    }
                }
            }
        });

        return fileMenu;
    }

    private boolean deleteRecursively(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) {
                    if (!deleteRecursively(c)) return false;
                }
            }
        }
        return f.delete();
    }

    private JMenu createRootMenu() {
        JMenu rootMenu = new JMenu("Root");
        rootMenu.add(new AbstractAction("Add Root") {
            @Override public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                    MainFrame.this, "Root name:");
                if (name == null || name.trim().isEmpty()) return;

                Color chosen = JColorChooser.showDialog(
                    MainFrame.this, "Choose root color", Color.BLACK);
                String colorHex = "#000000";
                if (chosen != null) {
                    colorHex = String.format(
                        "#%02x%02x%02x",
                        chosen.getRed(), chosen.getGreen(), chosen.getBlue()
                    );
                }

                RootGroup rg = new RootGroup(name.trim(), colorHex);
                allRoots.add(rg);
                refreshTree();
            }
        });
        
        rootMenu.addSeparator();
        rootMenu.add(new AbstractAction("Edit Root") {
            @Override public void actionPerformed(ActionEvent e) {
                TreePath sel = categoryTree.getSelectionPath();
                if (sel == null) return;
                Object obj = ((DefaultMutableTreeNode) sel.getLastPathComponent()).getUserObject();
                if (!(obj instanceof RootGroup)) return;
                RootGroup rg = (RootGroup) obj;

                String newName = JOptionPane.showInputDialog(
                    MainFrame.this, "Root name:", rg.getName());
                if (newName == null || newName.trim().isEmpty()) return;
                Color chosen = JColorChooser.showDialog(
                    MainFrame.this, "Choose root color", Color.decode(rg.getColorHex()));
                String newColor = (chosen == null)
                    ? rg.getColorHex()
                    : String.format("#%02x%02x%02x",
                        chosen.getRed(), chosen.getGreen(), chosen.getBlue());

                rg.setName(newName.trim());
                rg.setColorHex(newColor);
                refreshTree();
            }
        });
        return rootMenu;
    }

    private JMenu createCategoryMenu() {
        JMenu categoryMenu = new JMenu("Category");
        categoryMenu.add(new AbstractAction("Add Category") {
            @Override public void actionPerformed(ActionEvent e) {
                TreePath sel = categoryTree.getSelectionPath();
                if (sel == null) {
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Select a root to add a category.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
                Object obj = ((DefaultMutableTreeNode) sel.getLastPathComponent())
                                  .getUserObject();
                if (!(obj instanceof RootGroup)) {
                    JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Please select a root.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }
                RootGroup rg = (RootGroup) obj;

                String name = JOptionPane.showInputDialog(
                    MainFrame.this, "Category name:");
                if (name == null || name.trim().isEmpty()) return;

                Color chosen = JColorChooser.showDialog(
                    MainFrame.this, "Choose category color", Color.BLUE);
                String colorHex = "#0000FF";
                if (chosen != null) {
                    colorHex = String.format(
                        "#%02x%02x%02x",
                        chosen.getRed(), chosen.getGreen(), chosen.getBlue()
                    );
                }

                Category cat = new Category(name.trim(), colorHex);
                rg.getCategories().add(cat);
                refreshTree();
            }
        });
        
        categoryMenu.addSeparator();
        categoryMenu.add(new AbstractAction("Edit Category") {
            @Override public void actionPerformed(ActionEvent e) {
                TreePath sel = categoryTree.getSelectionPath();
                if (sel == null) return;
                Object obj = ((DefaultMutableTreeNode) sel.getLastPathComponent()).getUserObject();
                if (!(obj instanceof Category)) return;
                Category cat = (Category) obj;

                String newName = JOptionPane.showInputDialog(
                    MainFrame.this, "Category name:", cat.getName());
                if (newName == null || newName.trim().isEmpty()) return;
                Color chosen = JColorChooser.showDialog(
                    MainFrame.this, "Choose category color", Color.decode(cat.getColor()));
                String newColor = (chosen == null)
                    ? cat.getColor()
                    : String.format("#%02x%02x%02x",
                        chosen.getRed(), chosen.getGreen(), chosen.getBlue());

                cat.setName(newName.trim());
                cat.setColor(newColor);
                refreshTree();
            }
        });
        return categoryMenu;
    }

    private JMenu createTaskMenu() {
        JMenu taskMenu = new JMenu("Task");

        taskMenu.add(new AbstractAction("Add Task") {
            @Override public void actionPerformed(ActionEvent e) {
                addTaskDialog();
            }
        });

        taskMenu.add(new AbstractAction("Edit Task") {
            @Override public void actionPerformed(ActionEvent e) {
                editTaskDialog();
            }
        });

        taskMenu.addSeparator();

        taskMenu.add(new AbstractAction("Toggle Completed") {
            @Override public void actionPerformed(ActionEvent e) {
                TreePath sel = categoryTree.getSelectionPath();
                if (sel == null) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Select a task to toggle completion.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                Object obj = ((DefaultMutableTreeNode) sel.getLastPathComponent()).getUserObject();
                if (!(obj instanceof Task)) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Please select a task node.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                Task t = (Task) obj;
                t.setCompleted(!t.isCompleted());
                refreshTree();
                Category cat = (Category)((DefaultMutableTreeNode)sel.getParentPath().getLastPathComponent()).getUserObject();
                taskTableModel.setTasks(getTasksFromCategory(cat));
            }
        });
        taskTable.getTableHeader().setReorderingAllowed(true);
        
        taskMenu.addSeparator();
        taskMenu.add(new AbstractAction("Reset Order") {
            @Override public void actionPerformed(ActionEvent e) {
                TableRowSorter<?> sorter = (TableRowSorter<?>) taskTable.getRowSorter();
                sorter.setSortKeys(null);

                taskTableModel.setTasks(new ArrayList<>(masterTasks));
            }
        });
        return taskMenu;
    }

    private void applyFilter() {
        String text = filterField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            taskTableModel.setTasks(getAllTasks());
            refreshTree();
        } else {
            List<Task> filtered = getAllTasks().stream()
                    .filter(t -> t.getTitle().toLowerCase().contains(text)
                            || t.getDescription().toLowerCase().contains(text))
                    .collect(Collectors.toList());
            taskTableModel.setTasks(filtered);
        }
    }

    private List<Task> getAllTasks() {
        return allRoots.stream()
                .flatMap(rg -> rg.getCategories().stream())
                .flatMap(c -> c.getTasks().stream())
                .toList();
    }

    private List<Task> getTasksFromRoot(RootGroup rg) {
        return rg.getCategories().stream()
                .flatMap(c -> c.getTasks().stream())
                .toList();
    }

    private List<Task> getTasksFromCategory(Category cat) {
        return new ArrayList<>(cat.getTasks());
    }

    private void refreshTree() {
        categoryTreeModel.updateRoots(allRoots);
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < categoryTree.getRowCount(); i++) {
                categoryTree.expandRow(i);
            }
        });
    }

    private void showTasks(List<Task> tasks) {
        taskTableModel.setTasks(tasks);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void addTaskDialog() {
        TreePath sel = categoryTree.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(this,
                "Select a category to add a task.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object obj = ((DefaultMutableTreeNode) sel.getLastPathComponent()).getUserObject();
        if (!(obj instanceof Category)) {
            JOptionPane.showMessageDialog(this,
                "Please select a category node.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Category cat = (Category) obj;

        String title = JOptionPane.showInputDialog(this, "Task title:");
        if (title == null || title.trim().isEmpty()) return;
        String desc = JOptionPane.showInputDialog(this, "Task description:");
        if (desc == null) desc = "";

        String dueStr = JOptionPane.showInputDialog(this,
        	    "Due date (yyyy-MM-dd or yyyyMMdd):");
        	Calendar dueDate;
        	try {
        	    Date d;
        	    if (dueStr.contains("-")) {
        	        d = new SimpleDateFormat("yyyy-MM-dd").parse(dueStr);
        	    } else if (dueStr.length() == 8) {
        	        d = new SimpleDateFormat("yyyyMMdd").parse(dueStr);
        	    } else {
        	        throw new ParseException("Format invalid", 0);
        	    }
        	    dueDate = Calendar.getInstance();
        	    dueDate.setTime(d);
        	} catch (ParseException ex) {
        	    JOptionPane.showMessageDialog(this,
        	        "Invalid date format.\nUse yyyy-MM-dd or yyyyMMdd",
        	        "Error", JOptionPane.ERROR_MESSAGE);
        	    return;
        	}

        Color chosen = JColorChooser.showDialog(
            this, "Choose task color", Color.RED);
        String colorHex = "#A00000";
        if (chosen != null) {
            colorHex = String.format(
                "#%02x%02x%02x",
                chosen.getRed(), chosen.getGreen(), chosen.getBlue());
        }

        Task t = new Task(
            title.trim(),
            desc.trim(),
            dueDate,
            false,
            colorHex
        );
        cat.addTask(t);

        refreshTree();
        taskTableModel.setTasks(getTasksFromCategory(cat));
    }
    
    private void editTaskDialog() {
        TreePath sel = categoryTree.getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(this,
                "Select a task node to edit.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object obj = ((DefaultMutableTreeNode) sel.getLastPathComponent()).getUserObject();
        if (!(obj instanceof Task)) {
            JOptionPane.showMessageDialog(this,
                "Please select a task node.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Task t = (Task) obj;

        String newTitle = JOptionPane.showInputDialog(
            this, "Task title:", t.getTitle());
        if (newTitle == null || newTitle.trim().isEmpty()) return;

        String newDesc = JOptionPane.showInputDialog(
            this, "Task description:", t.getDescription());
        if (newDesc == null) newDesc = "";

        String dueStr = JOptionPane.showInputDialog(
            this, "Due date (yyyy-MM-dd or yyyyMMdd):",
            new SimpleDateFormat("yyyy-MM-dd")
                .format(t.getDueDate().getTime()));
        Calendar dueDate;
        try {
            Date d;
            if (dueStr.contains("-")) {
                d = new SimpleDateFormat("yyyy-MM-dd").parse(dueStr);
            } else {
                d = new SimpleDateFormat("yyyyMMdd").parse(dueStr);
            }
            dueDate = Calendar.getInstance();
            dueDate.setTime(d);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format.\nUse yyyy-MM-dd or yyyyMMdd",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Color chosen = JColorChooser.showDialog(
            this, "Choose task color", Color.decode(t.getColorHex()));
        String newColor = (chosen == null)
            ? t.getColorHex()
            : String.format("#%02x%02x%02x",
                chosen.getRed(), chosen.getGreen(), chosen.getBlue());

        t.setTitle(newTitle.trim());
        t.setDescription(newDesc.trim());
        t.setDueDate(dueDate);
        t.setColorHex(newColor);

        refreshTree();
        Category cat = (Category)
            ((DefaultMutableTreeNode) sel.getParentPath()
                 .getLastPathComponent()).getUserObject();
        taskTableModel.setTasks(getTasksFromCategory(cat));
    }
}

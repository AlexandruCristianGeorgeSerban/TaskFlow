package com.taskflow.ui;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * TransferHandler care permite drag & drop în JTree pentru:
 * - reordonarea RootGroup-urilor,
 * - mutarea Category între RootGroup-uri,
 * - mutarea Task între Category.
 */
public class TreeTransferHandler extends TransferHandler {
    private static final long serialVersionUID = 1L;
    private final DataFlavor nodeFlavor; // Formatul de transfer
    private final TaskService svc;      // Serviciu pentru actualizări
    private final MainFrame frame;      // Fereastra principală

    public TreeTransferHandler(MainFrame frame, TaskService svc) {
        this.frame = frame;
        this.svc   = svc;
        // Definim DataFlavor-ul pentru obiecte Java locale
        nodeFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreeNodeArray");
    }

    @Override
    public boolean canImport(TransferSupport support) {
        // Verifică că e un drop și că suportă DataFlavor-ul nostru
        if (!support.isDrop() || !support.isDataFlavorSupported(nodeFlavor)) {
            return false;
        }
        // Determină nodul țintă
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath dest = dl.getPath();
        if (dest == null) return false;
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) dest.getLastPathComponent();
        Object targetObj = targetNode.getUserObject();

        try {
            // Obține nodurile mutate
            TreePath[] paths = (TreePath[]) support.getTransferable().getTransferData(nodeFlavor);
            // Validăm fiecare nod tras
            for (TreePath tp : paths) {
                DefaultMutableTreeNode draggedNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
                Object draggedObj = draggedNode.getUserObject();
                // 1) RootGroup poate fi mutat doar la nivel root (reordonare)
                if (draggedObj instanceof RootGroup) {
                    if (!(targetObj instanceof RootGroup) && targetNode.getParent() != frame.getCategoryTree().getModel().getRoot()) {
                        return false;
                    }
                }
                // 2) Category poate fi mutată doar sub un RootGroup
                else if (draggedObj instanceof Category) {
                    if (!(targetObj instanceof RootGroup)) {
                        return false;
                    }
                }
                // 3) Task poate fi mutat doar sub o Category
                else if (draggedObj instanceof Task) {
                    if (!(targetObj instanceof Category)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (UnsupportedFlavorException | IOException ex) {
            return false;
        }
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        // Obține calea nodurilor selectate
        TreePath[] paths = ((JTree) c).getSelectionPaths();
        return new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{ nodeFlavor };
            }
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(nodeFlavor);
            }
            public Object getTransferData(DataFlavor flavor) {
                return paths;
            }
        };
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE; // Operație de mutare
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) return false;
        try {
            // Obține nodurile mutate și locația de drop
            TreePath[] draggedPaths = (TreePath[]) support.getTransferable().getTransferData(nodeFlavor);
            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            TreePath dropPath = dl.getPath();

            DefaultTreeModel model = (DefaultTreeModel) frame.getCategoryTree().getModel();
            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
            Object targetObj = targetNode.getUserObject();

            for (TreePath dragPath : draggedPaths) {
                DefaultMutableTreeNode draggedNode = (DefaultMutableTreeNode) dragPath.getLastPathComponent();
                Object draggedObj = draggedNode.getUserObject();
                DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode) draggedNode.getParent();

                // Elimină nodul din vechea poziție
                model.removeNodeFromParent(draggedNode);

                // 1) Reordonare RootGroup
                if (draggedObj instanceof RootGroup && targetObj instanceof RootGroup) {
                    int idx = targetNode.getParent().getIndex(targetNode);
                    model.insertNodeInto(draggedNode, (MutableTreeNode) targetNode.getParent(), idx + 1);
                    svc.moveRoot((RootGroup) draggedObj, idx + 1);
                }
                // 2) Mută Category între RootGroup-uri
                else if (draggedObj instanceof Category && targetObj instanceof RootGroup) {
                    RootGroup oldR = (RootGroup) oldParent.getUserObject();
                    RootGroup newR = (RootGroup) targetObj;
                    svc.removeCategory(oldR, (Category) draggedObj);
                    svc.addCategory(newR, (Category) draggedObj);
                    model.insertNodeInto(draggedNode, targetNode, targetNode.getChildCount());
                }
                // 3) Mută Task între Category
                else if (draggedObj instanceof Task && targetObj instanceof Category) {
                    Category oldC = (Category) oldParent.getUserObject();
                    Category newC = (Category) targetObj;
                    svc.removeTask(oldC, (Task) draggedObj);
                    svc.addTask(newC, (Task) draggedObj);
                    model.insertNodeInto(draggedNode, targetNode, targetNode.getChildCount());
                }
            }

            // Reîmprospătare completă a UI-ului
            frame.refreshTreeAll();
            return true;
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

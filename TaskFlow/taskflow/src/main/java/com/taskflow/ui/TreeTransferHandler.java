package com.taskflow.ui;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class TreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;

    private final DataFlavor nodeFlavor;
    private final TaskService svc;
    private final MainFrame frame;

    public TreeTransferHandler(MainFrame frame, TaskService svc) {
        this.frame = frame;
        this.svc   = svc;
        nodeFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreeNodeArray");
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop() || !support.isDataFlavorSupported(nodeFlavor)) {
            return false;
        }
        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
        TreePath dest = dl.getPath();
        if (dest == null) return false;
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode)dest.getLastPathComponent();
        Object targetObj = targetNode.getUserObject();

        try {
            TreePath[] paths = (TreePath[])support.getTransferable()
                                     .getTransferData(nodeFlavor);
            for (TreePath tp : paths) {
                DefaultMutableTreeNode draggedNode = (DefaultMutableTreeNode)tp.getLastPathComponent();
                Object draggedObj = draggedNode.getUserObject();
                if (draggedObj instanceof RootGroup) {
                    if (!(targetObj instanceof RootGroup) && targetNode.getParent() != frame.getCategoryTree().getModel().getRoot()) {
                        return false;
                    }
                }
                else if (draggedObj instanceof Category) {
                    if (!(targetObj instanceof RootGroup)) {
                        return false;
                    }
                }
                else if (draggedObj instanceof Task) {
                    if (!(targetObj instanceof Category)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (UnsupportedFlavorException|IOException ex) {
            return false;
        }
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        TreePath[] paths = ((JTree)c).getSelectionPaths();
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
        return MOVE;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) return false;

        try {
            TreePath[] draggedPaths = (TreePath[])support.getTransferable()
                                    .getTransferData(nodeFlavor);
            JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
            TreePath dropPath = dl.getPath();

            DefaultTreeModel model = (DefaultTreeModel) frame.getCategoryTree().getModel();
            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode)dropPath.getLastPathComponent();
            Object targetObj = targetNode.getUserObject();

            for (TreePath dragPath : draggedPaths) {
                DefaultMutableTreeNode draggedNode = (DefaultMutableTreeNode)dragPath.getLastPathComponent();
                Object draggedObj = draggedNode.getUserObject();
                DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode)draggedNode.getParent();
                model.removeNodeFromParent(draggedNode);
                if (draggedObj instanceof RootGroup && targetObj instanceof RootGroup) {
                    int idx = targetNode.getParent().getIndex(targetNode);
                    model.insertNodeInto(draggedNode, (MutableTreeNode)targetNode.getParent(), idx+1);
                    svc.moveRoot((RootGroup)draggedObj, idx+1);
                }
                else if (draggedObj instanceof Category && targetObj instanceof RootGroup) {
                    RootGroup oldR = (RootGroup)oldParent.getUserObject();
                    RootGroup newR = (RootGroup)targetObj;
                    svc.removeCategory(oldR, (Category)draggedObj);
                    svc.addCategory(newR, (Category)draggedObj);
                    model.insertNodeInto(draggedNode, targetNode, targetNode.getChildCount());
                }
                else if (draggedObj instanceof Task && targetObj instanceof Category) {
                    Category oldC = (Category)oldParent.getUserObject();
                    Category newC = (Category)targetObj;
                    svc.removeTask(oldC, (Task)draggedObj);
                    svc.addTask(newC, (Task)draggedObj);
                    model.insertNodeInto(draggedNode, targetNode, targetNode.getChildCount());
                }
            }
            frame.refreshTreeAll();
            return true;

        } catch (UnsupportedFlavorException|IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

package com.taskflow.ui;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

// Renderer personalizat pentru JTree care colorează nodurile în funcție de tip și culoare.
public class ColorTreeCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 12; // Dimensiunea iconiței

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // Obține obiectul asociat nodului
        Object obj = ((DefaultMutableTreeNode) value).getUserObject();
        String hex = "#000000"; // Culoare implicită neagră

        if (obj instanceof RootGroup) {
            RootGroup rg = (RootGroup) obj;
            hex = rg.getColorHex(); // Culoarea root-ului
            setText(rg.getName());
        } else if (obj instanceof Category) {
            Category cat = (Category) obj;
            hex = cat.getColor(); // Culoarea categoriei
            setText(cat.getName());
        } else if (obj instanceof Task) {
            Task task = (Task) obj;
            hex = task.getColorHex(); // Culoarea task-ului
            setText(task.getTitle());
        }

        // Normalizează formatul hex
        final String colorHex = hex.trim().startsWith("#") ? hex.trim() : "#" + hex.trim();

        // Setează iconița colorată în funcție de tipul obiectului
        setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Color col;
                try {
                    col = Color.decode(colorHex); // Decodează culoarea hex
                } catch (NumberFormatException ex) {
                    col = Color.BLACK; // Fallback negru
                }
                g.setColor(col);

                if (obj instanceof RootGroup) {
                    // Pătrat
                    g.fillRect(x, y, SIZE, SIZE);

                } else if (obj instanceof Category) {
                    // Cerc
                    g.fillOval(x, y, SIZE, SIZE);

                } else if (obj instanceof Task) {
                    // Triunghi
                    int[] xs = { x, x + SIZE / 2, x + SIZE };
                    int[] ys = { y + SIZE, y, y + SIZE };
                    g.fillPolygon(xs, ys, 3);

                } else {
                    // Fallback: cerc mic
                    g.fillOval(x + SIZE/4, y + SIZE/4, SIZE/2, SIZE/2);
                }
            }

            @Override public int getIconWidth()  { return SIZE; }
            @Override public int getIconHeight() { return SIZE; }
        });

        return this;
    }
}

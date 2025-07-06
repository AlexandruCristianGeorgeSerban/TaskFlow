package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.RootGroup;
import com.taskflow.service.TaskService;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Acțiune care afișează un dialog pentru adăugarea sau editarea unei categorii
 * Utilizează GridBagLayout pentru a permite introducerea numelui și a culorii categoriei
 */
public class CategoryDialogAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private final MainFrame frame; // Fereastra principală pentru afișarea dialogului
    private final TaskService svc; // Serviciul care gestionează operațiile pe categorii
    private final boolean isNew;   // True pentru creare, false pentru editare

    public CategoryDialogAction(MainFrame frame, TaskService svc, boolean isNew) {
        super(isNew ? "Add Category" : "Edit Category"); // Textul acțiunii afișat în meniu
        this.frame = frame;
        this.svc   = svc;
        this.isNew = isNew;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RootGroup rg;
        Category cat;

        if (isNew) {
            // Pentru adăugare, trebuie selectat un root existent
            rg = frame.getSelectedRoot();
            if (rg == null) {
                JOptionPane.showMessageDialog(frame,
                    "Select a root first to add a category.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            cat = null;
        } else {
            // Pentru editare, trebuie selectată o categorie
            cat = frame.getSelectedCategory();
            if (cat == null) {
                JOptionPane.showMessageDialog(frame,
                    "Select a category to edit.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Determinăm root-ul părinte al categoriei selectate
            rg = frame.getTaskService().getAllRoots().stream()
                .filter(r -> r.getCategories().contains(cat))
                .findFirst()
                .orElse(null);
        }

        // Setăm valorile inițiale în câmpurile de editare
        String initName  = isNew ? "" : cat.getName();
        Color  initColor = isNew
            ? Color.BLUE
            : Color.decode(cat.getColor());

        // Configurăm JTextArea pentru nume cu scroll orizontal
        JTextArea nameField = new JTextArea(initName, 1, 30);
        nameField.setLineWrap(false);
        JScrollPane nameScroll = new JScrollPane(nameField,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        nameScroll.setPreferredSize(new Dimension(300, 40));

        // Configurăm label pentru selecția culorii
        JLabel colorSwatch = new JLabel();
        colorSwatch.setOpaque(true);
        colorSwatch.setBackground(initColor);
        colorSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        colorSwatch.setPreferredSize(new Dimension(300,36));
        colorSwatch.setFocusable(false);
        colorSwatch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorSwatch.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                // La click, afișează JColorChooser pentru alegerea culorii
                Color color = JColorChooser.showDialog(
                    frame, "Choose category color", colorSwatch.getBackground());
                if (color != null) colorSwatch.setBackground(color);
            }
        });

        // Construim panel-ul de dialog folosind GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx=0; gc.gridy=0; panel.add(new JLabel("Name:"), gc);
        gc.gridx=1;            panel.add(nameScroll, gc);

        gc.gridx=0; gc.gridy=1; panel.add(new JLabel("Color:"), gc);
        gc.gridx=1;            panel.add(colorSwatch, gc);

        // Loop de validare: verifică input-ul până este corect
        while (true) {
            int ans = JOptionPane.showConfirmDialog(
                frame, panel,
                isNew ? "Add Category" : "Edit Category",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (ans != JOptionPane.OK_OPTION) {
                // Dacă utilizatorul anulează, ieșim fără modificări
                return;
            }

            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                // Numele nu poate fi gol
                JOptionPane.showMessageDialog(frame,
                    "Name cannot be empty.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Convertim culoarea în format hexazecimal
            String hex = String.format("#%02x%02x%02x",
                colorSwatch.getBackground().getRed(),
                colorSwatch.getBackground().getGreen(),
                colorSwatch.getBackground().getBlue()
            );

            if (isNew) {
                // Adaugă o categorie nouă sub root-ul selectat
                svc.addCategory(rg, new Category(name, hex));
            } else {
                // Actualizează categoria existentă
                cat.setName(name);
                cat.setColor(hex);
                svc.updateCategory(cat);
            }

            frame.refreshTreeAll(); // Reîmprospătăm arborele după schimbări
            break;
        }
    }
}

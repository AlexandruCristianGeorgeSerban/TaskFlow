package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.Task;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Acțiune care afișează un dialog pentru adăugarea sau editarea unui task
 * Permite introducerea titlului, descrierii, datei limită și culorii task-ului
 */
public class TaskDialogAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    private final MainFrame frame; // Fereastra principală în care se va afișa dialogul
    private final boolean isNew;   // Indică dacă dialogul este pentru creare (true) sau editare (false)

    public TaskDialogAction(MainFrame frame, boolean isNew) {
        super(isNew ? "Add Task" : "Edit Task"); // Textul acțiunii în meniu
        this.frame = frame;
        this.isNew = isNew;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 1) Determinăm categoria țintă pentru task
        Category cat = frame.getSelectedCategory();
        Task task = null;
        if (!isNew) {
            // Dacă edităm, obținem task-ul selectat
            task = frame.getSelectedTask();
            if (task != null && cat == null) {
                // Dacă nu avem categoria, o căutăm în serviciu
                for (Category c : frame.getTaskService()
                                        .getAllRoots().stream()
                                        .flatMap(r -> r.getCategories().stream())
                                        .toList()) {
                    if (c.getTasks().contains(task)) {
                        cat = c;
                        break;
                    }
                }
            }
        }
        if (cat == null) {
            // Dacă nu a fost selectat niciună categorie/task valid, informăm utilizatorul
            JOptionPane.showMessageDialog(frame,
                isNew
                  ? "Select a category first to add a task."
                  : "Select a task (or its category) first to edit.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 2) Pregătim valorile inițiale pentru câmpuri
        String initTitle = task == null ? "" : task.getTitle();
        String initDesc  = task == null ? "" : task.getDescription();
        String initDate  = "";
        Color  initColor = Color.RED;
        if (task != null) {
            initDate  = new SimpleDateFormat("yyyy-MM-dd")
                            .format(task.getDueDate().getTime()); // Formatăm data curentă
            initColor = Color.decode(task.getColorHex());           // Culoarea curentă
        }

        // Creăm câmpurile de intrare și le configurăm o singură dată
        JTextArea titleField = new JTextArea(initTitle, 1, 30);
        titleField.setLineWrap(false);
        JScrollPane titleScroll = new JScrollPane(titleField,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        titleScroll.setPreferredSize(new Dimension(300, 40));

        JTextArea descField = new JTextArea(initDesc, 4, 30);
        descField.setLineWrap(false);
        JScrollPane descScroll = new JScrollPane(descField,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        descScroll.setPreferredSize(new Dimension(300, 40));

        JTextArea dateField = new JTextArea(initDate, 1, 15);
        dateField.setLineWrap(false);
        JScrollPane dateScroll = new JScrollPane(dateField,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dateScroll.setPreferredSize(new Dimension(300, 40));

        // Label care afișează culoarea și permite alegerea ei
        JLabel colorSwatch = new JLabel();
        colorSwatch.setOpaque(true);
        colorSwatch.setBackground(initColor);
        colorSwatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        colorSwatch.setPreferredSize(new Dimension(300, 36));
        colorSwatch.setFocusable(false);
        colorSwatch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorSwatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                // La click, afișăm dialogul pentru alegerea culorii
                Color c = JColorChooser.showDialog(
                    frame, "Choose task color", colorSwatch.getBackground());
                if (c != null) colorSwatch.setBackground(c);
            }
        });

        // Construim panel-ul cu GridBagLayout pentru aranjarea câmpurilor
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);             // Margini între componente
        gc.anchor = GridBagConstraints.WEST;          // Aliniere la stânga

        gc.gridx=0; gc.gridy=0; panel.add(new JLabel("Title:"), gc);
        gc.gridx=1;            panel.add(titleScroll, gc);

        gc.gridx=0; gc.gridy=1; panel.add(new JLabel("Description:"), gc);
        gc.gridx=1;            panel.add(descScroll, gc);

        gc.gridx=0; gc.gridy=2; panel.add(
            new JLabel("Due Date (yyyy-MM-dd or yyyyMMdd):"), gc);
        gc.gridx=1;            panel.add(dateScroll, gc);

        gc.gridx=0; gc.gridy=3; panel.add(new JLabel("Color:"), gc);
        gc.gridx=1;            panel.add(colorSwatch, gc);

        // 3) Loop de validare și confirmare a datelor
        while (true) {
            int ans = JOptionPane.showConfirmDialog(
                frame, panel,
                isNew ? "Add Task" : "Edit Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (ans != JOptionPane.OK_OPTION) {
                // Dacă utilizatorul a apăsat Cancel, ieșim fără a salva
                return;
            }

            // Citim valorile introduse
            String title = titleField.getText().trim();
            String desc  = descField.getText().trim();
            String in    = dateField.getText().trim();
            Calendar due = Calendar.getInstance();
            try {
                Date d;
                if (in.contains("-")) {
                    // Parsăm data în formatul cu cratime
                    d = new SimpleDateFormat("yyyy-MM-dd").parse(in);
                } else {
                    // Parsăm data în formatul compact
                    d = new SimpleDateFormat("yyyyMMdd").parse(in);
                }
                due.setTime(d);
            } catch (ParseException ex) {
                // Dacă data nu este validă, afișăm eroare și rămânem în loop
                JOptionPane.showMessageDialog(frame,
                    "Invalid date. Use yyyy-MM-dd or yyyyMMdd",
                    "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Construim codul culorii în hexazecimal
            String hex = String.format("#%02x%02x%02x",
                colorSwatch.getBackground().getRed(),
                colorSwatch.getBackground().getGreen(),
                colorSwatch.getBackground().getBlue()
            );

            // Aplicăm operațiunea de creare sau actualizare task
            if (isNew) {
                Task t = new Task(title, desc, due, false, hex);
                frame.getTaskService().addTask(cat, t); // Adăugăm task nou
            } else {
                task.setTitle(title);
                task.setDescription(desc);
                task.setDueDate(due);
                task.setColorHex(hex);
                frame.getTaskService().updateTask(task); // Actualizăm task existent
            }
            frame.refreshAfterChange(cat); // Reîmprospătăm UI după schimbări
            break; // Datele sunt valide, ieșim din loop
        }
    }
}

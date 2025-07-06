package com.taskflow.action;

import com.taskflow.ui.MainFrame;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;

// Resetează ordinea task-urilor în tabel la cea implicită.
public class ResetOrderAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației

	public ResetOrderAction(MainFrame frame) {
		super("Reset Order"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 1) Elimină orice sortare aplicată pe tabel
		TableRowSorter<?> sorter = (TableRowSorter<?>) frame.getTaskTable().getRowSorter();
		sorter.setSortKeys(null);

		// 2) Reîmprospătează arborele și tabelul cu toate task-urile actuale
        frame.refreshTreeAll();
	}
}

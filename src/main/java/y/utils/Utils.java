/*
	This file is part of yAmbElf.

	yAmbElf is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	yAmbElf is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with yAmbElf.  If not, see <http://www.gnu.org/licenses/>.
	
	Copyright 2014 Miro Salvagni
*/

package y.utils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;



public class Utils
{
	public static String formatDoubleAsNeeded(double value)
	{
		return new DecimalFormat("#.##").format(value).replace(',', '.');
	}
	
	/**
	 * Visualizza una finestra di messaggio
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 */
	public static void MessageBox(String text, String title)
	{
		MessageBox(text, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Visualizza una finestra di messaggio
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 * @param type tipo della finestra di dialogo, si trovano in JOptionPane, es: JOptionPane.WARNING_MESSAGE
	 */
	public static void MessageBox(String text, String title, int type)
	{
		JOptionPane.showMessageDialog(null, text, title, type);
	}
	
	
	/**
	 * Centra la finestra nello schermo
	 * @param form
	 */
	public static void centerWindow(JFrame form)
	{
		Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window_dim = form.getSize();
		
		int posX = (screen_dim.width - window_dim.width) / 2;
		int posY = (screen_dim.height- window_dim.height) / 2;
		
		form.setLocation(posX, posY);
	}
	
	public static void jtable_adjustRowSizes(JTable jTable) {
        for (int row = 0; row < jTable.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < jTable.getColumnCount(); column++) {
                TableCellRenderer cellRenderer = jTable.getCellRenderer(row, column);
                Object valueAt = jTable.getValueAt(row, column);
                Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(jTable, valueAt, false, false, row, column);
                int heightPreferable = tableCellRendererComponent.getPreferredSize().height;
                maxHeight = Math.max(heightPreferable, maxHeight);
            }
            jTable.setRowHeight(row, maxHeight);
        }
    }

    public static void jtable_adjustColumnSizes(JTable table, int column, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(column);
        int width;

        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null)
            renderer = table.getTableHeader().getDefaultRenderer();

        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, column);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
            int currentWidth = comp.getPreferredSize().width;
            width = Math.max(width, currentWidth);
        }

        width += 2 * margin;

        col.setPreferredWidth(width);
        col.setWidth(width);
    }

	public static String openFileDialog(String dialog_title, Component component, String description, String... extensions)
	{
		return openFileDialog(Config.getInstance().getLastUsedFolder(), dialog_title, component, description, extensions);
	}
	
	public static String openFileDialog(String lastUsedFolder, String dialog_title, Component component, String description, String... extensions)
	{
		JFileChooser chooser = new JFileChooser(lastUsedFolder);
		chooser.setDialogTitle(dialog_title);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
		chooser.setFileFilter(filter);
		
		if (chooser.showOpenDialog(component) == JFileChooser.APPROVE_OPTION) {
			Config.getInstance().setLastUsedFolder(chooser.getSelectedFile().getParent());
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return  "";
	}
	
	
	public static String[] openMultipleFileDialog(String dialog_title, Component component, String description, String... extensions)
	{
		return openMultipleFileDialog(Config.getInstance().getLastUsedFolder(), dialog_title, component, description, extensions);
	}
	
	public static String[] openMultipleFileDialog(String lastUsedFolder, String dialog_title, Component component, String description, String... extensions)
	{
		JFileChooser chooser = new JFileChooser(lastUsedFolder);
		chooser.setDialogTitle(dialog_title);
		chooser.setMultiSelectionEnabled(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
		chooser.setFileFilter(filter);
		
		if (chooser.showOpenDialog(component) == JFileChooser.APPROVE_OPTION) {
			Config.getInstance().setLastUsedFolder(chooser.getSelectedFile().getParent());
			
			final File[] selFiles = chooser.getSelectedFiles();
			
			String[] ret = new String[selFiles.length];
			for (int i=0; i<ret.length; i++)
				ret[i] = selFiles[i].getAbsolutePath();
			
			return ret;
		}

		return  new String[0];
	}
	
	public static String saveFileDialog(String dialog_title, Component component, String description, String... extensions)
	{
		return openFileDialog(Config.getInstance().getLastUsedFolder(), dialog_title, component, description, extensions);
	}
	
	public static String saveFileDialog(String lastUsedFolder, String dialog_title, Component component, String description, String... extensions)
	{
		JFileChooser chooser = new JFileChooser(lastUsedFolder);
		chooser.setDialogTitle(dialog_title);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
		chooser.setFileFilter(filter);
		
		if (chooser.showSaveDialog(component) == JFileChooser.APPROVE_OPTION) {
			Config.getInstance().setLastUsedFolder(chooser.getSelectedFile().getParent());
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return  "";
	}
	
	/**
	 * Chiede all'utente una string
	 * @param title titolo della finestra
	 * @return la stringa immessa, o null se l'utente ha cliccato Cancel
	 */
	public static String MessageBoxString(String title)
	{
		return JOptionPane.showInputDialog(title);
	}
	
	
	public static String[] MessageBoxStrings(String title)
	{
		return MessageBoxStrings(title, ",");
	}
	
	public static String[] MessageBoxStrings(String title, String separator)
	{
		String input = Utils.MessageBoxString(title);
		if (input == null)
			return null;
		
		String[] fields = input.split(separator);
		for (int i=0; i<fields.length; i++)
			fields[i] = fields[i].trim();
		
		return fields;
	}
	
	/**
	 * Visualizza una finestra di messaggio per chiedere all'utente { Si, No }
	 * @param window la finestra da cui si visualizza
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 * @return true se l'utente ha selezionato si
	 */
	public static boolean MessageBoxYesNo(Component window, String text, String title)
	{
		return JOptionPane.showConfirmDialog(window, text, title, JOptionPane.YES_NO_OPTION) == 0;
	}
	
	public static boolean abortOnExistingAndDontOverwrite(String filename)
	{
		return (new File(filename).exists() && Utils.MessageBoxYesNo(null, filename+" "+"MsgOverwrite", "TitleOverwrite") == false);
	}
}


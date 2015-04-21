package y.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import org.joda.time.DateTime;

import y.elf.CurrentDb;
import y.elf.CurrentValue;
import y.elf.MeasurementValue;
import y.elf.MeasurementDb.PeriodType;
import y.utils.Config;
import y.utils.Utils;

public class PanelCurrents  extends PanelYEM {
	
	private static final long serialVersionUID = 4266466765100538182L;
	
	private String selectedDir;
	
	private FileBrowserList curFilelist;
	private JButton loadButton;
	private JSpinner daSpinner;
	private JSpinner aSpinner;
	private JComboBox<String> comboPeriod;
	
	private JTextField folderText;
	private JButton folderChange;
	private JTextField folderFilter;
	
	private JButton selAll;
	private JButton selNone;
	private JButton selAuto;
	
	private String[] columnNames;
	private JTable table;
	
	public PanelCurrents()
	{
		super();
		
		initialize();
	}
	
	private void initialize() {
		selectedDir = "";
		
		curFilelist = new FileBrowserList();
		
		loadButton = new JButton(Config.getResource("MsgGo"));
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadFiles();
			}
		});

		final ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				applyFilters();
			}
		};
		final DocumentListener dl = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				applyFileListSelectionFilter();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				applyFileListSelectionFilter();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				applyFileListSelectionFilter();
			}
		};

		daSpinner = PanelELF.createDateSpinner();
		aSpinner = PanelELF.createDateSpinner();
		comboPeriod = new JComboBox<String>(new String[] { Config.getResource("ItemDaily"), Config.getResource("ItemWeekly"), Config.getResource("ItemMonthly"), Config.getResource("ItemYearly")});
		comboPeriod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyFilters();
			}
		});
		
		daSpinner.addChangeListener(cl);
		aSpinner.addChangeListener(cl);

		this.setLayout(new GridLayout(0,2));
		
		
		JPanel westUp = new JPanel();
		westUp.setLayout(new GridLayout(2,0));
		
		
		JPanel westUpN = new JPanel();
		westUpN.setLayout(new BorderLayout());
		JPanel westUpS = new JPanel();
		westUpS.setLayout(new BorderLayout());
		
		folderText = new JTextField();
		folderText.setEditable(false);
		folderFilter = new JTextField();
		folderFilter.getDocument().addDocumentListener(dl);
		folderChange = new JButton("select");
		folderChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				if (fc.showOpenDialog(folderChange) != JFileChooser.APPROVE_OPTION)
					return;
				
				selectedDir = fc.getSelectedFile().getAbsolutePath();
				folderText.setText(selectedDir);
				curFilelist.setPath(selectedDir);
			}
		});
		
		JPanel westUpSE = new JPanel();
		westUpSE.setLayout(new GridLayout(0,3));
		selAll = new JButton("All");
		selAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curFilelist.selectAll();
			}
		});
		
		selNone = new JButton("None");
		selNone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curFilelist.selectNone();
			}
		});
		selAuto = new JButton("Auto");
		selAuto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyFileListSelectionFilter();
			}
		});
		
		westUpSE.add(selAll);
		westUpSE.add(selNone);
		westUpSE.add(selAuto);
		
		westUpN.add(new JLabel(" Folder:"), BorderLayout.WEST);
		westUpN.add(folderText, BorderLayout.CENTER);
		westUpN.add(folderChange, BorderLayout.EAST);
		
		westUpS.add(new JLabel(" Site:"), BorderLayout.WEST);
		westUpS.add(folderFilter, BorderLayout.CENTER);
		westUpS.add(westUpSE, BorderLayout.EAST);
		
		westUp.add(westUpN);
		westUp.add(westUpS);
		
		JPanel west = new JPanel();
		west.setLayout(new BorderLayout());
		
		curFilelist.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleCurr")));
		
		west.add(westUp, BorderLayout.NORTH);
		west.add(loadButton, BorderLayout.SOUTH);
		west.add(curFilelist, BorderLayout.CENTER);

		this.add(west);
		
		JPanel eastUp = new JPanel();
		eastUp.setLayout(new GridLayout(0,2));
		
		eastUp.add(new JLabel(" "+Config.getResource("MsgFrom")+":"));
		eastUp.add(daSpinner);
		eastUp.add(new JLabel(" "+Config.getResource("MsgTo")+":"));
		eastUp.add(aSpinner);
		eastUp.add(new JLabel(" "+Config.getResource("MsgPeriod")+":"));
		eastUp.add(comboPeriod);
		
		
		JPanel east = new JPanel();
		east.setLayout(new BorderLayout());
		east.add(eastUp, BorderLayout.NORTH);
		this.add(east);
		
		
		JPanel eastS = new JPanel();
		eastS.setLayout(new GridLayout(0, 2));
		
		JButton xlsButton = new JButton(Config.getResource("MsgSaveXLS"));
		xlsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportXLS();
			}
		});
		eastS.add(xlsButton);
		
		JButton graphButton = new JButton(Config.getResource("MsgSaveChart"));
		graphButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportGraph();
			}
		});
		eastS.add(graphButton);
		east.add(eastS, BorderLayout.SOUTH);
		
		columnNames = Config.getInstance().getDataColumnNames();
		table = new JTable(new Object[0][0], columnNames);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		for (int i=0; i<table.getColumnCount(); i++)
			Utils.jtable_adjustColumnSizes(table, i, 1);
		Utils.jtable_adjustRowSizes(table);
		JScrollPane tableScroller = new JScrollPane(table);
		east.add(tableScroller, BorderLayout.CENTER);
		
		masterDB = null;
		filteredDB = null;
	}

	
	private CurrentDb masterDB;
	private CurrentDb filteredDB;
	
	private synchronized void applyFileListSelectionFilter() {
		curFilelist.select(folderFilter.getText());
	}

	
	private void loadFiles() {
		
		final Config config = Config.getInstance();
		
		final CurrentDb newdb = CurrentDb.load(curFilelist.getSelectedFilenames(), config.getCurrentValuefieldn(), config);
		if (newdb.size() == 0)
			Utils.MessageBox(Config.getResource("MsgEmptyDbAfterRead"), Config.getResource("TitleWarning"));
		
		if (newdb != null) {
			filteredDB = masterDB = null; // prevent change listeners from fire events
			try { daSpinner.setValue(newdb.getStartDate().toDate()); } catch (Exception e) {}
			try { aSpinner.setValue(newdb.getEndDate().toDate()); } catch (Exception e) {}
			filteredDB = masterDB = newdb;
			applyFilters();
//			refreshTable();
		}
	}
	
	private PeriodType getPeriodType() {
		final String selectedText = (String) comboPeriod.getSelectedItem();
		if (selectedText == null)
			return PeriodType.DAILY;
		else if (selectedText.equals(Config.getResource("ItemFromTo")))
			return PeriodType.DAILY;
		else if (selectedText.equals(Config.getResource("ItemDaily")))
			return PeriodType.DAILY;
		else if (selectedText.equals(Config.getResource("ItemWeekly")))
			return PeriodType.WEEKLY;
		else if (selectedText.equals(Config.getResource("ItemMonthly")))
			return PeriodType.MONTHLY;
		else if (selectedText.equals(Config.getResource("ItemYearly")))
			return PeriodType.YEARLY;
		
		return PeriodType.DAILY; // unreachable
	}
	
	
	private synchronized void applyFilters() {
		if (masterDB != null)
			try {
				final DateTime from = new DateTime((Date) daSpinner.getValue());
				final DateTime to = new DateTime((Date) aSpinner.getValue());
				
				filteredDB = masterDB.filter(from, to,getPeriodType(), Config.getInstance().getOperationCurrent());	// apply last division/performed operation
				refreshTable();
			}
			catch (Exception e) {}
	}
	
	private void refreshTable()
	{
		table.setModel(new TableModelCurrents(filteredDB));
		table.clearSelection();
		
		boolean[] valuevalid = filteredDB.getOpValid();
		
		for (int i=0; i<valuevalid.length+2; i++)
			if (i >= valuevalid.length || valuevalid[i])
				table.addRowSelectionInterval(i, i);
	}
	
	public class TableModelCurrents extends AbstractTableModel
	{
		private static final long serialVersionUID = 7804380016977209351L;
	    private Object[][] data;
	    
	    public TableModelCurrents(CurrentDb db)
	    {
	    	final DateTime[] times = db.getPeriods();
	    	final int[] mediane = db.getOpValues();
	    	final int[] maxs = db.getOpMaxDay();
	    	final int[] count = db.getOpValueCount();
	    	
	    	this.data = new Object[times.length+2][];
	    	
	    	for (int i=0; i<times.length; i++) {
	    		data[i] = new Object[4];
	    		data[i][0] = Utils.toDateString(times[i]);
	    		data[i][1] = ((double)mediane[i]/100);
	    		data[i][2] = ((double)maxs[i]/100);
	    		data[i][3] = count[i];
	    	}
	    	
	    	final int maxi = db.getMaxidx();
	    	data[data.length-2] = new Object[4];
	    	data[data.length-2][0] = Config.getResource("MsgMax")+"("+db.getOperationPerformed().getName()+") - "+Utils.toDateString(times[maxi]);
    		data[data.length-2][1] = MeasurementValue.valueIntToDouble(mediane[maxi]);
    		data[data.length-2][2] = MeasurementValue.valueIntToDouble(maxs[maxi]);
    		data[data.length-2][3] = count[maxi];

    		final CurrentValue maxvalue = db.getSelectedCurrentValue(new Comparator<CurrentValue>() {
				@Override
				public int compare(CurrentValue o1, CurrentValue o2) {
					return o1.getValue()-o2.getValue();
				}
    		});
	    	data[data.length-1] = new Object[4];
	    	data[data.length-1][0] = Config.getResource("MsgMax")+"("+Utils.toDateString(maxvalue.getTime())+")";
    		data[data.length-1][1] = MeasurementValue.valueIntToDouble(maxvalue.getValue());
    		data[data.length-1][2] = "";
    		data[data.length-1][3] = 1;
	    }
	    
	    public int getColumnCount() 
	    {
	        return columnNames.length;
	    }
	    public String getColumnName(int column) 
	    {
	        return columnNames[column];
	    }
	    public int getRowCount() 
	    {
	        return data.length;
	    }
	    public Object getValueAt( int row, int column ) 
	    {
	        return data[row][column];
	    }
	    
	    public boolean isCellEditable(int row, int col)
        { return false; }
	}
	
	public void exportXLS()
	{
		String path = Utils.openFileDialog(Config.getResource("MsgSelectFile"), this, Config.getResource("TitleXLSFile"), "xlsx");
		if (path.isEmpty())
			return;
		
		if (!path.toLowerCase().endsWith("xls") && !path.toLowerCase().endsWith("xlsx"))
			path += ".xlsx";
		
		try {
//			XLSHelper.saveCorrentiSingole(path, filteredDB);	// TODO: exportXLS()
		}
		catch (Exception e) {
			Utils.MessageBox(Config.getResource("MsgErrorXlsx")+"\n"+e.getMessage(), Config.getResource("TitleErrorXlsx"));
		}
	}
	
	public void exportGraph()
	{
		// TODO: exportGraph()
	}

	
	public class FileBrowserList extends JPanel
	{
		private static final long serialVersionUID = -4154283294966066872L;

		private String path;
		private JList<String> filelist;
		private DefaultListModel<String> filelistModel;

		public FileBrowserList()
		{
			super();
			
			path = "";

			filelistModel = new DefaultListModel<String>();
			filelist = new JList<String>(filelistModel);
			filelist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			filelist.setLayoutOrientation(JList.VERTICAL);
			filelist.setVisibleRowCount(-1);
			JScrollPane filelistCentralineScroller = new JScrollPane(filelist);
			
			this.setLayout(new BorderLayout());
			this.add(filelistCentralineScroller, BorderLayout.CENTER);
		}
	
		public void setPath(String path)
		{
			clear();
			this.path = path;
			try {
				final File d = new File(path);
				final File[] files = d.listFiles();
				
				for (File f : files)
					if (f.isFile())
						filelistModel.addElement(f.getName());
			}
			catch (Exception e) {
				clear();
			}
		}
		
		public String getPath() { return path; }
		
		public void clear()
		{
			path = "";
			selectNone();
			filelistModel.clear();
		}
		
		public String[] getAllFilenames() {
			final String[] filenames = new String[filelistModel.size()];
			for (int i=0; i<filenames.length; i++)
				filenames[i] = (String) filelistModel.get(i);
			
			return filenames;
		}
		
		public String[] getSelectedFilenames() {
			final int[] selectedIx = filelist.getSelectedIndices();
			
			final String[] filenames = new String[selectedIx.length];
			for (int i=0; i<selectedIx.length; i++)
				filenames[i] = selectedDir + "/" + (String) filelistModel.get(selectedIx[i]);
			
			return filenames;
		}
		
		public void selectNone() {
			filelist.clearSelection();
		}

		public void selectAll() {
			final int end = filelistModel.getSize() - 1;
			if (end >= 0)
				filelist.setSelectionInterval(0, 0);
			else
				selectNone();
		}

		public void select(String text) {
			text = text.toLowerCase();
			selectNone();
			final ArrayList<Integer> listSel = new ArrayList<Integer>();
			
			for (int i=0, sz = filelistModel.size(); i<sz; i++) {
				final String itemi = (String) filelistModel.get(i);
				if (itemi.toLowerCase().contains(text))
					listSel.add(i);
			}
			
			final int[] indices = new int[listSel.size()];
			for (int i=0; i<indices.length; i++)
				indices[i] = listSel.get(i);
			
			filelist.setSelectedIndices(indices);
		}
	}
	
	
	
	@Override
	public PanelType getType() {
		return PanelType.Corrente;
	}
}

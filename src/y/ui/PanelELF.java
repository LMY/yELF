package y.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import y.elf.ElfDb;
import y.elf.TimeValue;
import y.elf.MeasurementDb.PeriodType;
import y.graphs.ChartHelperELF;
import y.graphs.XLSHelper;
import y.utils.Config;
import y.utils.Utils;

public class PanelELF extends PanelYEM
{
	private static final long serialVersionUID = -5095632256220648308L;
	
	private JTable table;

	private JButton go;
	private JButton save;
	private JButton saveGfx;
	
	private JSpinner daSpinner;
	private JSpinner aSpinner;

	private ElfDb masterValues;
	private ElfDb filteredValues;

	private FileList filelist;
	
	private static final String spinnerFormat = "d/M/y HH:mm";
	
	private String[] columnNames;
	
	public PanelELF()
	{
		super();
		
		initialize();
	}
	
	private void initialize() {
		columnNames = Config.getInstance().getDataColumnNames();
	
		go = new JButton(Config.getResource("MsgGo"));
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				go();
			}
		});
		
		save = new JButton(Config.getResource("MsgSaveXLS"));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveXLS();
			}
		});
		saveGfx = new JButton(Config.getResource("MsgSaveChart"));
		saveGfx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveGfx();
			}
		});
		
		table = new JTable(new Object[0][0], columnNames);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		for (int i=0; i<table.getColumnCount(); i++)
			Utils.jtable_adjustColumnSizes(table, i, 1);
		Utils.jtable_adjustRowSizes(table);
		
		daSpinner = createDateSpinner();
		aSpinner = createDateSpinner();
		final ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				applyFilters();
			}
		};
		daSpinner.addChangeListener(cl);
		aSpinner.addChangeListener(cl);
		
		JPanel instrPanel = new JPanel();
		instrPanel.setLayout(new BorderLayout());
		
		JPanel west = new JPanel();
		west.setLayout(new BorderLayout());

		JPanel east = new JPanel();
		east.setLayout(new BorderLayout());
		
		filelist = new FileList();
		
		west.add(instrPanel , BorderLayout.NORTH);
		west.add(filelist, BorderLayout.CENTER);
		west.add(go, BorderLayout.SOUTH);
		
		JPanel eastDown = new JPanel();
		eastDown.setLayout(new FlowLayout());
		eastDown.add(save);
		eastDown.add(saveGfx);

		JPanel eastUp = new JPanel();
		eastUp.setLayout(new GridLayout(0,2));
		
		eastUp.add(new JLabel(" "+Config.getResource("MsgFrom")+":"));
		eastUp.add(daSpinner);
		eastUp.add(new JLabel(" "+Config.getResource("MsgTo")+":"));
		eastUp.add(aSpinner);

		east.add(eastUp, BorderLayout.NORTH);
		JScrollPane tableScroller = new JScrollPane(table);
		
		JPanel centraline = new JPanel();
		centraline.setLayout(new BorderLayout());
		centraline.add(tableScroller, BorderLayout.CENTER);
		centraline.add(eastDown, BorderLayout.SOUTH);
		
		east.add(centraline, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		
		this.setLayout(new GridLayout(1,2));
		this.add(west);
		this.add(east);
	}

	@Override
	public PanelType getType() {
		return PanelType.ELF;
	}

	public static JSpinner createDateSpinner()
	{
		JSpinner theSpinner = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(theSpinner, spinnerFormat);
		theSpinner.setEditor(timeEditor);
		theSpinner.setValue(new Date());
		return theSpinner;
	}
	
	public void go()
	{
		final int valuefieldn = Config.getInstance().getElfValuefieldn();
		final int low = Config.getInstance().getInstrumentLowELF();
		final ElfDb newdb = ElfDb.load(filelist.getFilenames(), valuefieldn, low);
		newdb.perform(PeriodType.DAILY, Config.getInstance().getOperationELF());
		newdb.clearRaw();	// resample won't be needed after perform()

		if (newdb != null) {
			filteredValues = masterValues = null; // prevent change listeners from fire events
			try { daSpinner.setValue(newdb.getStartDate().toDate()); } catch (Exception e) {}
			try { aSpinner.setValue(newdb.getEndDate().toDate()); } catch (Exception e) {}
			filteredValues = masterValues = newdb;
		
			refreshTable();
		}
	}
	
	private void refreshTable()
	{
		table.setModel(new TableModel(filteredValues));
		table.clearSelection();
		
		boolean[] valuevalid = filteredValues.getOpValid();
		
		for (int i=0; i<valuevalid.length+1; i++)
			if (i == valuevalid.length || valuevalid[i])
				table.addRowSelectionInterval(i, i);
	}
	
	private void applyFilters()
	{
		if (masterValues != null)
			try {
				final TimeValue from = new TimeValue((Date) daSpinner.getValue());
				final TimeValue to = new TimeValue((Date) aSpinner.getValue());
				filteredValues = masterValues.filter(from, to);
				refreshTable();
			}
			catch (Exception e) {}
	}
	
	public class TableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 7804380016977209351L;
	    private Object[][] data;
	    
	    public TableModel(ElfDb db)
	    {
	    	final TimeValue[] times = db.getDays();
	    	final int[] mediane = db.getOpValues();
	    	final int[] maxs = db.getOpMaxDay();
	    	final int[] count = db.getOpValueCount();
	    	
	    	this.data = new Object[times.length+1][];
	    	
	    	for (int i=0; i<times.length; i++) {
	    		data[i] = new Object[4];
	    		data[i][0] = times[i].toDateString();
	    		data[i][1] = ((double)mediane[i]/100);
	    		data[i][2] = ((double)maxs[i]/100);
	    		data[i][3] = count[i];
	    	}
	    	
	    	final int maxi = db.getMaxidx();
	    	data[data.length-1] = new Object[4];
	    	data[data.length-1][0] = Config.getResource("MsgMax")+"("+times[maxi].toDateString()+")";
    		data[data.length-1][1] = ((double)mediane[maxi]/100);
    		data[data.length-1][2] = ((double)maxs[maxi]/100);
    		data[data.length-1][3] = count[maxi];
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
	
	public boolean saveXLS()
	{
		if (filteredValues == null)
			return false;
		
		String path = Utils.saveFileDialog(Config.getResource("MsgWhereToSaveData"), this, Config.getResource("MsgSelectFile"), "xlsx");
		if (path.isEmpty())
			return false;
		if (!path.contains("."))
			path += ".xlsx";
		
		return XLSHelper.saveData(path, filteredValues.getSampledData(), filteredValues.getOpValues(), filteredValues.getOpMaxDay(), filteredValues.getOpValueCount(), Config.getInstance().getInstrumentLowELF(), false);
	}
	
	public boolean saveGfx()
	{
		if (filteredValues == null)
			return false;

		String path = Utils.saveFileDialog(Config.getResource("MsgWhereToSaveImage"), this, Config.getResource("MsgSelectFile"), "png");
		if (path.isEmpty())
			return false;
		if (!path.contains("."))
			path += ".png";
		
		final TimeValue from = new TimeValue((Date) daSpinner.getValue());
		final TimeValue to = new TimeValue((Date) aSpinner.getValue());
	
		return ChartHelperELF.saveData(path, filteredValues.getSampledData(), filteredValues.getOpValues(), filteredValues.getOpValid(), filteredValues.getMaxidx(), from, to,
				Config.getInstance(), Config.getInstance().getConst_values().get("elf"));
	}
}

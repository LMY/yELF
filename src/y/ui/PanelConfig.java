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

package y.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import say.swing.JFontChooser;
import y.elf.CurrentValue;
import y.elf.ElfValue;
import y.elf.datafunctions.DataFunction;
import y.utils.ColorEditor;
import y.utils.Config;
import y.utils.ConfigSerie;
import y.utils.Utils;


public class PanelConfig extends JPanel
{
	private static final long serialVersionUID = 1292648709049384278L;

	public static void openWindow()
	{
		JFrame newform = new JFrame(Config.getResource("TitleConfig"));
		newform.add(new PanelConfig());
		newform.pack();
		Utils.centerWindow(newform);
		newform.setVisible(true);
	}
	
	private JComboBox comboLanguage;
	
	private JButton buttonSave;
	private JButton buttonReload;

	private JTextField pictureWidth;
	private JTextField pictureHeight;
	
	private JTextField minDataCoverage100;
	private JTextField elfValueFieldn;
	private JTextField srbValueFieldn;
	private JTextField currentValueFieldn;
	
	private JTextField graphYmin;
	private JTextField graphYmax;
	
	private JTextField legendSize;
	private JTextField legendX;
	private JTextField legendY;
	private JTextField instrumentLowELF;
	private JTextField instrumentLowSRB;
	private JTextField instrumentLowCurrent;
	private JTextField axisFormat;
	
	private JButton titleFont;
	
	private JTable srbSeries;
	private JTable emSeries;
	
	private JComboBox comboOpSRB;
	private JComboBox comboOpELF;
	
	
	private String[] configColumnNames;
	private final String[] avaibleLanguages = { "Italiano", "English" };
	
	public PanelConfig()
	{
		super();
		
		configColumnNames = Config.getInstance().getConfigColumnNames();
		
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new GridLayout(0, 2));
		
		comboLanguage = new JComboBox(avaibleLanguages);
		
		comboOpSRB = new JComboBox(DataFunction.getNames());
		comboOpELF = new JComboBox(DataFunction.getNames());
		
		buttonSave = new JButton(Config.getResource("TitleSave"));
		buttonReload = new JButton(Config.getResource("TitleReload"));
		buttonSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Save();
			}
		});
		buttonReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Reload();
			}
		});
		
		pictureWidth = new JTextField();
		pictureHeight = new JTextField();
		minDataCoverage100 = new JTextField();
		elfValueFieldn = new JTextField();
		srbValueFieldn = new JTextField();
		currentValueFieldn = new JTextField();
		
		graphYmin = new JTextField();
		graphYmax = new JTextField();
		
		legendSize = new JTextField();
		legendX = new JTextField();
		legendY = new JTextField();
		instrumentLowELF = new JTextField();
		instrumentLowSRB = new JTextField();
		instrumentLowCurrent = new JTextField();
		axisFormat = new JTextField();
		
		titleFont = new JButton(Config.getResource("TitleSelect"));
		titleFont.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseFont();
			}
		});
		
		final TableCellRenderer weirdRenderer = new ColorRenderer();
		
		srbSeries = new JTable(new Object[0][0], configColumnNames) {
			private static final long serialVersionUID = 123456789L;
			public TableCellRenderer getCellRenderer(int row, int column) { return column==1 ? weirdRenderer : super.getCellRenderer(row, column); }
		};
		srbSeries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		srbSeries.setRowSelectionAllowed(true);
		srbSeries.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i=0; i<srbSeries.getColumnCount(); i++)
			Utils.jtable_adjustColumnSizes(srbSeries, i, 1);
		Utils.jtable_adjustRowSizes(srbSeries);
		srbSeries.setModel(new TableModel(Config.getInstance().getConst_values().get("srb")));
		srbSeries.clearSelection();
		srbSeries.setDefaultEditor(Color.class, new ColorEditor());
		
		emSeries = new JTable(new Object[0][0], configColumnNames) {
			private static final long serialVersionUID = 123456788L;
			public TableCellRenderer getCellRenderer(int row, int column) { return column==1 ? weirdRenderer : super.getCellRenderer(row, column); }
		};
		emSeries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		emSeries.setRowSelectionAllowed(true);
		emSeries.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i=0; i<emSeries.getColumnCount(); i++)
			Utils.jtable_adjustColumnSizes(emSeries, i, 1);
		Utils.jtable_adjustRowSizes(emSeries);		
		emSeries.setModel(new TableModel(Config.getInstance().getConst_values().get("elf")));
		emSeries.clearSelection();
		emSeries.setDefaultEditor(Color.class, new ColorEditor());
		
		setColumnAsConfigSerieUsages(srbSeries, 2);
		setColumnAsConfigSerieUsages(emSeries, 2);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitleLanguage")));
		mainpanel.add(comboLanguage);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitleMinDataCoverage")));
		mainpanel.add(minDataCoverage100);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitlePicWidth")));
		mainpanel.add(pictureWidth);
		mainpanel.add(new JLabel(" "+Config.getResource("TitlePicHeight")));
		mainpanel.add(pictureHeight);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitleCurrentFieldn")));
		mainpanel.add(currentValueFieldn);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitleYrangeMin")));
		mainpanel.add(graphYmin);
		mainpanel.add(new JLabel(" "+Config.getResource("TitleYrangeMax")));
		mainpanel.add(graphYmax);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitleLegendSize")));
		mainpanel.add(legendSize);
		mainpanel.add(new JLabel(" "+Config.getResource("TitleLegendX")));
		mainpanel.add(legendX);
		mainpanel.add(new JLabel(" "+Config.getResource("TitleLegendY")));
		mainpanel.add(legendY);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitleInstrumentalLowCurrent")));
		mainpanel.add(instrumentLowCurrent);
		
		mainpanel.add(new JLabel(" "+Config.getResource("TitleXAxisFormat")));
		
		JPanel axisPanel = new JPanel();
		axisPanel.setLayout(new BorderLayout());
		JButton axisButton = new JButton(Config.getResource("TitleDefault"));
		axisButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				axisFormat.setText(Config.DEFAULT_TIMEFMT);
			}
		});
		
		axisPanel.add(axisFormat, BorderLayout.CENTER);
		axisPanel.add(axisButton, BorderLayout.EAST);
		mainpanel.add(axisPanel);
		mainpanel.add(new JLabel(" "+Config.getResource("TitleTitleFont")));
		mainpanel.add(titleFont);
		
		this.setLayout(new BorderLayout());
		this.add(mainpanel, BorderLayout.NORTH);
		
		JPanel tablePanel = new JPanel();
		tablePanel.setLayout(new GridLayout(0, 2));
		
		JPanel t1 = new JPanel();
		t1.setLayout(new BorderLayout());
		t1.add(new JScrollPane(srbSeries), BorderLayout.CENTER);
		t1.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleSerieSrb")));
		
		JButton srbBnew = new JButton(Config.getResource("MsgAdd"));
		srbBnew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((TableModel)srbSeries.getModel()).newLine();
			}
		});
		JButton srbBup = new JButton(Config.getResource("MsgUp"));
		srbBup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int[] selected = srbSeries.getSelectedRows();
				((TableModel)srbSeries.getModel()).move( selected, true );
				if (selected.length > 0)
					srbSeries.setRowSelectionInterval(selected[selected.length-1]-1, selected[selected.length-1]-1);
			}
		});		
		JButton srbBdown = new JButton(Config.getResource("MsgDown"));
		srbBdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int[] selected = srbSeries.getSelectedRows();
				((TableModel)srbSeries.getModel()).move( selected, false );
				if (selected.length > 0)
					srbSeries.setRowSelectionInterval(selected[selected.length-1]+1, selected[selected.length-1]+1);
			}
		});		
		JButton srbBdel = new JButton(Config.getResource("MsgDel"));
		srbBdel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((TableModel)srbSeries.getModel()).delLines( srbSeries.getSelectedRows() );
			}
		});
		JButton srbDefault = new JButton(Config.getResource("MsgRestoreDefaults"));
		srbDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Utils.MessageBoxYesNo(null, Config.getResource("MsgRestoreSRBDefaults"), Config.getResource("TitleRestoreSRBDefaults")))
					srbSeries.setModel(new TableModel(Config.createDefaultConstValuesSRB()) );
			}
		});
		JPanel t1x = new JPanel();
		t1x.setLayout(new FlowLayout());
		t1x.add(srbBnew);
		t1x.add(srbBup);
		t1x.add(srbBdown);
		t1x.add(srbBdel);
		t1x.add(srbDefault);
		t1.add(t1x, BorderLayout.SOUTH);
		
		JPanel t1up = new JPanel();
		t1up.setLayout(new GridLayout(0, 2));
		t1up.add(new JLabel(" "+Config.getResource("TitleSrbFieldn")));
		t1up.add(srbValueFieldn);
		t1up.add(new JLabel(" "+Config.getResource("TitleInstrumentalLowSRB")));
		t1up.add(instrumentLowSRB);
		t1up.add(new JLabel(" "+Config.getResource("TitleOperationTypeSRB")));
		t1up.add(comboOpSRB);
		t1.add(t1up, BorderLayout.NORTH);
		
		
		JPanel t2 = new JPanel();
		t2.setLayout(new BorderLayout());
		t2.add(new JScrollPane(emSeries), BorderLayout.CENTER);
		t2.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleSerieElf")));
		
		
		
		JButton elfBnew = new JButton(Config.getResource("MsgAdd"));
		elfBnew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((TableModel)emSeries.getModel()).newLine();
			}
		});
		JButton elfBup = new JButton(Config.getResource("MsgUp"));
		elfBup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int[] selected = emSeries.getSelectedRows();
				((TableModel)emSeries.getModel()).move( selected, true );
				if (selected.length > 0)
					emSeries.setRowSelectionInterval(selected[selected.length-1]-1, selected[selected.length-1]-1);
			}
		});		
		JButton elfBdown = new JButton(Config.getResource("MsgDown"));
		elfBdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int[] selected = emSeries.getSelectedRows();
				((TableModel)emSeries.getModel()).move( selected, false );
				if (selected.length > 0)
					emSeries.setRowSelectionInterval(selected[selected.length-1]+1, selected[selected.length-1]+1);
			}
		});		
		JButton elfBdel = new JButton(Config.getResource("MsgDel"));
		elfBdel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((TableModel)emSeries.getModel()).delLines( emSeries.getSelectedRows() );
			}
		});
		JButton elfDefault = new JButton(Config.getResource("MsgRestoreDefaults"));
		elfDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Utils.MessageBoxYesNo(null, Config.getResource("MsgRestoreELFDefaults"), Config.getResource("TitleRestoreELFDefaults")))
					emSeries.setModel(new TableModel(Config.createDefaultConstValuesELF()) );
			}
		});
		JPanel t2x = new JPanel();
		t2x.setLayout(new FlowLayout());
		t2x.add(elfBnew);
		t2x.add(elfBup);
		t2x.add(elfBdown);
		t2x.add(elfBdel);
		t2x.add(elfDefault);
		t2.add(t2x, BorderLayout.SOUTH);
		
		JPanel t2up = new JPanel();
		t2up.setLayout(new GridLayout(0, 2));
		t2up.add(new JLabel(" "+Config.getResource("TitleElfFieldn")));
		t2up.add(elfValueFieldn);
		t2up.add(new JLabel(" "+Config.getResource("TitleInstrumentalLowELF")));
		t2up.add(instrumentLowELF);
		t2up.add(new JLabel(" "+Config.getResource("TitleOperationTypeELF")));
		t2up.add(comboOpELF);
		t2.add(t2up, BorderLayout.NORTH);
		
		tablePanel.add(t1);
		tablePanel.add(t2);
		this.add(tablePanel, BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(1, 2));
		btnPanel.add(buttonSave);
		btnPanel.add(buttonReload);
		this.add(btnPanel, BorderLayout.SOUTH);
		
		Refresh();
	}
	
    private void setColumnAsConfigSerieUsages(JTable table, int i) {
		TableColumn theColumn = table.getColumnModel().getColumn(i);
		JComboBox comboBox = new JComboBox();
		for (String s : ConfigSerie.USAGES)
			comboBox.addItem(s);
		theColumn.setCellEditor(new DefaultCellEditor(comboBox));
	}


    public class TableModel extends AbstractTableModel
	{
		private static final long serialVersionUID = 7804380016977209351L;
	    private ArrayList<ConfigSerie> data;
	    
	    public TableModel(ArrayList<ConfigSerie> series)
	    {
	    	this.data = series;
		}
	    
	    public int getColumnCount() 
	    {
	        return configColumnNames.length;
	    }
	    public String getColumnName(int column) 
	    {
	        return configColumnNames[column];
	    }
	    public int getRowCount() 
	    {
	        return data.size();
	    }
	    public Object getValueAt(int row, int column) 
	    {
	        final ConfigSerie cs = data.get(row);
	        switch (column) {
		        case 0: return (Object) cs.getName();
		        case 1: return (Object) cs.getColor();
		        case 2: return (Object) ConfigSerie.USAGES[cs.getUsage()];
		        case 3: return (Object) ElfValue.valueIntToDouble(cs.getValue());
		        case 4: return (Object) cs.isDrawLine();
		        case 5: return (Object) cs.getShapeSize();
		        default: return null;
	        }
	    }
	    
	    public boolean isCellEditable(int row, int col)
        { return true; }

	    // questa serve ad avere le checkbox per drawLines e drawShapes
	    @SuppressWarnings({ "rawtypes", "unchecked" })
		public Class getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	    }
	    
	    public void setValueAt(Object value, int row, int col) {
	    	
	    	ConfigSerie selected = data.get(row);
	    	
	    	switch (col) {
	    	case 0:
	    		selected.setName((String)value);
	    		break;
	    	case 1:
	    		selected.setColor((Color) value);
	    		break;
	    	case 2:
	    		selected.setUsage(ConfigSerie.usageStringToInt((String) value));
	    		break;
	    	case 3:
	    		selected.setValue(ElfValue.valueDoubleToInt((Double)value));
	    		break;
	    	case 4:
	    		selected.setDrawLine(!selected.isDrawLine());
	    		break;
	    	case 5:
	    		try {
	    			final float v = (Float)value;
	    			if (v < 0) throw new Exception();
	    			selected.setShapeSize(v);
	    		}
	    		catch (Exception e) { }
	    		break;
	    	}
	    	
	        fireTableCellUpdated(row, col);
	    }
	    
	    public void newLine() {
	    	data.add(new ConfigSerie());
			fireTableDataChanged();
	    }
	    
	    public void delLines(int[] l) {
	    	Arrays.sort(l);
	    	for (int i=l.length-1; i>=0; i--)
	    		data.remove(l[i]);
	    	if (l.length > 0)
	    		fireTableDataChanged();
	    }
	    
	    public void move(int[] x, boolean up) {
	    	Arrays.sort(x);
	    	boolean doupdate=false;
	    	
	    	for (int a : x)
	    		if ((up && a > 0) || (!up && a < data.size()-1)) {
	    			final ConfigSerie cs = data.get(a);
	    			data.remove(a);
	    			data.add(a +(up?-1:+1), cs);
	    			doupdate=true;
	    		}
	    	
	    	if (doupdate)
	    		fireTableDataChanged();
	    		
	    	//return x.length==0?-1:x[x.length-1];
	    }
	}
	
	public class ColorRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 9021535818465873861L;
		
		public ColorRenderer() {
			setOpaque(true); //MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
			setBackground((Color)color);
			return this;
		}
	}

	private void chooseFont()
	{
		JFontChooser fontChooser = new JFontChooser();
		int result = fontChooser.showDialog(null);
		if (result != JFontChooser.OK_OPTION)
			return;
		
		Config conf = Config.getInstance();
		conf.setTitleFont(fontChooser.getSelectedFont());
		conf.Save();
	}

	public void Save()
	{
		Config conf = Config.getInstance();
		
		boolean restart_needed = false;
		
		if (comboLanguage.getSelectedIndex() == 0) {
			if (!conf.getLocale().getLanguage().toLowerCase().equals("it")) {
				conf.setLocale("it", "IT");
				restart_needed = true;
			}
		}
		else {
			if (!conf.getLocale().getLanguage().toLowerCase().equals("en")) {
				conf.setLocale("en", "US");
				restart_needed = true;
			}
		}
		
		if (restart_needed)
			Utils.MessageBox(conf.getResources().getString("MsgChangesOnRestart"), conf.getResources().getString("TitleChangesOnRestart"));
		
		
		try { conf.setPictureHeight(Integer.parseInt(pictureHeight.getText())); } catch (Exception e) {}
		try { conf.setPictureWidth(Integer.parseInt(pictureWidth.getText())); } catch (Exception e) {}
		
		try { conf.setMinDataCoverage100(Double.parseDouble(minDataCoverage100.getText())/100); } catch (Exception e) {}
		
		try {
			final int n = Integer.parseInt(elfValueFieldn.getText());
			if (n < 2)
				Utils.MessageBox(conf.getResources().getString("MsgInvalidSRBFieldn"), conf.getResources().getString("TitleWarning"));
			conf.setElfValuefieldn(n); 
		} catch (Exception e) {}
		
		try {
			final int n = Integer.parseInt(srbValueFieldn.getText());
			if (n < 2)
				Utils.MessageBox(conf.getResources().getString("MsgInvalidELFFieldn"), conf.getResources().getString("TitleWarning"));
			conf.setSrbValuefieldn(n);
		} catch (Exception e) {}
		
		try {
			final int n = Integer.parseInt(currentValueFieldn.getText());
			if (n < 2)
				Utils.MessageBox(conf.getResources().getString("MsgInvalidCurrentFieldn"), conf.getResources().getString("TitleWarning"));
			conf.setCurrentValuefieldn(n);
		} catch (Exception e) {}
		
		try { conf.setLegendSize(Double.parseDouble(legendSize.getText())); } catch (Exception e) {}
		try { conf.setLegendX(Integer.parseInt(legendX.getText())); } catch (Exception e) {}
		try { conf.setLegendY(Integer.parseInt(legendY.getText())); } catch (Exception e) {}
		
		if (graphYmin.getText().isEmpty() && graphYmax.getText().isEmpty()) {
			conf.setForceYmin(0); // disable force y range
			conf.setForceYmax(0);
		}
		else {
			try { conf.setForceYmin(Integer.parseInt(graphYmin.getText())); } catch (Exception e) {}
			try { conf.setForceYmax(Integer.parseInt(graphYmax.getText())); } catch (Exception e) {}
		}
		
		try { conf.setInstrumentLowELF(ElfValue.valueStringToInt(instrumentLowELF.getText())); } catch (Exception e) {}
		try { conf.setInstrumentLowSRB(ElfValue.valueStringToInt(instrumentLowSRB.getText())); } catch (Exception e) {}
		try { conf.setCurrentLowCut(ElfValue.valueStringToInt(instrumentLowCurrent.getText())); } catch (Exception e) {}

		conf.setOperationELF(DataFunction.create((String)comboOpELF.getSelectedItem()));
		conf.setOperationSRB(DataFunction.create((String)comboOpSRB.getSelectedItem()));
		
		conf.setAxisFormat(axisFormat.getText());
		
		conf.Save();
	}
	
	public void Reload()
	{
		Config.init();
		Refresh();
	}
	
	public void Refresh()
	{
		final Config conf = Config.getInstance();
		
		try {
			if (conf.getLocale().getLanguage().toLowerCase().equals("it"))
				comboLanguage.setSelectedIndex(0);
			else
				comboLanguage.setSelectedIndex(1);
		}
		catch (Exception e) {}
		
		pictureHeight.setText(""+conf.getPictureHeight());
		pictureWidth.setText(""+conf.getPictureWidth());
		elfValueFieldn.setText(""+conf.getElfValuefieldn());
		srbValueFieldn.setText(""+conf.getSrbValuefieldn());
		currentValueFieldn.setText(""+conf.getCurrentValuefieldn());
		legendSize.setText(""+conf.getLegendSize());
		legendX.setText(""+conf.getLegendX());
		legendY.setText(""+conf.getLegendY());
		
		minDataCoverage100.setText(""+Utils.formatDoubleAsNeeded(conf.getMinDataCoverage100()*100));
		
		instrumentLowELF.setText(""+ElfValue.valueIntToString(conf.getInstrumentLowELF()));
		instrumentLowSRB.setText(""+ElfValue.valueIntToString(conf.getInstrumentLowSRB()));
		instrumentLowCurrent.setText(""+CurrentValue.valueIntToString(conf.getCurrentLowCut()));
		
		comboOpSRB.setSelectedItem(conf.getOperationSRB().getName());
		comboOpELF.setSelectedItem(conf.getOperationELF().getName());
		
		axisFormat.setText(conf.getAxisFormat());
		
		if (conf.getForceYmin() == 0 && conf.getForceYmax() == 0) {
			graphYmin.setText("");
			graphYmax.setText("");
		}
		else {		
			graphYmin.setText(ElfValue.valueIntToString(conf.getForceYmin()));
			graphYmax.setText(ElfValue.valueIntToString(conf.getForceYmax()));
		}
	}
}

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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
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
import y.elf.filterfunctions.FilterFunction;
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
	
	
	private JTabbedPane tabs;
	
	
	private JComboBox<String> comboLanguage;
	
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
	
	private JTextField lineWidthELF;
	private JTextField lineWidthSRB;
	private JPanel colorBackELF;
	private JPanel colorBackSRB;
	
	private JTextField legendSize;
	private JTextField legendX;
	private JTextField legendY;
	
	private JTextField workingFolder;
	
	private JTextField instrumentLowELF;
	private JTextField instrumentLowSRB;
	private JTextField instrumentLowCurrent;
	private JTextField axisFormat;
	
	private JButton titleFont;
	
	private JTable srbSeries;
	private JTable emSeries;
	private JTable curSeries;
	
	private JComboBox<String> comboOpSRB;
	private JComboBox<String> comboOpELF;
	private JComboBox<String> comboOpCurrent;
	
	private JComboBox<String> comboFilterSRB;
	private JComboBox<String> comboFilterELF;
	private JComboBox<String> comboFilterCurrent;
	
	private String[] configColumnNames;
	private final String[] avaibleLanguages = { "Italiano", "English" };
	
	
	public PanelConfig()
	{
		super();
		
		tabs = new JTabbedPane();
		JPanel generalTab = new JPanel();
		JPanel elfTab = new JPanel();
		JPanel srbTab = new JPanel();
		JPanel currentTab = new JPanel();
		
		tabs.add(generalTab, Config.getResource("TitleConfigTabGeneral"));
		tabs.add(elfTab, Config.getResource("TitleConfigTabElf"));
		tabs.add(srbTab, Config.getResource("TitleConfigTabSrb"));
		tabs.add(currentTab, Config.getResource("TitleConfigTabCurrent"));
		
		this.setLayout(new BorderLayout());
		this.add(tabs, BorderLayout.CENTER);
		
		
		configColumnNames = Config.getInstance().getConfigColumnNames();
		
		generalTab.setLayout(new GridLayout(0, 2));
		
		comboLanguage = new JComboBox<String>(avaibleLanguages);
		
		comboOpSRB = new JComboBox<String>(DataFunction.getNames());
		comboOpELF = new JComboBox<String>(DataFunction.getNames());
		comboOpCurrent = new JComboBox<String>(DataFunction.getNames());
		
		comboFilterSRB = new JComboBox<String>(FilterFunction.getNames());
		comboFilterELF = new JComboBox<String>(FilterFunction.getNames());
		comboFilterCurrent = new JComboBox<String>(FilterFunction.getNames());
		
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
		
		lineWidthELF = new JTextField();
		lineWidthSRB = new JTextField();
		
		colorBackELF = new JPanel();
		colorBackELF.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final ColorEditor ce = new ColorEditor();
				ce.actionPerformed(new ActionEvent(this, 0, "edit"));
				colorBackELF.setBackground((Color) ce.getCellEditorValue());
			}

			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		colorBackSRB = new JPanel();
		colorBackSRB.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final ColorEditor ce = new ColorEditor();
				ce.actionPerformed(new ActionEvent(this, 0, "edit"));
				colorBackSRB.setBackground((Color) ce.getCellEditorValue());
			}

			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		
		legendSize = new JTextField();
		legendX = new JTextField();
		legendY = new JTextField();
		workingFolder = new JTextField();
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
		srbSeries.setModel(new TableModel(Config.getInstance().getConst_value("srb")));
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
		emSeries.setModel(new TableModel(Config.getInstance().getConst_value("elf")));
		emSeries.clearSelection();
		emSeries.setDefaultEditor(Color.class, new ColorEditor());
		
		
		curSeries = new JTable(new Object[0][0], configColumnNames) {
			private static final long serialVersionUID = 123456787L;
			public TableCellRenderer getCellRenderer(int row, int column) { return column==1 ? weirdRenderer : super.getCellRenderer(row, column); }
		};
		curSeries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		curSeries.setRowSelectionAllowed(true);
		curSeries.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i=0; i<curSeries.getColumnCount(); i++)
			Utils.jtable_adjustColumnSizes(curSeries, i, 1);
		Utils.jtable_adjustRowSizes(curSeries);		
		curSeries.setModel(new TableModel(Config.getInstance().getConst_value("cur")));
		curSeries.clearSelection();
		curSeries.setDefaultEditor(Color.class, new ColorEditor());
		
		setColumnAsConfigSerieUsages(srbSeries, 2);
		setColumnAsConfigSerieUsages(emSeries, 2);
		setColumnAsConfigSerieUsages(curSeries, 2);
		
		
		generalTab.add(new JLabel(" "+Config.getResource("TitleLanguage")));
		generalTab.add(comboLanguage);
		
		generalTab.add(new JLabel(" "+Config.getResource("TitleMinDataCoverage")));
		generalTab.add(minDataCoverage100);
		
		generalTab.add(new JLabel(" "+Config.getResource("TitlePicWidth")));
		generalTab.add(pictureWidth);
		generalTab.add(new JLabel(" "+Config.getResource("TitlePicHeight")));
		generalTab.add(pictureHeight);
		
		generalTab.add(new JLabel(" "+Config.getResource("TitleYrangeMin")));
		generalTab.add(graphYmin);
		generalTab.add(new JLabel(" "+Config.getResource("TitleYrangeMax")));
		generalTab.add(graphYmax);
		
		generalTab.add(new JLabel(" "+Config.getResource("TitleLegendSize")));
		generalTab.add(legendSize);
		generalTab.add(new JLabel(" "+Config.getResource("TitleLegendX")));
		generalTab.add(legendX);
		generalTab.add(new JLabel(" "+Config.getResource("TitleLegendY")));
		generalTab.add(legendY);
		
		generalTab.add(new JLabel(" "+Config.getResource("TitleWorkingFolder")));
		generalTab.add(createOpenDirectoryTextField(this, workingFolder, Config.getResource("TitleWorkingFolder")));
		
		generalTab.add(new JLabel(" "+Config.getResource("TitleInstrumentalLowCurrent")));
		generalTab.add(instrumentLowCurrent);
		
		generalTab.add(new JLabel(" "+Config.getResource("TitleXAxisFormat")));
		
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
		generalTab.add(axisPanel);
		generalTab.add(new JLabel(" "+Config.getResource("TitleTitleFont")));
		generalTab.add(titleFont);
		
		srbTab.setLayout(new BorderLayout());
		srbTab.add(srbSeries/*new JScrollPane(srbSeries)*/, BorderLayout.CENTER);
		srbTab.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleSerieSrb")));
		
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
					srbSeries.setModel(new TableModel(Config.getInstance().restoreDefaultConstValuesSRB()) );
			}
		});
		JPanel t1x = new JPanel();
		t1x.setLayout(new FlowLayout());
		t1x.add(srbBnew);
		t1x.add(srbBup);
		t1x.add(srbBdown);
		t1x.add(srbBdel);
		t1x.add(srbDefault);
		srbTab.add(t1x, BorderLayout.SOUTH);
		
		JPanel t1up = new JPanel();
		t1up.setLayout(new GridLayout(0, 2));
		t1up.add(new JLabel(" "+Config.getResource("TitleSrbFieldn")));
		t1up.add(srbValueFieldn);
		t1up.add(new JLabel(" "+Config.getResource("TitleInstrumentalLowSRB")));
		t1up.add(instrumentLowSRB);
		t1up.add(new JLabel(" "+Config.getResource("TitleSRBFilterFunction")));
		t1up.add(comboFilterSRB);
		t1up.add(new JLabel(" "+Config.getResource("TitleOperationTypeSRB")));
		t1up.add(comboOpSRB);
		t1up.add(new JLabel(" "+Config.getResource("TitleBackgroundColor")));
		t1up.add(colorBackSRB);		
		t1up.add(new JLabel(" "+Config.getResource("TitleLineWidth")));
		t1up.add(lineWidthSRB);
		
		srbTab.add(t1up, BorderLayout.NORTH);
		
		
		elfTab.setLayout(new BorderLayout());
		elfTab.add(emSeries/*new JScrollPane(emSeries)*/, BorderLayout.CENTER);
		elfTab.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleSerieElf")));
		
		
		
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
					emSeries.setModel(new TableModel(Config.getInstance().restoreDefaultConstValuesELF()) );
			}
		});
		JPanel t2x = new JPanel();
		t2x.setLayout(new FlowLayout());
		t2x.add(elfBnew);
		t2x.add(elfBup);
		t2x.add(elfBdown);
		t2x.add(elfBdel);
		t2x.add(elfDefault);
		elfTab.add(t2x, BorderLayout.SOUTH);
		
		JPanel t2up = new JPanel();
		t2up.setLayout(new GridLayout(0, 2));
		t2up.add(new JLabel(" "+Config.getResource("TitleElfFieldn")));
		t2up.add(elfValueFieldn);
		t2up.add(new JLabel(" "+Config.getResource("TitleInstrumentalLowELF")));
		t2up.add(instrumentLowELF);
		t2up.add(new JLabel(" "+Config.getResource("TitleELFFilterFunction")));
		t2up.add(comboFilterELF);
		t2up.add(new JLabel(" "+Config.getResource("TitleOperationTypeCurrent")));
		t2up.add(comboOpELF);
		t2up.add(new JLabel(" "+Config.getResource("TitleBackgroundColor")));
		t2up.add(colorBackELF);
		t2up.add(new JLabel(" "+Config.getResource("TitleLineWidth")));
		t2up.add(lineWidthELF);
		elfTab.add(t2up, BorderLayout.NORTH);
		
		
		currentTab.setLayout(new BorderLayout());
		
		JPanel curUp = new JPanel();
		curUp.setLayout(new GridLayout(0, 2));
		curUp.add(new JLabel(" "+Config.getResource("TitleCurrentFieldn")));
		curUp.add(currentValueFieldn);
		curUp.add(new JLabel(" "+Config.getResource("TitleCurrentFilterFunction")));
		curUp.add(comboFilterCurrent);
		curUp.add(new JLabel(" "+Config.getResource("TitleCurrentFilterFunction")));
		curUp.add(comboOpCurrent);
		
		currentTab.add(curUp, BorderLayout.NORTH);
		currentTab.add(curSeries/*new JScrollPane(curSeries)*/, BorderLayout.CENTER);
		currentTab.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleSerieCurrent")));
		
		
		JButton curBnew = new JButton(Config.getResource("MsgAdd"));
		curBnew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((TableModel)curSeries.getModel()).newLine();
			}
		});
		JButton curBup = new JButton(Config.getResource("MsgUp"));
		curBup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int[] selected = curSeries.getSelectedRows();
				((TableModel)curSeries.getModel()).move( selected, true );
				if (selected.length > 0)
					curSeries.setRowSelectionInterval(selected[selected.length-1]-1, selected[selected.length-1]-1);
			}
		});		
		JButton curBdown = new JButton(Config.getResource("MsgDown"));
		curBdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int[] selected = curSeries.getSelectedRows();
				((TableModel)curSeries.getModel()).move( selected, false );
				if (selected.length > 0)
					curSeries.setRowSelectionInterval(selected[selected.length-1]+1, selected[selected.length-1]+1);
			}
		});		
		JButton curBdel = new JButton(Config.getResource("MsgDel"));
		curBdel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((TableModel)curSeries.getModel()).delLines( curSeries.getSelectedRows() );
			}
		});
		JButton curDefault = new JButton(Config.getResource("MsgRestoreDefaults"));
		curDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Utils.MessageBoxYesNo(null, Config.getResource("MsgRestoreCurDefaults"), Config.getResource("TitleRestoreCurDefaults")))
					curSeries.setModel(new TableModel(Config.getInstance().restoreDefaultConstValuesCurrents()) );
			}
		});
		JPanel t3x = new JPanel();
		t3x.setLayout(new FlowLayout());
		t3x.add(curBnew);
		t3x.add(curBup);
		t3x.add(curBdown);
		t3x.add(curBdel);
		t3x.add(curDefault);
		currentTab.add(t3x, BorderLayout.SOUTH);
		
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(1, 2));
		btnPanel.add(buttonSave);
		btnPanel.add(buttonReload);
		this.add(btnPanel, BorderLayout.SOUTH);
		
		Refresh();
	}
	
    private void setColumnAsConfigSerieUsages(JTable table, int i) {
		TableColumn theColumn = table.getColumnModel().getColumn(i);
		JComboBox<String> comboBox = new JComboBox<String>();
		for (String s : ConfigSerie.USAGES)
			comboBox.addItem(s);
		theColumn.setCellEditor(new DefaultCellEditor(comboBox));
	}

	private static JPanel createOpenDirectoryTextField(final Component window, final JTextField textedit, final String dialog_title)
	{
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		JButton btn = new JButton("...");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser(Config.getInstance().getLastUsedFolder());
				chooser.setDialogTitle(dialog_title);
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION)
					textedit.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		
		jp.add(textedit, BorderLayout.CENTER);
		jp.add(btn, BorderLayout.EAST);
		
		return jp;
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
		
		
		conf.setColorBackgroundELF(colorBackELF.getBackground());
		conf.setColorBackgroundSRB(colorBackSRB.getBackground());
		
		try { conf.setLineWidthELF(Double.parseDouble(lineWidthELF.getText())); } catch (Exception e) {}
		try { conf.setLineWidthSRB(Double.parseDouble(lineWidthSRB.getText())); } catch (Exception e) {}
		
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
		try { conf.setLegendX(Double.parseDouble(legendX.getText())); } catch (Exception e) {}
		try { conf.setLegendY(Double.parseDouble(legendY.getText())); } catch (Exception e) {}
		
		conf.setWorkingFolder(workingFolder.getText());
		
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

		conf.setOperationELF(DataFunction.createFromName((String)comboOpELF.getSelectedItem()));
		conf.setOperationSRB(DataFunction.createFromName((String)comboOpSRB.getSelectedItem()));
		conf.setOperationCurrent(DataFunction.createFromName((String)comboOpCurrent.getSelectedItem()));
		
		conf.setFilterELF(FilterFunction.createFromName((String)comboFilterELF.getSelectedItem()));
		conf.setFilterSRB(FilterFunction.createFromName((String)comboFilterSRB.getSelectedItem()));
		conf.setFilterCurrent(FilterFunction.createFromName((String)comboFilterCurrent.getSelectedItem()));
		
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
		
		colorBackELF.setBackground(conf.getColorBackgroundELF());
		colorBackSRB.setBackground(conf.getColorBackgroundSRB());
		lineWidthELF.setText(""+conf.getLineWidthELF());
		lineWidthSRB.setText(""+conf.getLineWidthSRB());
		pictureHeight.setText(""+conf.getPictureHeight());
		pictureWidth.setText(""+conf.getPictureWidth());
		elfValueFieldn.setText(""+conf.getElfValuefieldn());
		srbValueFieldn.setText(""+conf.getSrbValuefieldn());
		currentValueFieldn.setText(""+conf.getCurrentValuefieldn());
		legendSize.setText(""+conf.getLegendSize());
		legendX.setText(""+conf.getLegendX());
		legendY.setText(""+conf.getLegendY());
		
		workingFolder.setText(conf.getWorkingFolder());
		minDataCoverage100.setText(""+Utils.formatDoubleAsNeeded(conf.getMinDataCoverage100()*100));
		
		instrumentLowELF.setText(""+ElfValue.valueIntToString(conf.getInstrumentLowELF()));
		instrumentLowSRB.setText(""+ElfValue.valueIntToString(conf.getInstrumentLowSRB()));
		instrumentLowCurrent.setText(""+CurrentValue.valueIntToString(conf.getCurrentLowCut()));
		
		comboOpSRB.setSelectedItem(conf.getOperationSRB().getName());
		comboOpELF.setSelectedItem(conf.getOperationELF().getName());
		comboOpCurrent.setSelectedItem(conf.getOperationCurrent().getName());
		
		comboFilterSRB.setSelectedItem(conf.getFilterSRB().getName());
		comboFilterELF.setSelectedItem(conf.getFilterELF().getName());
		comboFilterCurrent.setSelectedItem(conf.getFilterCurrent().getName());
		
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

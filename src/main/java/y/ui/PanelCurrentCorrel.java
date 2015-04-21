package y.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.joda.time.DateTime;

import y.elf.CurrentElfDb;
import y.graphs.XLSHelper;
import y.utils.Config;
import y.utils.Utils;

public class PanelCurrentCorrel extends PanelYEM
{
	private static final long serialVersionUID = -8639394788255353153L;

	private FileList elfFilelist;
	private FileList curFilelist;
	private JButton loadButton;
	private JSpinner daSpinner;
	private JSpinner aSpinner;
	
	private JTextField deltaTShift;
	
	private JTextField textImax;
	private JTextField textErrI;
	private JTextField textErrB;

	private JTextField textMatchingN;
	private JTextField textCorrelation;
	private JTextField textBmax;

	
	public PanelCurrentCorrel()
	{
		super();
		
		initialize();
	}
	
	private void initialize() {
		
		elfFilelist = new FileList();
		curFilelist = new FileList();
		
		loadButton = new JButton(Config.getResource("MsgGo"));
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadFiles();
			}
		});
		
		daSpinner = PanelELF.createDateSpinner();
		aSpinner = PanelELF.createDateSpinner();
		textMatchingN = new JTextField();
		textMatchingN.setEditable(false);
		textCorrelation = new JTextField(Config.getResource("MsgNone"));
		textCorrelation.setEditable(false);
		textBmax = new JTextField(Config.getResource("MsgNone"));
		textBmax.setEditable(false);
		
		textImax = new JTextField("100");
		textErrI = new JTextField("0.1");
		textErrB = new JTextField("0.1");
		deltaTShift = new JTextField("0");
		
		final ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				applyFilters();
			}
		};
//		final ActionListener al = new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				applyFilters();
//			}
//		};
		final DocumentListener dl = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				applyFilters();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				applyFilters();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				applyFilters();
			}
		};
		daSpinner.addChangeListener(cl);
		aSpinner.addChangeListener(cl);
		textImax.getDocument().addDocumentListener(dl);
		textErrI.getDocument().addDocumentListener(dl);
		textErrB.getDocument().addDocumentListener(dl);
		deltaTShift.getDocument().addDocumentListener(dl);
		
//		textImax.addActionListener(al);
//		textErrI.addActionListener(al);
//		textErrB.addActionListener(al);
//		deltaTShift.addActionListener(al);

		JPanel west = new JPanel();
		west.setLayout(new GridLayout(0,1));
		
		JPanel west2 = new JPanel();
		west2.setLayout(new BorderLayout());
		
		elfFilelist.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleElf")));
		curFilelist.setBorder(BorderFactory.createTitledBorder(Config.getResource("TitleCurr")));
		
		west.add(elfFilelist);
		west.add(curFilelist);
		west2.add(loadButton, BorderLayout.SOUTH);
		west2.add(west, BorderLayout.CENTER);

		this.setLayout(new GridLayout(0,2));
		this.add(west2);
		
		JPanel eastUp = new JPanel();
		eastUp.setLayout(new GridLayout(0,2));
		
		eastUp.add(new JLabel(Config.getResource("TitleMatchingDataN")));
		eastUp.add(textMatchingN);
		eastUp.add(new JLabel(" "+Config.getResource("MsgFrom")+":"));
		eastUp.add(daSpinner);
		eastUp.add(new JLabel(" "+Config.getResource("MsgTo")+":"));
		eastUp.add(aSpinner);
		
		eastUp.add(new JLabel(" Imax:"));
		eastUp.add(textImax);
		eastUp.add(new JLabel(" u(I):"));
		eastUp.add(textErrI);
		eastUp.add(new JLabel(" u(B):"));
		eastUp.add(textErrB);
		
		
		JPanel shiftPanel = new JPanel();
		shiftPanel.setLayout(new BorderLayout());
		JButton shiftCheck = new JButton(Config.getResource("MsgCheck"));
		shiftCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkDeltaTimeShift();
			}
		});
		shiftPanel.add(deltaTShift, BorderLayout.CENTER);
		shiftPanel.add(shiftCheck, BorderLayout.EAST);
		eastUp.add(new JLabel(Config.getResource("TitleDtShift")));
		eastUp.add(shiftPanel);
		
		eastUp.add(new JLabel(Config.getResource("TitleCorrelation")));
		eastUp.add(textCorrelation);
		eastUp.add(new JLabel(Config.getResource("TitleBmax")));
		eastUp.add(textBmax);
		
		JPanel east = new JPanel();
		east.setLayout(new BorderLayout());
		east.add(eastUp, BorderLayout.NORTH);
		this.add(east);
		
		JButton xlsButton = new JButton(Config.getResource("MsgSaveXLS"));
		xlsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportXLS();
			}
		});
		east.add(xlsButton, BorderLayout.SOUTH);
		
		masterDB = null;
		filteredDB = null;
	}

	
	private CurrentElfDb masterDB;
	private CurrentElfDb filteredDB;
	
	
	private void loadFiles() {
		
		final Config config = Config.getInstance();
		
		masterDB = CurrentElfDb.createDb(elfFilelist.getFilenames(), curFilelist.getFilenames(), config);
		// non chiamare MAI .match() su masterDB!
		filtersEnabled = false;
		daSpinner.setValue(masterDB.getStartDate().toDate());
		aSpinner.setValue(masterDB.getEndDate().toDate());
		filtersEnabled = true;
		applyFilters();
	}
	
	
	private boolean filtersEnabled = true;
	
	private synchronized void applyFilters() {
		if (!filtersEnabled)
			return;
		
		final DateTime from = new DateTime((Date) daSpinner.getValue());
		final DateTime to = new DateTime((Date) aSpinner.getValue());
		final int dt = getDtShift();
		
		filteredDB = masterDB.cut(from, to, dt);
		final int matched = filteredDB.match();
		
		final Color dasColor = UIManager.getColor(daSpinner);

		textMatchingN.setText(""+matched);
		textMatchingN.setBackground(matched > 0 ? dasColor : Color.red);
		
		if (matched < 0)
			Utils.MessageBox(Config.getResource("MsgErrorMatch"), Config.getResource("TitleErrorInternal"));
		
		final double correlation = filteredDB.correlation();
		textCorrelation.setText(String.format("%.6f", correlation));
		if (correlation >= 0.90) {
			textCorrelation.setBackground(dasColor);
			try {
				final double Imax = Double.parseDouble(textImax.getText());
				final double eI = Double.parseDouble(textErrI.getText());
				final double eB = Double.parseDouble(textErrB.getText());
				
				final double Rm = filteredDB.getRm();					// mean Ri
				final double Bmax = Imax*Rm;

				final double eR2 = eI*eI + eB*eB - eI*eB*correlation;	// e(R)^2
				final double Rm2 = filteredDB.getURm2();				// 1/n^2 * sum R_i^2
				final double uRm2 = Rm2*eR2;							// u(Rm)^2 = 1/n^2 * sum R_i^2 * e(R)^2
				
				final double uBmax = Math.sqrt( uRm2 + Rm*Rm*eI*eI )*Imax;
				final double eperc2 = 2*100*uBmax/Bmax;
				
				textBmax.setText(String.format("%.4f", Bmax) + " ± " + String.format("%.2f", eperc2)+"%");
			}
			catch (Exception e) {
				textBmax.setText(Config.getResource("MsgErrorImax"));
			}
			textBmax.setBackground(dasColor);
		}
		else {
			textCorrelation.setBackground(Color.red);
			textBmax.setText("-");
			textBmax.setBackground(Color.red);
		}
	}

	
	public int getDtShift() {
		try { return Integer.parseInt(this.deltaTShift.getText()); }
		catch (Exception e) { return 0; }
	}
	
	public void checkDeltaTimeShift()
	{
		final String sMaxDt = Utils.MessageBoxString(Config.getResource("MsgSelectDtShift"));
		if (sMaxDt.isEmpty())
			return;
		
		try {
			final int maxShift = Integer.parseInt(sMaxDt);
			
			int bestShift = -maxShift;
			double bestCorr = -1;
			
			final DateTime from = new DateTime((Date) daSpinner.getValue());
			final DateTime to = new DateTime((Date) aSpinner.getValue());
			
			for (int actShift = -maxShift; actShift <= maxShift; actShift++) {
				CurrentElfDb db = masterDB.cut(from, to, actShift);
				db.match();
				final double curCorr = db.correlation();
				
				if (curCorr > bestCorr || (actShift==0 && curCorr == bestCorr)) { // 0 vince anche a parimerito
					bestCorr = curCorr;
					bestShift = actShift;
				}
			}
			
			if (bestCorr > 0) { 
				deltaTShift.setText(""+bestShift);
				Utils.MessageBox(Config.getResource("MsgBestShift")+bestShift+"\n"+Config.getResource("MsgBestCorr")+bestCorr, Config.getResource("TitleDone"));
			}
		}
		catch (NumberFormatException e) {
			Utils.MessageBox(Config.getResource("MsgInsertedInput")+ " \""+sMaxDt+"\" "+Config.getResource("MsgIsInvalidNumber"), Config.getResource("TitleError"));
			return;
		}
	}

	public void exportXLS()
	{
		String path = Utils.openFileDialog(Config.getResource("MsgSelectFile"), this, Config.getResource("TitleXLSFile"), "xlsx");
		if (path.isEmpty())
			return;
		
		if (!path.toLowerCase().endsWith("xls") && !path.toLowerCase().endsWith("xlsx"))
			path += ".xlsx";
		
		try {
			final double imax = Double.parseDouble(textImax.getText());
			final double ui = Double.parseDouble(textErrI.getText());
			final double ub = Double.parseDouble(textErrB.getText());
			
			XLSHelper.saveCorrelationsCurrents(path, filteredDB, imax, ui, ub);
		}
		catch (Exception e) {
			Utils.MessageBox(Config.getResource("MsgErrorXlsx")+"\n"+e.getMessage(), Config.getResource("TitleErrorXlsx"));
		}
	}

	@Override
	public PanelType getType() {
		return PanelType.CorrenteCorrel;
	}
}

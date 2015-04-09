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
import y.yElf;
import y.utils.Config;
import y.utils.Utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;


public class WindowMain extends JFrame
{
	public static final Dimension PREFERRED_DIMENSION = new Dimension(960, 650);

	private static WindowMain INSTANCE = null;
	public static void init() { INSTANCE = new WindowMain(); }
	public static WindowMain getInstance() { return INSTANCE; }

	private static final long serialVersionUID = 4597576424263564992L;
	
	private JTabbedPane maintab;
	private PanelELF elfPanel;
	private PanelSRB srbPanel;
	private PanelCurrentCorrel currentCorrPanel;
	private PanelCurrents currentsPanel;

	
	public WindowMain()
	{
		super("yElf - " + yElf.VersionString + "-" + yElf.ReleaseDate.replaceAll("-", "").substring(2));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exception) {
			Utils.MessageBox("Invalid look and feel.", "ERROR");
		}

		// Add a window listener for close button
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});
		
		maintab = new JTabbedPane();
		elfPanel = new PanelELF();
		maintab.add(elfPanel, Config.getResource("TitleTabELF"));
		
		srbPanel = new PanelSRB();
		maintab.add(srbPanel, Config.getResource("TitleTabSRB"));
		
		currentsPanel = new PanelCurrents();
		maintab.add(currentsPanel, Config.getResource("TitleTabCurrent"));
		
		currentCorrPanel = new PanelCurrentCorrel();
		maintab.add(currentCorrPanel, Config.getResource("TitleTabCurrentCorr"));

		PanelConfig pc = new PanelConfig();
		maintab.add(pc, Config.getResource("TitleTabConfig"));
		
		PanelAboutBox pa = new PanelAboutBox(this);
		maintab.add(pa, Config.getResource("TitleTabAbout"));
		
		this.setLayout(new BorderLayout());
		this.add(maintab, BorderLayout.CENTER);
		
		setPreferredSize(PREFERRED_DIMENSION);
		
		// pack and display
		pack();
		setVisible(true);
		Utils.centerWindow(this);
	}
}

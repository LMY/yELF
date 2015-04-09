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

import javax.swing.JFrame;
import javax.swing.JPanel;

import y.utils.Utils;

public abstract class PanelYEM extends JPanel
{
	protected static void do_openWindow(String title, PanelYEM panel)
	{
		JFrame newform = new JFrame(title);
		newform.add(panel);
		newform.pack();
		Utils.centerWindow(newform);
		newform.setVisible(true);
	}
	
	public static enum PanelType { None, UNKNOWN, About, AboutBox, Calculate, Canvas, Config, Debug, ELF, SRB, Corrente, CorrenteCorrel  };
	
	
	private static final long serialVersionUID = -6268810766958299473L;

	public PanelYEM()
	{
		super();
	}
	
	public abstract PanelType getType();
}

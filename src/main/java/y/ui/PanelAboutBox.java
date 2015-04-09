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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import y.yElf;
import y.utils.Utils;

public class PanelAboutBox extends PanelYEM
{
	private static final long serialVersionUID = -4661587884201970452L;

	public static void do_openWindow()
	{
		JFrame newform = new JFrame("About");
		newform.add(new PanelAboutBox(newform));
		newform.pack();
		Utils.centerWindow(newform);
		newform.setVisible(true);
	}
	
	
	private JFrame form;
	
	public PanelAboutBox(JFrame form)
	{
		super();
		this.form = form;
		this.setLayout(new BorderLayout());
		
		this.add(new JLabel("<html>&nbsp;yElf "+yElf.VersionString+"<br/>"
				+"&nbsp;&nbsp;&nbsp;Miro Salvagni - 2015 - ARPA FVG&nbsp;<br/>"
				+"&nbsp;&nbsp;&nbsp;GPL v3.0&nbsp;<br/>"
				+"&nbsp;&nbsp;&nbsp;0432 1918356 - miro.salvagni@arpa.fvg.it&nbsp;<br />"
				+"&nbsp;&nbsp;&nbsp;349 1086544 - miro.salvagni@gmail.com&nbsp;<br />"
				+"</html>"), BorderLayout.CENTER);
		
		JPanel sub = new JPanel();
		sub.setLayout(new FlowLayout());
		
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ok();
			}
		});
		sub.add(ok);
		this.add(sub, BorderLayout.SOUTH);		
	}

	public void ok()
	{
		form.dispose();
	}
	
	@Override
	public PanelType getType() {
		return PanelType.AboutBox;
	}
}

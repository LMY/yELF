package y.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import y.utils.Config;
import y.utils.Utils;

public class FileList extends JPanel
{
	private static final long serialVersionUID = 263483585229461634L;

	private JList<String> filelist;
	private JButton addFile;
	private JButton removeFile;
	
	private DefaultListModel<String> filelistModel;

	public FileList()
	{
		super();

		addFile = new JButton(Config.getResource("MsgAdd"));
		addFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				add();
			}
		});
		
		removeFile = new JButton(Config.getResource("MsgDel"));
		removeFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remove();
			}
		});
		
		filelistModel = new DefaultListModel<String>();
		filelist = new JList<String>(filelistModel);
		filelist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		filelist.setLayoutOrientation(JList.VERTICAL);
		filelist.setVisibleRowCount(-1);
		JScrollPane filelistCentralineScroller = new JScrollPane(filelist);
		
		JPanel upPanel = new JPanel();
		upPanel.setLayout(new BorderLayout());
		
		JPanel weatUp = new JPanel();
		weatUp.setLayout(new FlowLayout());
		weatUp.add(addFile);
		weatUp.add(removeFile);
		
		upPanel.add(weatUp, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(upPanel, BorderLayout.NORTH);
		this.add(filelistCentralineScroller, BorderLayout.CENTER);

		addFile.requestFocus();
	}
	
	public void add()
	{
		final String[] f = Utils.openMultipleFileDialog(Config.getResource("MsgSelectFileToAdd"), this, Config.getResource("MsgMeasurementFile"), "txt", "csv");
		
		for (int i=0; i<f.length; i++)
			filelistModel.addElement(f[i]);
	}
	
	public void remove()
	{
		final int[] idxes = filelist.getSelectedIndices();
		if (idxes == null || idxes.length <= 0)
			return;
		
		for (int i=idxes.length-1; i>=0; i--)
			if (idxes[i] >= 0 && idxes[i] < filelistModel.size())
				filelistModel.remove(idxes[i]);
	}
	
	public void clear()
	{
		filelistModel.clear();
	}
	
	public String[] getFilenames() {
		String[] filenames = new String[filelistModel.size()];
		for (int i=0; i<filenames.length; i++)
			filenames[i] = (String) filelistModel.get(i);
		
		return filenames;
	}
}

package y.graphs;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import y.utils.Config;
import y.utils.Utils;

public class ChartHelper {
	
	public static boolean saveGraph(String filename, JFreeChart chart, Config config)
	{
		return saveGraph(filename, chart, config, true);
	}
	
	public static boolean saveGraph(String filename, JFreeChart chart, Config config, boolean overwrite_check)
	{
		if (overwrite_check && Utils.abortOnExistingAndDontOverwrite(filename))
			return false;
		
        try {
			ChartUtilities.saveChartAsPNG(new File(filename), chart, config.getPictureWidth(), config.getPictureHeight());
			Utils.MessageBox(Config.getResource("MsgImageSavedOk"), Config.getResource("TitleDone"));
			return true;
		} catch (IOException e) {
			Utils.MessageBox(Config.getResource("MsgTitleErrorWritingGraph")+"\n"+e.toString(), Config.getResource("MsgTitleErrorIO"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}

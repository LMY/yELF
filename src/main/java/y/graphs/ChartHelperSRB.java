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

package y.graphs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.ShapeUtilities;
import org.joda.time.DateTime;

import y.elf.ElfValue;
import y.utils.Config;
import y.utils.ConfigSerie;
import y.utils.Utils;

public class ChartHelperSRB
{
	public static JFreeChart getGraph(ElfValue[][] dayvalues, int[] mediane, boolean[] medianevalide, int med_max, DateTime from, DateTime to, Config config, ArrayList<ConfigSerie> series)
	{
        final TimeSeriesCollection dataset = createDataset(dayvalues, mediane, medianevalide, med_max, config, series);
        return createChart(dataset, from.toDate(), to.toDate(), config, series);
	}
	
	public static boolean saveData(String filename, ElfValue[][] dayvalues, int[] mediane, boolean[] medianevalide, int med_max, DateTime from, DateTime to, Config config, ArrayList<ConfigSerie> series)
	{
		if (Utils.abortOnExistingAndDontOverwrite(filename))
			return false;
		
        final JFreeChart chart = getGraph(dayvalues, mediane, medianevalide, med_max, from, to, config, series);
        return ChartHelper.saveGraph(filename, chart, config, false);
	}

	private static TimeSeriesCollection createDataset(ElfValue[][] dayvalues, int[] mediane, boolean[] medianevalide, int med_max, Config config, ArrayList<ConfigSerie> series)
    {
		final TimeSeries[] aserie = new TimeSeries[series.size()];
        for (int i=0; i<aserie.length; i++)
        	aserie[i] = new TimeSeries(series.get(i).getName());

        for (int d=0; d<dayvalues.length; d++) {
        	final ElfValue[] day = dayvalues[d];
        	
        	for (int i=0; i<day.length; i++) {
        		final ElfValue value = day[i];
        		final DateTime time = value.getTime();
        		
        		final RegularTimePeriod date = new Minute(time.toDate());
        		
        		if (value.isValid())
        			for (int aix=0; aix<aserie.length; aix++)
        				series.get(aix).add(aserie[aix], date, value.getValue(), mediane[d], medianevalide[d], mediane[med_max]);
        	}
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        
        for (int aix=0; aix<aserie.length; aix++)
        	dataset.addSeries(aserie[aix]);
        
        return dataset;
    }
    
    private static JFreeChart createChart(final TimeSeriesCollection dataset, Date from, Date to, Config config, ArrayList<ConfigSerie> series)
    {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
			Config.getResource("MsgTitleSrbGraph"),
			Config.getResource("TitleDate"),
			Config.getResource("MsgTitleSrbGraphYAxis"),
            dataset,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();
		LegendTitle lt = new LegendTitle(plot);
		lt.setItemFont(new Font("Dialog", Font.PLAIN, 12));
		lt.setBackgroundPaint(Color.white);
		lt.setFrame(new BlockBorder(Color.white));
		lt.setVerticalAlignment(VerticalAlignment.CENTER);
		XYTitleAnnotation ta = new XYTitleAnnotation(config.getLegendX(), config.getLegendY(), lt, RectangleAnchor.TOP_RIGHT);
		ta.setMaxWidth(config.getLegendSize());
		plot.addAnnotation(ta);
		chart.removeLegend();
        
        plot.setBackgroundPaint(config.getColorBackgroundSRB());
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        final Stroke lineStroke = new BasicStroke((float)config.getLineWidthSRB());
        
        for (int si=0; si<series.size(); si++) {
        	final ConfigSerie cs = series.get(si);
        	renderer.setSeriesLinesVisible(si, cs.isDrawLine());
        	
        	final float size = cs.getShapeSize();
        	renderer.setSeriesShapesVisible(si, size>0);
        	if (size > 0)
        		renderer.setSeriesShape(si, ShapeUtilities.createRegularCross(size, size) );

        	renderer.setSeriesStroke(si, lineStroke);
//          renderer.setSeriesOutlineStroke(si, lineStroke);
           	renderer.setSeriesPaint(si, cs.getColor());
        }
        plot.setRenderer(renderer);
        
        // x axis
        final DateAxis rangeAxis = (DateAxis) plot.getDomainAxis();
//      rangeAxis.setStandardTickUnits(DateAxis.createStandardDateTickUnits()); // Returns a collection of standard date tick units that uses the default time zone. This collection will be used by default, but you are free to create your own collection if you want to
        rangeAxis.setAutoRange(true);
//        rangeAxis.setRange(from, to);
        rangeAxis.setLowerMargin(0.01);
        rangeAxis.setUpperMargin(0.01);

        {
	        final Font axisFont = config.getAxisFont();
	        if (axisFont != null) {
	        	rangeAxis.setLabelFont(axisFont);
	        	plot.getRangeAxis().setLabelFont(axisFont);
	        }
	    }
        
        final String xaxisFmt = config.getAxisFormat();
        
		if (xaxisFmt == null || xaxisFmt.isEmpty()) {
	        double diffInDays =  (to.getTime() - from.getTime()) / (1000.0 * 60.0 * 60.0 * 24.0) ;
	        if (diffInDays < 2)
	        	rangeAxis.setDateFormatOverride(new SimpleDateFormat(Config.DEFAULT_TIMEFMT, DateFormatSymbols.getInstance()));
	        else
	        	rangeAxis.setDateFormatOverride(new SimpleDateFormat(Config.DEFAULT_SHORTTIMEFMT, DateFormatSymbols.getInstance()));
		}
		else
        	rangeAxis.setDateFormatOverride(new SimpleDateFormat(xaxisFmt, DateFormatSymbols.getInstance()));
		
		final ValueAxis domain = plot.getRangeAxis();
		if (config.getForceYmin() != 0 || config.getForceYmin() != 0)
			domain.setRange(config.getForceYmin(), config.getForceYmax());
		
		// title
		final Font titleFont = config.getTitleFont();
        if (titleFont != null)
        	chart.getTitle().setFont(titleFont);
        
        return chart;
    }
}

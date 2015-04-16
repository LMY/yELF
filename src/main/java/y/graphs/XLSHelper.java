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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.ScatterChartData;
import org.apache.poi.ss.usermodel.charts.ScatterChartSeries;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




import org.joda.time.DateTime;

import y.elf.CurrentElfDb;
import y.elf.CurrentValue;
import y.elf.ElfDb;
import y.elf.ElfValue;
import y.elf.MeasurementValue;
import y.utils.Config;
import y.utils.Utils;

public class XLSHelper
{
	public static int getIndexOfMax(int[] values)
	{
		int maxv = 0;
		for (int i=1; i<values.length; i++)
			if (values[i] > values[maxv])
				maxv = i;
		return maxv;
	}

	public static boolean saveElfData(String filename, ElfDb db, double sensibilita, boolean save_grafico)
	{
		final DateTime[] times = db.getPeriods();
		final ElfValue[][] dayvalues = db.getSampledData();
		final int[] mediane = db.getOpValues();
		final int[] maxs = db.getOpMaxDay();
		final int[] counts = db.getOpValueCount();
		
		Workbook wb = null;
		
		try {
			if (Utils.abortOnExistingAndDontOverwrite(filename))
				return false;
			
			final int maxi = getIndexOfMax(mediane);
			
		    wb = new XSSFWorkbook();
		    
		    CreationHelper createHelper = wb.getCreationHelper();
		    Sheet sheet = wb.createSheet(Config.getResource("TitleStats"));

		    int rown = 0;
		    Row row = sheet.createRow(rown++);
		    Cell cell = row.createCell(0);
		    cell.setCellValue(Config.getResource("TitleDate"));
		    cell = row.createCell(1);
		    cell.setCellValue(Config.getResource("TitleMediana"));
		    cell = row.createCell(2);
		    cell.setCellValue(Config.getResource("TitleMaxM"));
		    cell = row.createCell(3);
		    cell.setCellValue(Config.getResource("TitleNumberOfData"));
		    
		    CellStyle dateStyle1 = wb.createCellStyle();
		    dateStyle1.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy"));
		    CellStyle doubleFormat1 = wb.createCellStyle();
		    DataFormat format1 = wb.createDataFormat();
		    doubleFormat1.setDataFormat(format1.getFormat("0.00"));
		    
		    
		    for (int i=0; i<mediane.length; i++) {
		    	row = sheet.createRow(rown++);
		    	
		    	cell = row.createCell(0);
			    cell.setCellStyle(dateStyle1);
			    cell.setCellValue(Utils.toDateString(dayvalues[i][0].getTime()));
			    
			    cell = row.createCell(1);
			    cell.setCellStyle(doubleFormat1);
			    cell.setCellValue(ElfValue.valueIntToDouble(mediane[i]));
			    
			    cell = row.createCell(2);
			    cell.setCellStyle(doubleFormat1);
			    cell.setCellValue(ElfValue.valueIntToDouble(maxs[i]));
			    
			    cell = row.createCell(3);
			    cell.setCellValue(counts[i]);
		    }
		    
		    // line with DataFunction max
		    row = sheet.createRow(rown++);
		    row = sheet.createRow(rown++);
		    cell = row.createCell(0);
		    cell.setCellValue(Config.getResource("MsgMax")+"("+db.getOperationPerformed().getName()+") - "+Utils.toDateString(times[maxi]));
		    
		    cell = row.createCell(1);
		    cell.setCellStyle(doubleFormat1);
		    cell.setCellValue(ElfValue.valueIntToDouble(mediane[maxi]));
		    
		    cell = row.createCell(2);
		    cell.setCellStyle(doubleFormat1);
		    cell.setCellValue(ElfValue.valueIntToDouble(maxs[maxi]));
		    
		    cell = row.createCell(3);
		    cell.setCellValue(counts[maxi]);
		    
		    
		    // line with max
    		final ElfValue maxvalue = db.getSelectedElfValue(new Comparator<ElfValue>() {
				@Override
				public int compare(ElfValue o1, ElfValue o2) {
					return o1.getValue()-o2.getValue();
				}
    		});
		    row = sheet.createRow(rown++);
		    cell = row.createCell(0);
		    cell.setCellValue(Config.getResource("MsgMax")+"("+Utils.toDateString(maxvalue.getTime())+")");
		    
		    cell = row.createCell(1);
		    cell.setCellStyle(doubleFormat1);
		    cell.setCellValue(MeasurementValue.valueIntToDouble(maxvalue.getValue()));
		    
		    cell = row.createCell(2);
		    cell.setCellStyle(doubleFormat1);
		    cell.setCellValue(MeasurementValue.valueIntToDouble(maxvalue.getMax()));
		    
		    cell = row.createCell(3);
		    cell.setCellValue(counts[maxi]);		    
		    
		    
		    // sheet containing all raw data
		    Sheet sheetdata = wb.createSheet(Config.getResource("TitleSheetDatas"));
		    CellStyle dateTimeStyle2 = wb.createCellStyle();
		    dateTimeStyle2.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));

		    CellStyle doubleFormat2 = wb.createCellStyle();
		    DataFormat format2 = wb.createDataFormat();
		    doubleFormat2.setDataFormat(format2.getFormat("0.00"));
		    
		    rown = 0;
		    row = sheetdata.createRow(rown++);
		    cell = row.createCell(0);
		    cell.setCellValue(Config.getResource("TitleDate"));
		    cell = row.createCell(1);
		    cell.setCellValue(Config.getResource("TitleValue"));
		    cell = row.createCell(2);
		    cell.setCellValue(Config.getResource("TitlePeak"));
		    cell = row.createCell(3);
		    cell.setCellValue(Config.getResource("TitleMediana"));
		    cell = row.createCell(4);
		    cell.setCellValue(Config.getResource("TitleDayMax"));
		    cell = row.createCell(5);
		    cell.setCellValue(Config.getResource("TitleMedianaMax"));
		    cell = row.createCell(6);
		    cell.setCellValue(Config.getResource("TitleSens"));
		    cell = row.createCell(7);
		    cell.setCellValue(Config.getResource("TitleQualityTarget"));
		    cell = row.createCell(8);
		    cell.setCellValue(Config.getResource("TitleAttentionValue"));
		    
		    for (int i=0; i<dayvalues.length; i++) {
		    	final ElfValue[] day = dayvalues[i];

		    	for (int k=0; k<day.length; k++) {
		    		final ElfValue value = day[k];
		    		final DateTime time = value.getTime();
		    		
		    		row = sheetdata.createRow(rown++);
		    		cell = row.createCell(0);
		    		cell.setCellStyle(dateTimeStyle2);
		    		cell.setCellValue(Utils.toDateString(time));
		    		
		    		cell = row.createCell(1);
		    		cell.setCellStyle(doubleFormat2);
		    		
		    		if (value.isValid())
		    			cell.setCellValue(ElfValue.valueIntToDouble(value.getValue()));
		    		else
		    			cell.setCellValue("");
		    		
		    		cell = row.createCell(2);
		    		cell.setCellStyle(doubleFormat2);
		    		if (value.isValid())
		    			cell.setCellValue(ElfValue.valueIntToDouble(value.getMax()));
		    		else
		    			cell.setCellValue("");

		    		cell = row.createCell(3);
		    		cell.setCellStyle(doubleFormat2);
		    		cell.setCellValue(ElfValue.valueIntToDouble(mediane[i]));
		    		
		    		cell = row.createCell(4);
		    		cell.setCellStyle(doubleFormat2);
		    		cell.setCellValue(ElfValue.valueIntToDouble(maxs[i]));
		    		
		    		cell = row.createCell(5);
		    		cell.setCellStyle(doubleFormat2);
		    		cell.setCellValue(ElfValue.valueIntToDouble(mediane[maxi]));
		    		
		    		cell = row.createCell(6);
		    		cell.setCellStyle(doubleFormat2);
		    		cell.setCellValue(sensibilita);
		    		cell = row.createCell(7);
		    		cell.setCellStyle(doubleFormat2);
		    		cell.setCellValue(3);
		    		cell = row.createCell(8);
		    		cell.setCellStyle(doubleFormat2);
		    		cell.setCellValue(10);
		    	}
		    }
		    
		    if (save_grafico) {
			    final int maxline = rown-1;
	
			    sheet = wb.createSheet(Config.getResource("TitleChart"));
	
			    Drawing drawing = sheet.createDrawingPatriarch();  
		        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, 1, 18, 25);  
		  
		        Chart chart = drawing.createChart(anchor);  
		        ChartLegend legend = chart.getOrCreateLegend();  
		        legend.setPosition(LegendPosition.TOP_RIGHT);  
		  
		        ScatterChartData data = chart.getChartDataFactory().createScatterChartData();  
	//	        LineChartData data = chart.getChartDataFactory().createLineChartData();
		        
		        ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);  
		        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);  
		        
		        leftAxis.setMinimum(0.0);
		        leftAxis.setMaximum(10.0);
		        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);  
		  
		        ChartDataSource<String> xs = DataSources.fromStringCellRange(sheetdata, new CellRangeAddress(1, maxline, 0, 0));
		        ChartDataSource<Number> ys_val = DataSources.fromNumericCellRange(sheetdata, new CellRangeAddress(1, maxline, 1, 1));
		        ChartDataSource<Number> ys_sens = DataSources.fromNumericCellRange(sheetdata, new CellRangeAddress(1, maxline, 6, 6));
		        ChartDataSource<Number> ys_qual = DataSources.fromNumericCellRange(sheetdata, new CellRangeAddress(1, maxline, 7, 7));
		        ChartDataSource<Number> ys_att = DataSources.fromNumericCellRange(sheetdata, new CellRangeAddress(1, maxline, 8, 8));
		        
		        ScatterChartSeries data_val = data.addSerie(xs, ys_val);
		        data_val.setTitle(Config.getResource("TitleMeasuredValues"));
		        
		        ScatterChartSeries data_sens = data.addSerie(xs, ys_sens);
		        data_sens.setTitle(Config.getResource("TitleInstrumentSens"));
		        
		        ScatterChartSeries data_qual = data.addSerie(xs, ys_qual);
		        data_qual.setTitle(Config.getResource("TitleQualityTarget"));
		        
		        ScatterChartSeries data_att = data.addSerie(xs, ys_att);
		        data_att.setTitle(Config.getResource("TitleAttentionValue"));
		  
		        chart.plot(data, bottomAxis, leftAxis);  
		    }
		    
		    FileOutputStream fileOut = new FileOutputStream(filename);
		    wb.write(fileOut);
		    fileOut.close();
			return true;
		}
		catch (Exception e) {
			Utils.MessageBox(Config.getResource("MsgErrorXlsx")+"\n"+e.toString(), Config.getResource("TitleError"));
			return false;
		}
		finally {
			if (wb != null)
				try { wb.close(); }
				catch (IOException e) {}
		}
	}
	
	public static void saveCorrenti(String filename, CurrentElfDb db, final double imax, final double ui, final double ub) throws IOException {
		
		if (Utils.abortOnExistingAndDontOverwrite(filename))
			return;

		Workbook wb = new XSSFWorkbook();
	    
		final String nDati = Config.getResource("TitleSheetDatas");
		final String nDelta = Config.getResource("TitleSheetDelta");
		final String nCalcs = Config.getResource("TitleSheetCalcs");
		
	    CreationHelper createHelper = wb.getCreationHelper();
	    Sheet sheet = wb.createSheet(nDati);

	    CellStyle dateStyle1 = wb.createCellStyle();
	    dateStyle1.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy"));
	    
	    CellStyle timeStyle1 = wb.createCellStyle();
	    timeStyle1.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm"));
	    
	    CellStyle doubleFormat1 = wb.createCellStyle();
	    DataFormat format1 = wb.createDataFormat();
	    doubleFormat1.setDataFormat(format1.getFormat("0.00"));
	    
	    CellStyle percFormat1 = wb.createCellStyle();
	    percFormat1.setDataFormat(format1.getFormat("0.00%"));
	    
	    
	    int rown = 0;
	    Row row = sheet.createRow(rown++);
	    
	    final String[] CorrentiColonne = Config.getInstance().getXLSColumnNames();
	    
	    for (int i=0; i<CorrentiColonne.length; i++) {
		    Cell cell = row.createCell(i);
		    cell.setCellValue(CorrentiColonne[i]);
	    }
	    
	    final List<ElfValue> elfs = db.getElfDb();
		final List<CurrentValue> currs = db.getCurrentDb();
		
	    for (int i=0, maxi=Math.max(elfs.size(), currs.size()); i<maxi; i++) {
	    	final ElfValue e = i<=elfs.size() ? elfs.get(i) : null;
	    	final CurrentValue c = i<=currs.size() ? currs.get(i) : null;
	    
	    	row = sheet.createRow(rown++);
	    	int columnnn = 0;
		    
		    if (c == null) {
		    	Cell cell = row.createCell(columnnn++);
		    	cell.setCellValue("");
		    	cell = row.createCell(columnnn++);
		    	cell.setCellValue("");
		    	cell = row.createCell(columnnn++);
		    	cell.setCellValue("");
		    }
		    else {
			    Cell cell = row.createCell(columnnn++);
			    cell.setCellValue(Utils.toDateString(c.getTime()));	// data corrente
			    cell.setCellStyle(dateStyle1);
			    cell = row.createCell(columnnn++);
			    cell.setCellStyle(timeStyle1);
			    cell.setCellValue(Utils.toTimeString(c.getTime()));	// ora corrente
			    cell = row.createCell(columnnn++);
			    cell.setCellStyle(doubleFormat1);
			    cell.setCellValue(ElfValue.valueIntToDouble(c.getValue()));
		    }
		    
		    if (e == null) {
		    	Cell cell = row.createCell(columnnn++);
		    	cell.setCellValue("");
		    	cell = row.createCell(columnnn++);
		    	cell.setCellValue("");
		    	cell = row.createCell(columnnn++);
		    	cell.setCellValue("");
		    }
		    else {
			    Cell cell = row.createCell(columnnn++);
			    cell.setCellStyle(dateStyle1);
			    cell.setCellValue(Utils.toDateString(e.getTime()));	// data corrente
			    cell = row.createCell(columnnn++);
			    cell.setCellStyle(timeStyle1);
			    cell.setCellValue(Utils.toTimeString(e.getTime()));	// ora corrente
			    cell = row.createCell(columnnn++);
			    cell.setCellStyle(doubleFormat1);
			    cell.setCellValue(ElfValue.valueIntToDouble(e.getValue()));	// ora corrente
		    }
	    }
	    
	    final int total_rown = rown;
	    
	    // intermedi
	    {
		    sheet = wb.createSheet(nDelta);
		    rown = 0;
		    int columnnn;
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    Cell cell = row.createCell(columnnn++);
		    cell.setCellValue("dI");
		    cell = row.createCell(columnnn++);
		    cell.setCellValue("dB");
		    cell = row.createCell(columnnn++);
		    cell.setCellValue("dIdB");
		    cell = row.createCell(columnnn++);
		    cell.setCellValue("dI^2");
		    cell = row.createCell(columnnn++);
		    cell.setCellValue("dB^2");
		    cell = row.createCell(columnnn++);
		    cell.setCellValue("Ri = Bi/Ii");
		    cell = row.createCell(columnnn++);
		    cell.setCellValue("Ri^2");

		    for (int i=2; i<=total_rown; i++) {
		    	columnnn = 0;
			    row = sheet.createRow(rown++);
			    cell = row.createCell(columnnn++);
			    cell.setCellFormula(nDati+"!C"+i+"-"+nCalcs+"!$B$2");
			    cell = row.createCell(columnnn++);
			    cell.setCellFormula(nDati+"!F"+i+"-"+nCalcs+"!$B$3");
			    cell = row.createCell(columnnn++);
			    cell.setCellFormula("A"+i+"*B"+i);
			    cell = row.createCell(columnnn++);
			    cell.setCellFormula("A"+i+"*A"+i);
			    cell = row.createCell(columnnn++);
			    cell.setCellFormula("B"+i+"*B"+i);
			    cell = row.createCell(columnnn++);
			    cell.setCellFormula(nDati+"!F"+i+"/"+nDati+"!C"+i);
			    cell = row.createCell(columnnn++);
			    cell.setCellFormula("F"+i+"*F"+i);
		    }
	    }
	    
	    
	    // correlazioni e calcoli finali
	    {
		    sheet = wb.createSheet(nCalcs);
		    rown = 0;
		    int columnnn;
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    Cell cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleNumberOfData"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("COUNT("+nDati+"!C:C)");	// B1
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleCurAvg"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("AVERAGE("+nDati+"!C:C)");	// B2
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleFieldAvg"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("AVERAGE("+nDati+"!F:F)");	// B3
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleRm"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("AVERAGE("+nDelta+"!F:F)");	// B4

		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleImax"));
		    cell = row.createCell(columnnn++);
		    cell.setCellStyle(doubleFormat1);
		    cell.setCellValue(imax);						// B5
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleUI"));
		    cell = row.createCell(columnnn++);
		    cell.setCellStyle(percFormat1);
		    cell.setCellValue(ui);							// B6
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleUB"));
		    cell = row.createCell(columnnn++);
		    cell.setCellStyle(percFormat1);
		    cell.setCellValue(ub);							// B7
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleURm"));
		    cell = row.createCell(columnnn++);
		    cell.setCellStyle(percFormat1);
		    cell.setCellFormula("$B$6*$B$6+$B$7*$B$7-$B$6*$B$7*$B$9");	// B8
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleCorrelation"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("SUM("+nDelta+"!C:C)/SQRT(SUM("+nDelta+"!D:D)*SUM("+nDelta+"!E:E))"); // B9
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleBmax"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("$B$4*$B$5");				// B10
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleEperc"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("$B$8*SUM(delta!G:G)/$B$1/$B$1");		// B11 = u(Rm)^2
		    
		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleUBmax"));
		    cell = row.createCell(columnnn++);
		    cell.setCellFormula("$B$5 * SQRT($B$11 + $B$4*$B$4*$B$6*$B$6)");		// B12		    

		    columnnn = 0;
		    row = sheet.createRow(rown++);
		    cell = row.createCell(columnnn++);
		    cell.setCellValue(Config.getResource("TitleEBmax"));
		    cell = row.createCell(columnnn++);
		    cell.setCellStyle(percFormat1);
		    cell.setCellFormula("2*$B$12/$B$10");		// B13
	    }
	    
	    FileOutputStream fileOut = new FileOutputStream(filename);
	    wb.write(fileOut);
	    wb.close();
	    fileOut.close();
	}
}

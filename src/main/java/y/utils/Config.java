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

package y.utils;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import y.elf.CurrentValue;
import y.elf.ElfValue;
import y.elf.datafunctions.DataFunction;
import y.elf.datafunctions.MedianaFunction;
import y.elf.datafunctions.RmsFunction;
import y.elf.filterfunctions.FilterFunction;
import y.elf.filterfunctions.FilterFunctionConvertToUTC;
import y.elf.filterfunctions.FilterFunctionRemoveDuplicates;

public class Config
{
	public String getInternalResource(String name) {
		try { return resources.getString(name); }
		catch (Exception e) { return "*"+name+"*"; } // kinda debug
	}

	public static String getResource(String name) {
		return instance.getInternalResource(name);
	}
	
	public String getSelectedLanguage() { return resources.getString("Language"); }
	
	private static Config instance;
	public static Config getInstance() { return instance; }
	
	public final static String DEFAULT_CONF_FILENAME = "yElf.conf";
	
	public static void init() { init(DEFAULT_CONF_FILENAME); }
	private static void init(String filename) { instance = new Config(filename); }
	
	private Map<String, ArrayList<ConfigSerie>> const_values;
	
	private int pictureWidth;
	private int pictureHeight;
	
	private double legendSize;
	private double legendX;
	private double legendY;
	
	private double minDataCoverage100;
	
	private int elfValuefieldn;
	private int srbValuefieldn;
	private int currentValuefieldn;
	
	private int instrumentLowELF;
	private int instrumentLowSRB;
	private int currentLowCut;
	
	private Font titleFont;
	private String axisFormat;
	
	private int forceYmin;
	private int forceYmax;
	
	private Locale locale;
	private ResourceBundle resources;
	
	private DataFunction operationSRB;
	private DataFunction operationELF;
	
	private FilterFunction filterSRB;
	private FilterFunction filterELF;
	private FilterFunction filterCurrent;
	
	public static final Font DEFAULT_FONT = new Font("Times New Roman", Font.PLAIN, 22);
	public static final double DEFAULT_LEGRATIO = 0.15;
	public static final String DEFAULT_TIMEFMT = "HH:MM dd-MMM-yyyy";
	public static final String DEFAULT_SHORTTIMEFMT = "dd-MMM-yyyy";
	
	
	private Config(String filename)
	{
		LoadDefaults();
		Load(filename);
	}
	
	private String lastUsedFolder;	// for this session only, will not be saved
	
	
	public void LoadDefaults()
	{
		lastUsedFolder = ".";
		pictureWidth = 1100;
		pictureHeight = 550;
		legendSize = DEFAULT_LEGRATIO;
		legendX = 0.98;
		legendY = 0.98;
		instrumentLowELF = ElfValue.valueDoubleToInt(0.05);
		instrumentLowSRB = ElfValue.valueDoubleToInt(0.5);
		currentLowCut = CurrentValue.valueDoubleToInt(0.1);
		elfValuefieldn = 3;
		srbValuefieldn = 3;
		minDataCoverage100 = 0.75;
		currentValuefieldn = 2;
		titleFont = DEFAULT_FONT;
		axisFormat = DEFAULT_TIMEFMT;
		
		operationSRB = new RmsFunction();
		operationELF = new MedianaFunction();
		
		filterSRB = new FilterFunctionRemoveDuplicates();
		filterELF = new FilterFunctionRemoveDuplicates();
		filterCurrent = new FilterFunctionConvertToUTC();
		
		forceYmin = 0;
		forceYmax = 0;
		
		locale = null;
		resources = null;
		
		const_values = new HashMap<String, ArrayList<ConfigSerie>>();
	}
	
	
	public static ArrayList<ConfigSerie> createDefaultConstValuesSRB()
	{
		ArrayList<ConfigSerie> srb = new ArrayList<ConfigSerie>();
		srb.add(new ConfigSerie("valore di attenzione", ElfValue.valueDoubleToInt(6.0), Color.red, ConfigSerie.USAGES_LIMIT1, true, 0));
		srb.add(new ConfigSerie("valore di soglia", ElfValue.valueDoubleToInt(0.5), Color.orange, ConfigSerie.USAGES_LIMIT2, true, 0));
		srb.add(new ConfigSerie("valori misurati", 0, Color.blue, ConfigSerie.USAGES_VALUE, true, 0));
		srb.add(new ConfigSerie("sensibilità strumentale", ElfValue.valueDoubleToInt(0.3), Color.cyan, ConfigSerie.USAGES_LOW, true, 0));
		srb.add(new ConfigSerie("media giornaliera", 0, Color.green, ConfigSerie.USAGES_DATADAY, true, 0));
		srb.add(new ConfigSerie("media massima", 0, Color.pink, ConfigSerie.USAGES_DATAMAX, true, 0));
		return srb;
	}
	
	public static ArrayList<ConfigSerie> createDefaultConstValuesELF()
	{
		ArrayList<ConfigSerie> em = new ArrayList<ConfigSerie>();
		em.add(new ConfigSerie("valore di attenzione", ElfValue.valueDoubleToInt(10.0), Color.red, ConfigSerie.USAGES_LIMIT1, true, 0));
		em.add(new ConfigSerie("obiettivo di qualità", ElfValue.valueDoubleToInt(3.0), Color.orange, ConfigSerie.USAGES_LIMIT2, true, 0));
		em.add(new ConfigSerie("valori misurati", 0, Color.blue, ConfigSerie.USAGES_VALUE, true, 0));
		em.add(new ConfigSerie("sensibilità strumentale", ElfValue.valueDoubleToInt(0.05), Color.cyan, ConfigSerie.USAGES_LOW, true, 0));
		em.add(new ConfigSerie("mediana giornaliera", 0, Color.green, ConfigSerie.USAGES_DATADAY, true, 0));
		em.add(new ConfigSerie("mediana massima", 0, Color.pink, ConfigSerie.USAGES_DATAMAX, true, 0));
		return em;
	}
	
	private void loadDefaultConstValues()
	{
		const_values = new HashMap<String, ArrayList<ConfigSerie>>();
		const_values.put("elf", createDefaultConstValuesELF());
		const_values.put("srb", createDefaultConstValuesSRB());
	}
	
	public boolean Load() { return Load(DEFAULT_CONF_FILENAME); }
	public boolean Load(String filename)
	{
		BufferedReader bf;
		
		try {
			bf = new BufferedReader(new FileReader(filename));
		}
		catch (Exception e) {
			try { setLocale("it", "IT"); }
			catch (Exception ex) { setLocale("en", "US"); }
			loadDefaultConstValues();
			return true;
		}
		
		try {
			String line;

			while ((line=bf.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#") || line.startsWith(";") || line.startsWith("//"))
					continue;
				
				String[] args = line.split(" ");
				if (args == null || args.length <= 1)
					continue;					
				
				String command = args[0];
				String arg = line.substring(command.length()).trim();
				if (command.endsWith(":"))
					command = command.substring(0, command.length()-1);

				if (command.equals("pictureWidth"))
					pictureWidth = Integer.parseInt(arg);
				else if (command.equals("pictureHeight"))
					pictureHeight = Integer.parseInt(arg);
				else if (command.equals("legendSize"))
					legendSize = Double.parseDouble(arg);
				else if (command.equals("legendX"))
					legendX = Double.parseDouble(arg);
				else if (command.equals("legendY"))
					legendY = Double.parseDouble(arg);
				else if (command.equals("elfValuefieldn"))
					elfValuefieldn = Integer.parseInt(arg);
				else if (command.equals("srbValuefieldn"))
					srbValuefieldn = Integer.parseInt(arg);
				else if (command.equals("currentValuefieldn"))
					currentValuefieldn = Integer.parseInt(arg);
				else if (command.equals("minDataCoverage100"))
					minDataCoverage100 = Double.parseDouble(arg);
				
				else if (command.equals("forceYmin"))
					forceYmin = Integer.parseInt(arg);
				else if (command.equals("forceYmax"))
					forceYmax = Integer.parseInt(arg);		
				
				else if (command.equals("axisFormat"))
					axisFormat = arg;
				else if (command.equals("instrumentLowELF"))
					instrumentLowELF = Integer.parseInt(arg);
				else if (command.equals("instrumentLowSRB"))
					instrumentLowSRB = Integer.parseInt(arg);
				else if (command.equals("currentLowCut"))
					currentLowCut = Integer.parseInt(arg);
				
				else if (command.equals("locale")) {
					final String[] parts = arg.split(";");

					try { setLocale(parts[0], parts[1]); }
					catch (Exception e) { setLocale("en", "US"); }
				}
				
				else if (command.equals("titleFont")) {
					String[] ele = arg.split(",");
					int fontstyle = (ele[1].contains("i")?Font.ITALIC:Font.PLAIN) | (ele[1].contains("b")?Font.BOLD:Font.PLAIN) | Font.PLAIN;
					
					titleFont = new Font(ele[0], fontstyle, Integer.parseInt(ele[2].trim()));
				}
				
				else if (command.equals("constvalue")) {
					final String[] parts = arg.split("/");
					
					ArrayList<ConfigSerie> cs = new ArrayList<ConfigSerie>();
					for (int i=1; i<parts.length; i++)
						cs.add(ConfigSerie.parse(parts[i]));
					
					const_values.put(parts[0], cs);
				}
				else if (command.equals("operationSRB"))
					operationSRB = DataFunction.create(arg);
				else if (command.equals("operationELF"))
					operationELF = DataFunction.create(arg);
				
				else if (command.equals("filterSRB"))
					filterSRB = FilterFunction.create(arg);
				else if (command.equals("filterELF"))
					filterELF = FilterFunction.create(arg);
				else if (command.equals("filterCurrent"))
					filterCurrent = FilterFunction.create(arg);
				
				else
					continue;	// invalid line
			}
			
			return true;
		}
		catch (Exception e) {
			Utils.MessageBox(e.getMessage(), resources==null?"Errore nel file di configurazione":resources.getString("TitleErrorConfig") );
			return false;
		}
		finally {
			if (locale == null)
				try { setLocale("it", "IT"); }
				catch (Exception e) { setLocale("en", "US"); }
			
			if (const_values.size() == 0)
				loadDefaultConstValues();
			
			if (bf != null)
				try { bf.close(); }
				catch (IOException e) {}
		}
	}
	
	public boolean Save() { return Save(DEFAULT_CONF_FILENAME); }
	public boolean Save(String filename)
	{
		if (filename == null || filename.isEmpty())
			return true;
			
		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter(filename));
			
			if (locale != null) bf.write("locale: "+ locale.getLanguage()+";"+locale.getCountry() + "\n");
			bf.write("pictureWidth: " + pictureWidth + "\n");
			bf.write("pictureHeight: " + pictureHeight + "\n");
			
			bf.write("legendSize: " + legendSize + "\n");
			bf.write("legendX: " + legendX + "\n");
			bf.write("legendY: " + legendY + "\n");
			bf.write("elfValuefieldn: " + elfValuefieldn + "\n");
			bf.write("srbValuefieldn: " + srbValuefieldn + "\n");
			bf.write("currentValuefieldn: " + currentValuefieldn + "\n");
			
			bf.write("minDataCoverage100: " + minDataCoverage100 + "\n");
			
			bf.write("instrumentLowELF: " + instrumentLowELF + "\n");
			bf.write("instrumentLowSRB: " + instrumentLowSRB + "\n");
			bf.write("currentLowCut: " + currentLowCut + "\n");
			
			bf.write("operationELF: " + operationELF.getKey() + "\n");
			bf.write("operationSRB: " + operationSRB.getKey() + "\n");
			
			bf.write("filterELF: " + filterELF.getKey() + "\n");
			bf.write("filterSRB: " + filterSRB.getKey() + "\n");
			bf.write("filterCurrent: " + filterCurrent.getKey() + "\n");
			
			if (forceYmin != 0 && forceYmax != 0) {
				bf.write("forceYmin: " + forceYmin + "\n");
				bf.write("forceYmax: " + forceYmax + "\n");
			}
			
			bf.write("titleFont: " + titleFont.getName() + ", "
						+ (titleFont.isBold()?"b":"") + (titleFont.isItalic()?"i":"") + ", "						
						+ titleFont.getSize() + "\n");
			bf.write("axisFormat: " + axisFormat + "\n");
			
			for (String s : const_values.keySet()) {
				bf.write("constvalue: "+s);	// name
				
				final ArrayList<ConfigSerie> thisserie = const_values.get(s);
				
				for (ConfigSerie t : thisserie)
					bf.write("/" + t.toString());
				
				bf.write("\n");
			}
			
			bf.close();
			return true;
		}
		catch (Exception e) { return false; }	
	}
	
	final static String[] columnNames = { "Data", "Valore", "Massimo", "#valori" };
	
	public String[] getConfigColumnNames() {
		return new String[] { getInternalResource("TitleConfigName"), getInternalResource("TitleConfigColor"), getInternalResource("TitleConfigUsage"),
				getInternalResource("TitleConfigValue"), getInternalResource("TitleConfigDrawLine"), getInternalResource("TitleConfigDrawShape") };
	}
	public String[] getXLSColumnNames() {
		return new String[] { getInternalResource("TitleXLSCurrentDate"), getInternalResource("TitleXLSCurrentTime"), getInternalResource("TitleXLSCurrentValue"),
				getInternalResource("TitleXLSFieldDate"), getInternalResource("TitleXLSFieldTime"), getInternalResource("TitleXLSFieldValue") };
	}
	public String[] getDataColumnNames() {
		return new String[] { getInternalResource("TitleDate"), getInternalResource("TitleValue"), getInternalResource("TitleMaxM"), getInternalResource("TitleNumberOfData") };
	}
	
	
	public int getPictureWidth() {
		return pictureWidth;
	}
	public void setPictureWidth(int pictureWidth) {
		this.pictureWidth = pictureWidth;
	}
	public int getPictureHeight() {
		return pictureHeight;
	}
	public void setPictureHeight(int pictureHeight) {
		this.pictureHeight = pictureHeight;
	}
	public double getLegendSize() {
		return legendSize;
	}
	public void setLegendSize(double legendSize) {
		this.legendSize = legendSize;
	}
	public double getLegendX() {
		return legendX;
	}
	public void setLegendX(double legendX) {
		this.legendX = legendX;
	}
	public double getLegendY() {
		return legendY;
	}
	public void setLegendY(double legendY) {
		this.legendY = legendY;
	}
	public Font getTitleFont() {
		return titleFont;
	}
	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}
	public String getAxisFormat() {
		return axisFormat;
	}
	public void setAxisFormat(String axisFormat) {
		this.axisFormat = axisFormat;
	}
	public String getLastUsedFolder() {
		return lastUsedFolder;
	}
	public void setLastUsedFolder(String lastUsedFolder) {
		this.lastUsedFolder = lastUsedFolder;
	}
	public int getInstrumentLowELF() {
		return instrumentLowELF;
	}
	public void setInstrumentLowELF(int instrumentLow) {
		this.instrumentLowELF = instrumentLow;
	}
	public int getInstrumentLowSRB() {
		return instrumentLowSRB;
	}

	public void setInstrumentLowSRB(int instrumentLowSRB) {
		this.instrumentLowSRB = instrumentLowSRB;
	}
	public Map<String, ArrayList<ConfigSerie>> getConst_values() {
		return const_values;
	}
	public int getElfValuefieldn() {
		return elfValuefieldn;
	}
	public void setElfValuefieldn(int elfValuefieldn) {
		this.elfValuefieldn = elfValuefieldn;
	}
	public int getSrbValuefieldn() {
		return srbValuefieldn;
	}
	public void setSrbValuefieldn(int srbValuefieldn) {
		this.srbValuefieldn = srbValuefieldn;
	}
	public int getForceYmin() {
		return forceYmin;
	}
	public void setForceYmin(int forceYmin) {
		this.forceYmin = forceYmin;
	}
	public int getForceYmax() {
		return forceYmax;
	}
	public void setForceYmax(int forceYmax) {
		this.forceYmax = forceYmax;
	}
	
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(String language, String country) {
		setLocale(new Locale(language, country));
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
		resources = ResourceBundle.getBundle("Messages", locale);
	}
	public ResourceBundle getResources() {
		return resources;
	}
	public int getCurrentValuefieldn() {
		return currentValuefieldn;
	}
	public void setCurrentValuefieldn(int currentValuefieldn) {
		this.currentValuefieldn = currentValuefieldn;
	}
	public int getCurrentLowCut() {
		return currentLowCut;
	}
	public void setCurrentLowCut(int currentLowCut) {
		this.currentLowCut = currentLowCut;
	}

	public double getMinDataCoverage100() {
		return minDataCoverage100;
	}

	public void setMinDataCoverage100(double minDataCoverage100) {
		this.minDataCoverage100 = minDataCoverage100;
	}

	public DataFunction getOperationSRB() {
		return operationSRB;
	}

	public void setOperationSRB(DataFunction operationSRB) {
		this.operationSRB = operationSRB;
	}

	public DataFunction getOperationELF() {
		return operationELF;
	}

	public void setOperationELF(DataFunction operationELF) {
		this.operationELF = operationELF;
	}

	public FilterFunction getFilterSRB() {
		return filterSRB;
	}

	public void setFilterSRB(FilterFunction filterSRB) {
		this.filterSRB = filterSRB;
	}

	public FilterFunction getFilterELF() {
		return filterELF;
	}

	public void setFilterELF(FilterFunction filterELF) {
		this.filterELF = filterELF;
	}

	public FilterFunction getFilterCurrent() {
		return filterCurrent;
	}

	public void setFilterCurrent(FilterFunction filterCurrent) {
		this.filterCurrent = filterCurrent;
	}
}

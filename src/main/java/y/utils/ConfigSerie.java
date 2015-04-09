package y.utils;
import java.awt.Color;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

import y.elf.ElfValue;

public class ConfigSerie {
	private String name;
	private Color color;
	
	private int value;
	private int usage;

	private boolean drawLine;
	private float shapeSize;
	
	public final static String[] USAGES = { "none", "value", "limit", "quality", "instrlow", "dataday", "datamax", "custom" };
	public final static int USAGES_NONE = 0;
	public final static int USAGES_VALUE = 1;
	public final static int USAGES_LIMIT1 = 2;
	public final static int USAGES_LIMIT2 = 3;
	public final static int USAGES_LOW = 4;
	public final static int USAGES_DATADAY = 5;
	public final static int USAGES_DATAMAX = 6;
	public final static int USAGES_CUSTOM = 7;
	
	public ConfigSerie()
	{
		this("", 0, Color.BLUE, USAGES_VALUE, true, 0);
	}
	
	public ConfigSerie(String name, int value, Color color, int usage, boolean drawLine, float shapeSize)
	{
		this.name = name;
		this.value = value;
		this.color = color;
		this.usage = usage;
		this.drawLine = drawLine;
		this.shapeSize = shapeSize;
	}
	
	public final static ConfigSerie INVALID = new ConfigSerie("INVALID", 1, Color.black, USAGES_NONE, true, 0);
	
	public static String SEPARATOR1 = "#";
	public static String SEPARATOR2 = ";";
	
	public void add(TimeSeries ts, final RegularTimePeriod date, int value, int dataday, boolean datadayvalid, int datamax) {
		switch (usage) {
			case USAGES_NONE: break;
			case USAGES_VALUE:
				ts.add(date, ElfValue.valueIntToDouble(value));
				break;
			case USAGES_DATADAY:
				if (datadayvalid)
					ts.add(date, ElfValue.valueIntToDouble(dataday));
				break;
			case USAGES_DATAMAX:
				ts.add(date, ElfValue.valueIntToDouble(datamax));
				break;
			case USAGES_CUSTOM:
			case USAGES_LIMIT1:
			case USAGES_LIMIT2:
			case USAGES_LOW:
				ts.add(date, ElfValue.valueIntToDouble(this.value));
		}
	}
	

	public static ConfigSerie parse(String s) {
		final String[] parts1 = s.split(SEPARATOR1);
		final String[] parts2 = parts1[2].split(SEPARATOR2);
		
		try {
			int[] cparts = new int[parts2.length];
			for (int i=0; i<cparts.length; i++)
				cparts[i] = Integer.parseInt(parts2[i]);
			
			final Color color = cparts.length>=4 ? new Color(cparts[0], cparts[1], cparts[2], cparts[3]) : new Color(cparts[0], cparts[1], cparts[2]);
			return new ConfigSerie(parts1[0], Integer.parseInt(parts1[1]), color, usageStringToInt(parts1[3]), parts1[4].equals("true"), Float.parseFloat(parts1[5]) );
		}
		catch (Exception e) {
			return INVALID;
		}
	}
	public String toString() {
		return name+SEPARATOR1+
				value+SEPARATOR1+
				color.getRed()+SEPARATOR2+color.getGreen()+SEPARATOR2+color.getBlue()+SEPARATOR2+color.getAlpha()+SEPARATOR1+
				usageIntToString(usage)+SEPARATOR1+
				(drawLine?"true":"false")+SEPARATOR1+shapeSize;
	}

	public static int getInstrumentalLow(ConfigSerie[] series) {
		for (final ConfigSerie s : series)
			if (s.getUsage() == USAGES_LOW)
				return s.getValue();
		
		return 0;
	}
	
	public static int usageStringToInt(String s) {
		for (int u=0; u<USAGES.length; u++)
			if (USAGES[u].equalsIgnoreCase(s))
				return u;
		
		return -1;
	}
	
	public static String usageIntToString(int i) {
		return i>=0 && i<USAGES.length ? USAGES[i] : "";
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isDrawLine() {
		return drawLine;
	}

	public void setDrawLine(boolean drawLine) {
		this.drawLine = drawLine;
	}
	public int getUsage() {
		return usage;
	}
	public void setUsage(int usage) {
		this.usage = usage;
	}

	public float getShapeSize() {
		return shapeSize;
	}

	public void setShapeSize(float shapeSize) {
		this.shapeSize = shapeSize;
	}
}

package y.elf;

import org.joda.time.DateTime;

public class MeasurementValue implements Comparable<MeasurementValue> {
	private DateTime time;
	private int value;

	public static final int PRECISION = 100;
	
	public static int valueDoubleToInt(double value)  { return (int)Math.round(PRECISION*value); }
	public static double valueIntToDouble(int value)  { return (double)value/PRECISION; }
	public static String valueIntToString(int value)  { return new Double((double)value/PRECISION).toString(); }
	public static int valueStringToInt(String value)  { return (int) (PRECISION*Double.parseDouble(value)); }
	
	public MeasurementValue(DateTime time, int value)
	{
		this.time = time;
		this.value = value;
	}
	public DateTime getTime() {
		return time;
	}
	public void setTime(DateTime time) {
		this.time = time;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	// sort by time
	@Override
	public int compareTo(MeasurementValue o) {
		return this.time.compareTo(o.time);
	}
}

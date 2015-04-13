package y.elf.filterfunctions;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import y.elf.MeasurementValue;

public class FilterFunctionConvertToUTC extends FilterFunction {
	public static final String KEY = "FilterFunctionConvertToUTC";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public <T extends MeasurementValue> void filter(List<T> rawData) {
		doConvert2UTC(rawData, DateTimeZone.getDefault()); 
	}

	private static <T extends MeasurementValue> void doConvert2UTC(List<T> rawData, DateTimeZone fromTZ) {
		for (T t : rawData) {
			final long x = fromTZ.convertLocalToUTC(t.getTime().toInstant().getMillis(), false);
			final DateTime dt = new DateTime(x, DateTimeZone.UTC);
			t.setTime(dt);
		}
	}
}

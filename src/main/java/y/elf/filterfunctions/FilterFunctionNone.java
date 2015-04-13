package y.elf.filterfunctions;

import java.util.List;

import y.elf.MeasurementValue;

public class FilterFunctionNone extends FilterFunction {
	public static final String KEY = "FilterFunctionNone";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public <T extends MeasurementValue> void filter(List<T> rawData) {
	}
}

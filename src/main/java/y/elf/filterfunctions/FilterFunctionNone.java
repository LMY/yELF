package y.elf.filterfunctions;

import java.util.List;

import y.elf.MeasurementValue;

public class FilterFunctionNone implements FilterFunction {
	public static final String NAME = "None";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public <T extends MeasurementValue> void filter(List<T> rawData) {
	}

}

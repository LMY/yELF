package y.elf.filterfunctions;

import java.util.List;

import y.elf.MeasurementValue;

public interface FilterFunction {
	public abstract <T extends MeasurementValue> void filter(List<T> rawData);
	
	public abstract String getName();
	
	public static String[] getNames()
	{
		return new String[] { FilterFunctionNone.NAME, FilterFunctionRemoveDuplicates.NAME, FilterFunctionConvertToUTC.NAME };
	}
	
	public static FilterFunction create(String name)
	{
		if (name.equals(FilterFunctionNone.NAME))
			return new FilterFunctionNone();
		else if (name.equals(FilterFunctionRemoveDuplicates.NAME))
			return new FilterFunctionRemoveDuplicates();
		else if (name.startsWith(FilterFunctionConvertToUTC.NAME))
			return new FilterFunctionConvertToUTC();
		
		else
			return null;
	}
	
}

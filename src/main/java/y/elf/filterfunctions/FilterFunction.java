package y.elf.filterfunctions;

import java.util.Arrays;
import java.util.List;

import y.elf.MeasurementValue;
import y.utils.Config;

public abstract class FilterFunction {
	public abstract <T extends MeasurementValue> void filter(List<T> rawData);
	
	public abstract String getKey();
	public String getName() { return key2Name(getKey()); }
	
	public static String[] getKeys()
	{
		return new String[] { FilterFunctionNone.KEY, FilterFunctionRemoveDuplicates.KEY, FilterFunctionConvertToUTC.KEY };
	}
	
	public static String[] getNames()
	{
		final String[] names = getKeys();
		
		for (int i=0; i<names.length; i++)
			names[i] = key2Name(names[i]);
		
		Arrays.sort(names);
		return names;
	}
	

	public static FilterFunction create(String key)
	{
		if (key.equals(FilterFunctionNone.KEY))
			return new FilterFunctionNone();
		else if (key.equals(FilterFunctionRemoveDuplicates.KEY))
			return new FilterFunctionRemoveDuplicates();
		else if (key.startsWith(FilterFunctionConvertToUTC.KEY))
			return new FilterFunctionConvertToUTC();
		
		else
			return null;
	}
	
	public static FilterFunction createFromName(String name)
	{
		return create(name2Key(name));
	}
	
	
	public static String name2Key(String name) {
		if (name.equals(Config.getResource(FilterFunctionNone.KEY)))
			return FilterFunctionNone.KEY;
		else if (name.equals(Config.getResource(FilterFunctionRemoveDuplicates.KEY)))
			return FilterFunctionRemoveDuplicates.KEY;
		else if (name.equals(Config.getResource(FilterFunctionConvertToUTC.KEY)))
			return FilterFunctionConvertToUTC.KEY;
		else
			return "";
	}
	
	public static String key2Name(String key) {
		if (key.equals(FilterFunctionNone.KEY))
			return Config.getResource(FilterFunctionNone.KEY);
		else if (key.equals(FilterFunctionRemoveDuplicates.KEY))
			return Config.getResource(FilterFunctionRemoveDuplicates.KEY);
		else if (key.equals(FilterFunctionConvertToUTC.KEY))
			return Config.getResource(FilterFunctionConvertToUTC.KEY);
		else
			return "";
	}
}

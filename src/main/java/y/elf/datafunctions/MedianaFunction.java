package y.elf.datafunctions;

import java.util.Arrays;

public class MedianaFunction extends DataFunction {
	public static String KEY = "DataFunctionMedian";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public double function(int[] values) {
		Arrays.sort(values);

		if (values.length == 0)
			return 0;
		else if (values.length % 2 == 0)
			return ((double)values[values.length/2] + (double)values[values.length/2 - 1])/2;
		else
		    return (double)values[values.length/2];
	}
}

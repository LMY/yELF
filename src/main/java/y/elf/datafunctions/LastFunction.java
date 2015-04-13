package y.elf.datafunctions;

public class LastFunction extends DataFunction {
	public static String KEY = "DataFunctionLast";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public double function(int[] values) {
		return values.length > 0 ? values[values.length-1] : 0;
	}
}


package y.elf.datafunctions;

public class FirstFunction extends DataFunction {
	public static String KEY = "DataFunctionFirst";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public double function(int[] values) {
		return values.length > 0 ? values[0] : 0;
	}
}

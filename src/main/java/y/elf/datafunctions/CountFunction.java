package y.elf.datafunctions;

public class CountFunction extends DataFunction {
	public static String KEY = "DataFunctionCount";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public double function(int[] data) {
		return data.length;
	}
}

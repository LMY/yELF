package y.elf.datafunctions;

public class AverageFunction extends DataFunction {
	public static String KEY = "DataFunctionAverage";
	
	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public double function(int[] data) {
		double sum = 0;
		
		for (int i : data)
			sum += i;
		
		return data.length > 0 ? sum/data.length : 0;
	}
}

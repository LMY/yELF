package y.elf.datafunctions;

public class AverageFunction extends DataFunction {
	public static String NAME = "AVERAGE";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public int function(int[] data) {
		int sum = 0;
		
		for (int i : data)
			sum += i;
		
		return data.length > 0 ? sum/data.length : 0;
	}
}

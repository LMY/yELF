package y.elf.datafunctions;

public class LastFunction extends DataFunction {
	public static String NAME = "LAST";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public double function(int[] values) {
		return values.length > 0 ? values[values.length-1] : 0;
	}
}


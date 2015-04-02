package y.elf.datafunctions;

public class FirstFunction extends DataFunction {
	public static String NAME = "FIRST";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public int function(int[] values) {
		return values.length > 0 ? values[0] : 0;
	}
}

package y.elf.datafunctions;

public class CountFunction extends DataFunction {
	public static String NAME = "COUNT";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public double function(int[] data) {
		return data.length;
	}
}

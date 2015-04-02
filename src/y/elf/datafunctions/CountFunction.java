package y.elf.datafunctions;

public class CountFunction extends DataFunction {
	public static String NAME = "COUNT";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public int function(int[] data) {
		return data.length;
	}
}

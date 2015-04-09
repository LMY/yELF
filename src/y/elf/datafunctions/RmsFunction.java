package y.elf.datafunctions;

public class RmsFunction extends DataFunction {
	public static String NAME = "RMS";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public double function(int[] data) {
		double v = 0;
		for (int x : data)
			v += x*x;
		if (data.length > 0)
			v /= data.length;
		return Math.sqrt(v);
	}
}

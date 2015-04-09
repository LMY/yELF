package y.elf.datafunctions;

public class ConstFunction extends DataFunction {
	public static String NAME = "CONST";
	
	@Override
	public String getName() {
		return NAME+" "+value;
	}
	
	private double value;
	
	public ConstFunction(double value) {
		this.value = value;
	}

	@Override
	public double function(int[] data) {
		return value;
	}

	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}

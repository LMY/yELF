package y.elf.datafunctions;

public class ConstFunction extends DataFunction {
	public static String KEY = "DataFunctionConst";
	
	@Override
	public String getKey() {
		return KEY+" "+value;
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

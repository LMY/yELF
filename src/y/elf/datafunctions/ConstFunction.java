package y.elf.datafunctions;

public class ConstFunction extends DataFunction {
	public static String NAME = "CONST";
	
	@Override
	public String getName() {
		return NAME+" "+value;
	}
	
	private int value;
	
	public ConstFunction(int value) {
		this.value = value;
	}

	@Override
	public int function(int[] data) {
		return value;
	}

	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}

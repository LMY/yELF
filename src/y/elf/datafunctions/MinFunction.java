package y.elf.datafunctions;

public class MinFunction extends DataFunction {
	public static String NAME = "MIN";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int function(int[] data) {
		int m = data[0];
		
		for (int i=1; i<data.length; i++)
			if (data[i] < m)
				m = data[i];
		
		return m;
	}
}

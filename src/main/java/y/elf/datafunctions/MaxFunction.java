
package y.elf.datafunctions;

public class MaxFunction extends DataFunction {
	public static String NAME = "MAX";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public double function(int[] data) {
		int m = data[0];
		
		for (int i=1; i<data.length; i++)
			if (data[i] > m)
				m = data[i];
		
		return (double)m;
	}
}

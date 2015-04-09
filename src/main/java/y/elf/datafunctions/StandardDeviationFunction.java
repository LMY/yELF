package y.elf.datafunctions;

public class StandardDeviationFunction extends DataFunction {
	public static String NAME = "STANDARD DEVIATION";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public double function(int[] data) {
		
		if (data.length <= 1)
			return 0;
		
		double M = data[0];							// M[1] = x[1]
		double Q = 0;								// Q[1] = 0
		
		for (int i=1; i<data.length; i++) {
			Q += i*(data[i]-M)*(data[i]-M)/(i+1);	// Q[k] = Q[k-1] + (k-1)/k  (x[k] - M[k-1])^2
			M += (data[i]-M)/(i+1);					// M[k] = M[k-1] + (x[k] - M[k-1])/k
		}
		
		return Math.sqrt(Q/data.length);
	}
}

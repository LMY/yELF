package y.elf.datafunctions;

import java.util.Random;

public class RandomFunction extends DataFunction {
	public static String NAME = "RANDOM";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	private Random random;
	
	public RandomFunction() {
		reset();
	}
	public RandomFunction(long seed) {
		reset(seed);
	}
	
	public void reset() {
		random = new Random();
	}
	public void reset(long seed) {
		random = new Random(seed);
	}
	
	public double function(int[] data) {
//		return random.nextInt();
		return random.nextDouble();
	}
}

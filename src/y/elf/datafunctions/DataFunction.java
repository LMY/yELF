package y.elf.datafunctions;

import java.util.List;

import y.elf.CurrentValue;
import y.elf.ElfValue;

public abstract class DataFunction {

	public static DataFunction create(String name) {
		if (name.equals(AverageFunction.NAME))
			return new AverageFunction();
		else if (name.startsWith(ConstFunction.NAME))
			return new ConstFunction(Integer.parseInt(name.replace(ConstFunction.NAME,  "").trim()));
		else if (name.equals(CountFunction.NAME))
			return new CountFunction();
		else if (name.equals(LastFunction.NAME))
			return new LastFunction();
		else if (name.equals(MaxFunction.NAME))
			return new MaxFunction();
		else if (name.equals(MedianaFunction.NAME))
			return new MedianaFunction();
		else if (name.equals(MinFunction.NAME))
			return new MinFunction();
		else if (name.equals(RandomFunction.NAME))
			return new RandomFunction();
		else if (name.equals(RmsFunction.NAME))
			return new RmsFunction();
		else if (name.equals(StandardDeviationFunction.NAME))
			return new StandardDeviationFunction();
		else
			return null;
	}
	
	public static String[] getNames()
	{
		return new String[] { AverageFunction.NAME, ConstFunction.NAME, CountFunction.NAME, LastFunction.NAME, MaxFunction.NAME,
				MedianaFunction.NAME, MinFunction.NAME, RandomFunction.NAME, RmsFunction.NAME, StandardDeviationFunction.NAME };
	}
	
	
	public abstract String getName();
	
	
	public abstract int function(int[] data);
	
	public int function(List<ElfValue> values)
	{
		return function(values.toArray(new ElfValue[values.size()]));
	}
	
	public int function(ElfValue[] values)
	{
		int valids = 0;
		for (int i=0; i<values.length; i++)
			if (values[i].isValid())
				valids++;			
		
		int[] v = new int[valids];
		int idx = 0;
		for (int i=0; i<values.length; i++)
			if (values[i].isValid())
				v[idx++] = values[i].getValue();		
		return function(v);
	}
	
	public int functionCurr(List<CurrentValue> values)
	{
		return functionCurr(values.toArray(new CurrentValue[values.size()]));
	}
	
	public int functionCurr(CurrentValue[] values)
	{
		int[] v = new int[values.length];
		for (int i=0; i<values.length; i++)
			v[i] = values[i].getValue();		
		return function(v);
	}
}

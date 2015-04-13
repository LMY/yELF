package y.elf.datafunctions;

import java.util.Arrays;
import java.util.List;

import y.elf.CurrentValue;
import y.elf.ElfValue;
import y.utils.Config;

public abstract class DataFunction {

	public abstract String getKey();
	
	public abstract double function(int[] data);
	
	public String getName() { return key2Name(getKey()); }
	
	public static String[] getKeys()
	{
		return new String[] { AverageFunction.KEY, ConstFunction.KEY, CountFunction.KEY, FirstFunction.KEY, LastFunction.KEY, MaxFunction.KEY,
				MedianaFunction.KEY, MinFunction.KEY, RandomFunction.KEY, RmsFunction.KEY, StandardDeviationFunction.KEY };
	}
	
	public static String[] getNames()
	{
		final String[] names = getKeys();
		
		for (int i=0; i<names.length; i++)
			names[i] = key2Name(names[i]);
		
		Arrays.sort(names);
		return names;
	}

	public static DataFunction create(String name) {
		if (name.equals(AverageFunction.KEY))
			return new AverageFunction();
		else if (name.startsWith(ConstFunction.KEY))
			return new ConstFunction(Integer.parseInt(name.replace(ConstFunction.KEY,  "").trim()));
		else if (name.equals(CountFunction.KEY))
			return new CountFunction();
		else if (name.equals(FirstFunction.KEY))
			return new FirstFunction();
		else if (name.equals(LastFunction.KEY))
			return new LastFunction();
		else if (name.equals(MaxFunction.KEY))
			return new MaxFunction();
		else if (name.equals(MedianaFunction.KEY))
			return new MedianaFunction();
		else if (name.equals(MinFunction.KEY))
			return new MinFunction();
		else if (name.equals(RandomFunction.KEY))
			return new RandomFunction();
		else if (name.equals(RmsFunction.KEY))
			return new RmsFunction();
		else if (name.equals(StandardDeviationFunction.KEY))
			return new StandardDeviationFunction();
		else
			return null;
	}
	
	public static DataFunction createFromName(String name)
	{
		return create(name2Key(name));
	}
	
	
	public static String name2Key(String name) {
		if (name.equals(Config.getResource(AverageFunction.KEY)))
			return AverageFunction.KEY;
		else if (name.equals(Config.getResource(ConstFunction.KEY)))
			return ConstFunction.KEY;
		else if (name.equals(Config.getResource(CountFunction.KEY)))
			return CountFunction.KEY;
		else if (name.equals(Config.getResource(FirstFunction.KEY)))
			return FirstFunction.KEY;
		else if (name.equals(Config.getResource(LastFunction.KEY)))
			return LastFunction.KEY;		
		else if (name.equals(Config.getResource(MaxFunction.KEY)))
			return MaxFunction.KEY;
		else if (name.equals(Config.getResource(MedianaFunction.KEY)))
			return MedianaFunction.KEY;		
		else if (name.equals(Config.getResource(MinFunction.KEY)))
			return MinFunction.KEY;
		else if (name.equals(Config.getResource(RandomFunction.KEY)))
			return RandomFunction.KEY;		
		else if (name.equals(Config.getResource(RmsFunction.KEY)))
			return RmsFunction.KEY;
		else if (name.equals(Config.getResource(StandardDeviationFunction.KEY)))
			return StandardDeviationFunction.KEY;		
		else
			return "";
	}
	
	public static String key2Name(String key) {
		if (key.equals(AverageFunction.KEY))
			return Config.getResource(AverageFunction.KEY);
		else if (key.equals(ConstFunction.KEY))
			return Config.getResource(ConstFunction.KEY);
		else if (key.equals(CountFunction.KEY))
			return Config.getResource(CountFunction.KEY);
		else if (key.equals(FirstFunction.KEY))
			return Config.getResource(FirstFunction.KEY);
		else if (key.equals(LastFunction.KEY))
			return Config.getResource(LastFunction.KEY);		
		else if (key.equals(MaxFunction.KEY))
			return Config.getResource(MaxFunction.KEY);
		else if (key.equals(MedianaFunction.KEY))
			return Config.getResource(MedianaFunction.KEY);
		else if (key.equals(MinFunction.KEY))
			return Config.getResource(MinFunction.KEY);
		else if (key.equals(RandomFunction.KEY))
			return Config.getResource(RandomFunction.KEY);
		else if (key.equals(RmsFunction.KEY))
			return Config.getResource(RmsFunction.KEY);
		else if (key.equals(StandardDeviationFunction.KEY))
			return Config.getResource(StandardDeviationFunction.KEY);
		else
			return "";
	}
	
	
	
	
	public double function(List<ElfValue> values)
	{
		return function(values.toArray(new ElfValue[values.size()]));
	}
	
	public double function(ElfValue[] values)
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
	
	public double functionCurr(List<CurrentValue> values)
	{
		return functionCurr(values.toArray(new CurrentValue[values.size()]));
	}
	
	public double functionCurr(CurrentValue[] values)
	{
		int[] v = new int[values.length];
		for (int i=0; i<values.length; i++)
			v[i] = values[i].getValue();		
		return function(v);
	}
}

package y.elf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import y.elf.datafunctions.DataFunction;
import y.utils.Config;


public class CurrentDb extends MeasurementDb {

	public static CurrentDb load(String[] filenames, int fieldn, Config config) {
		final CurrentDb db = new CurrentDb();
		db.add(filenames, fieldn, -1);
		return db.filterLowCut(config);
	}
	
	
	private List<CurrentValue> rawData;
	private CurrentValue[][] sampledData;
	
	public CurrentDb() {
		super();
	}
	
	@Override
	public TimeValue getStartDate() {
		try {
			if (rawData != null)
				return rawData.get(0).getTime();
			else
				return sampledData[0][0].getTime();
		}
		catch (Exception e) { return new TimeValue(2000, 1, 1, 0, 0); }
	}
	
	@Override
	public TimeValue getEndDate() {
		try {
			if (rawData != null)
				return rawData.get(rawData.size()-1).getTime();
			else
				return sampledData[sampledData.length-1][sampledData[sampledData.length-1].length-1].getTime();
		}
		catch (Exception e) { return new TimeValue(2999, 12, 12, 23, 59); }
	}

	@Override
	public boolean add(String filename, int valuefieldn, int low) {
		final List<CurrentValue> unfiltered = DbReader.readCurrentFile(filename, valuefieldn, low);
		if (unfiltered == null)
			return false;
		
		rawData.addAll(unfiltered);
		return true;
	}


	@Override
	public void sort() {
		Collections.sort(rawData);
		perform(getPeriodDivision(), getOperationPerformed());
	}
	
	
	@Override
	protected void sample(PeriodType period) {
		final List<List<CurrentValue>> sampledList = cut(rawData, period);
		sampledData = new CurrentValue[sampledList.size()][];
		
		for (int i=0; i<sampledList.size(); i++)
			sampledData[i] = sampledList.get(i).toArray(new CurrentValue[sampledList.get(i).size()]);
	}

	@Override
	protected void calculate(DataFunction function) {
		opValues = new int[periods.length];
		opValueCount = new int[periods.length];
		opMaxDay = new int[periods.length];
		
		for (int i=0; i<sampledData.length; i++) {
			opValues[i] = function.functionCurr(sampledData[i]);
			opValueCount[i] = sampledData[i].length;
			opMaxDay[i] =  maxx(sampledData[i]);
		}
	}

	private static int maxx(CurrentValue[] currentValues) {
		int maxv = -1;
		for (CurrentValue c : currentValues) {
				final int v = c.getValue();
				if (v > maxv)
					maxv = v;
		}
		
		return maxv;
	}

	@Override
	public void clearRaw() {
		rawData = new ArrayList<CurrentValue>();
	}

	@Override
	public void clearSampled() {
		sampledData = new CurrentValue[0][0];
	}

	public CurrentDb filter(TimeValue from, TimeValue to) {
		List<CurrentValue> newvalues = new ArrayList<CurrentValue>();

		for (int i=0, imax=rawData.size(); i<imax; i++) {
			final CurrentValue value = rawData.get(i);
			final TimeValue day = value.getTime(); //.getDate(); bugfix: 2.1.6

			if (day.compareTo(from) >= 0 && day.compareTo(to) <= 0)	// is between [from,to]
				newvalues.add(value);
		}

		final CurrentDb newdb = new CurrentDb();						// create new db
		newdb.rawData = newvalues;									// assign raw values
		newdb.perform(getPeriodDivision(), getOperationPerformed());	// perform same operation
		return newdb;
	}
	
	public CurrentDb filterLowCut(Config config)
	{
		final int currentLowCut = config.getCurrentLowCut();
		final List<CurrentValue> filtered = new ArrayList<CurrentValue>();
		
		for (CurrentValue c : rawData)
			if (c.getValue() >= currentLowCut)
				filtered.add(c);
		
		final CurrentDb newdb = new CurrentDb();						// create new db
		newdb.rawData = filtered;										// assign raw values
		newdb.perform(getPeriodDivision(), getOperationPerformed());	// perform same operation
		return newdb;
	}
	
	public int size() { return rawData.size(); }
	
	public CurrentValue[][] getSampledData() {
		return sampledData;
	}
}
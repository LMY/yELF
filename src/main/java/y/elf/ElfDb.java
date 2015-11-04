package y.elf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import y.elf.datafunctions.DataFunction;
import y.elf.filterfunctions.FilterFunction;


public class ElfDb extends MeasurementDb
{
	public static ElfDb load(String[] filenames, int fieldn, int low, FilterFunction filter) {
		final ElfDb db = new ElfDb();
		db.add(filenames, fieldn, low, filter);
		return db;
	}
	
	
	private List<ElfValue> rawData;
	private ElfValue[][] sampledData = null;

	public ElfDb() {
		super();
	}
	
	@Override
	public List<? extends MeasurementValue> getRawData() { return rawData; }


	@Override
	public DateTime getStartDate() {
		try {
			if (rawData != null && !rawData.isEmpty())
				return rawData.get(0).getTime();
			else
				return sampledData[0][0].getTime();
		}
		catch (Exception e) { return new DateTime(2000, 1, 1, 0, 0); }
	}
	
	@Override
	public DateTime getEndDate() {
		try {
			if (rawData != null && !rawData.isEmpty())
				return rawData.get(rawData.size()-1).getTime();
			else
				return sampledData[sampledData.length-1][sampledData[sampledData.length-1].length-1].getTime();
		}
		catch (Exception e) { return new DateTime(2999, 12, 12, 23, 59); }
	}
	
	@Override
	protected boolean add(String filename, int valuefieldn, int low) {
		List<ElfValue> newlista = DbReader.readFile(filename, low, valuefieldn);
		
		// if no value were readen, try to fallback to old format
		if (newlista.size() == 0)
			newlista = DbReader.readOldCentralineFile(filename, low, valuefieldn);
		// if still empty, read failed
		if (newlista.size() == 0)
			return false;
		
		rawData.addAll(newlista);
		return true;
	}

	@Override
	protected void sample(PeriodType period) {
		final List<List<ElfValue>> sampledList = cut(rawData, period);
		sampledData = new ElfValue[sampledList.size()][];
		
		for (int i=0; i<sampledList.size(); i++)
			sampledData[i] = sampledList.get(i).toArray(new ElfValue[sampledList.get(i).size()]);
	}
	
	@Override
	public void applyFilter(FilterFunction filter) {
		filter.filter(rawData);
	}

	@Override
	protected void calculate(DataFunction function) {
		opValues = new int[periods.length];
		opValueCount = new int[periods.length];
		opMaxDay = new int[periods.length];
		
		for (int i=0; i<periods.length; i++) {
			opValueCount[i] = count_valids(sampledData[i]);

			if (opValueCount[i] > 0) {
				opValues[i] = (int)Math.round(function.function(sampledData[i]));
				opMaxDay[i] = maxx(sampledData[i]);
			}
			else {
				opValues[i] = 0;
				opMaxDay[i] = 0;
			}
		}
	}

	private static int maxx(ElfValue[] values)
	{
		int maxv = -1;
		for (int i=0; i<values.length; i++)
			if (values[i].isValid()) {
				final int v = values[i].getValue();
				if (v > maxv)
					maxv = v;
		}
		
		return maxv;
	}

	
	private static int count_valids(ElfValue[] thisday_values)
	{
		int cnt = 0;
		for (int i=0; i<thisday_values.length; i++)
			if (thisday_values[i].isValid())
				cnt++;
		
		return cnt;
	}
	
	
	@Override
	public void clearRaw() {
		rawData = new ArrayList<ElfValue>();
	}

	@Override
	public void clearSampled() {
		sampledData = new ElfValue[0][0];
	}

	@Override
	public void sort() {
		Collections.sort(rawData);
		perform(getPeriodDivision(), getOperationPerformed());
	}
	
	public ElfDb filter(DateTime from, DateTime to) {
		return filter(from, to, getPeriodDivision(), getOperationPerformed());
	}
	
	public ElfDb filter(DateTime from, DateTime to, PeriodType periodCut, DataFunction operation) {
		List<ElfValue> newvalues = new ArrayList<ElfValue>();

		for (int i=0, imax=rawData.size(); i<imax; i++) {
			final ElfValue value = rawData.get(i);
			final DateTime day = value.getTime(); //.getDate(); bugfix: 2.1.6

			if (day.compareTo(from) >= 0 && day.compareTo(to) <= 0)	// is between [from,to]
				newvalues.add(value);
		}
		
		final ElfDb newdb = new ElfDb();								// create new db
		newdb.rawData = newvalues;										// assign raw values
		newdb.perform(getPeriodDivision(), getOperationPerformed());	// perform same operation
		return newdb;
	}

	public int size() { return rawData.size(); }
	
	public ElfValue[][] getSampledData() {
		return sampledData;
	}
	
	public ElfValue getSelectedElfValue(Comparator<ElfValue> comparator) {
		ElfValue ret = null;
		
		if (rawData != null && !rawData.isEmpty()) {
			for (ElfValue e : rawData)
				if ((ret == null || comparator.compare(e, ret) > 0) && e.isValid())
					ret = e;
		}
		else
			if (sampledData != null)
				for (int y=0; y<sampledData.length; y++)
					for (int x=0; x<sampledData[y].length; x++)
						// consider only valid data
						if ((ret == null || comparator.compare(sampledData[y][x], ret) > 0) && sampledData[y][x].isValid())
							ret = sampledData[y][x];
		
		return ret;
	}
}

package y.elf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import y.elf.datafunctions.DataFunction;
import y.elf.filterfunctions.FilterFunction;
import y.utils.Config;

public abstract class MeasurementDb {

	public MeasurementDb()
	{
		clear();
	}
	
	public enum PeriodType { NONE, FROMTO, DAILY, WEEKLY, MONTHLY, YEARLY };
	
	private DataFunction operationPerformed;
	private PeriodType periodDivision;
	protected DateTime[] periods;
	protected int[] opValues;
	protected int[] opValueCount;
	protected int[] opMaxDay;
	private boolean[] opValid;
	private int maxidx;

	
	public boolean add(String[] filenames, int valuefieldn, int low, FilterFunction filter)
	{
		boolean ret = true;
		
		for (String s : filenames)
			ret &= add(s, valuefieldn, low);
		
		if (filter != null)
			applyFilter(filter);
		
		sort();
		
		return ret;
	}
	
	/**
	 * read a given file, adding value to db
	 */
	protected abstract boolean add(String filename, int valuefieldn, int low);
	
	/**
	 * divide data into periods.
	 * protected, since calculate(f) must be called later, public interface for this function is perform()
	 */
	protected abstract void sample(PeriodType period);
	
	/**
	 * apply data function to periods
	 */
	protected abstract void calculate(DataFunction function);
	
	/**
	 * unload raw/temporary data.
	 * The data from which sample() divides the periods will be freed.
	 * 
	 * NOTE that it will not be possible to call sample() after deleteRaw(). Therefore, empty implementation is permitted and ok.
	 */
	public abstract void clearRaw();
	
	/**
	 * clear sampled data
	 */
	public abstract void clearSampled();

	/**
	 * sort raw data, delete duplicated times
	 */
	public abstract void sort();
	
	
	/**
	 * apply validity filter (remove duplicates, convert to utc, ...) 
	 */
	public abstract void applyFilter(FilterFunction filter);
	
	
	public abstract List<? extends MeasurementValue> getRawData();
	
	/**
	 * clear entire db
	 */
	public void clear()
	{
		periodDivision = PeriodType.NONE;
		periods = new DateTime[0];
		opValues = new int[0];
		opValid = new boolean[0];
		opValueCount = new int[0];
		opMaxDay = new int[0];
		maxidx = -1;
		periodDivision = PeriodType.NONE;
		operationPerformed = null;
		
		clearRaw();
		clearSampled();
	}
	
	
	public abstract DateTime getStartDate();
	public abstract DateTime getEndDate();
	
	
	public void perform(PeriodType period, DataFunction function)
	{
		periodDivision = period;
		operationPerformed = function;
		
		if (period != PeriodType.NONE) {
			sample(period);
			
			if (function != null) {
				calculate(function);
				calculateMaxIdxAndValids();
			}
		}
	}
	
	private void calculateMaxIdxAndValids()
    {
		if (opValues == null || opValues.length <= 0) {
			opValueCount = new int[0];
			maxidx = -1;
			return;
		}
		
		opValid = new boolean[opValues.length];
		
        int mm = 0;
    	for (int i=1; i<opValueCount.length; i++)
    		if (opValueCount[i] > opValueCount[mm])
    			mm = i;
    	
        final double THRES_FACTOR = Config.getInstance().getMinDataCoverage100();
    	final int thres = (int)Math.round(opValueCount[mm]*THRES_FACTOR);
    	maxidx=0;
    	for (int i=0; i<opValues.length; i++)
    		if (opValueCount[i] >= thres) {
    			opValid[i] = true;
    			
    			if (opValues[i] > opValues[maxidx])
    				maxidx = i;
    		}
    		else
    			opValid[i] = false;
    }
	
	/**
	 * cut raw data into specified periods
	 */
	protected <T extends MeasurementValue> List<List<T>> cut(Collection<T> values, PeriodType period)
	{
		switch (period) {
			case FROMTO:
			case DAILY: return cutDo(values, 2);
			case WEEKLY: return cutDoWeeks(values);
			case MONTHLY: return cutDo(values, 1);
			case YEARLY: return cutDo(values, 0);
			default: return null;
		}
	}
	
	private <T extends MeasurementValue> List<List<T>> cutDoWeeks(Collection<T> values)
	{
		final Map<Long, List<T>> map = new HashMap<Long, List<T>>();
		
		for (T v: values) {
			final DateTime time = v.getTime();
			
			final Long week = (long) ((time.year().get()-2000)*60 + time.getWeekOfWeekyear());
			
			List<T> day = map.get(week);
			if (day == null) {
				day = new ArrayList<T>();
				map.put(week, day);
			}
			
			day.add(v);
		}
		
		// sorted periods
		periods = map.keySet().toArray(new DateTime[map.keySet().size()]);
		Arrays.sort(periods);
		
		// map to list
		final List<List<T>> ret = new ArrayList<List<T>>();
		for (DateTime t : periods) {
			final List<T> d = map.get(t);
			Collections.sort(d);
			ret.add(d);
		}

		return ret;
	}
	
	private <T extends MeasurementValue> List<List<T>> cutDo(Collection<T> values, int cutType)
	{
		final Map<DateTime, List<T>> map = new HashMap<DateTime, List<T>>();
		
		for (T v: values) {
			final DateTime time = v.getTime();
			
			final DateTime keytime = new DateTime(time.year().get(), cutType >= 1 ? time.monthOfYear().get() : 0, cutType >= 2 ? time.dayOfMonth().get() : 0,
																 cutType >= 3 ? time.hourOfDay().get() : 0, cutType >= 4 ? time.minuteOfHour().get() : 0);
			
			List<T> day = map.get(keytime);
			if (day == null) {
				day = new ArrayList<T>();
				map.put(keytime, day);
			}
			
			day.add(v);
		}
		
		// sorted periods
		periods = map.keySet().toArray(new DateTime[map.keySet().size()]);
		Arrays.sort(periods);
		
		// map to list
		final List<List<T>> ret = new ArrayList<List<T>>();
		for (DateTime t : periods) {
			final List<T> d = map.get(t);
			Collections.sort(d);
			ret.add(d);
		}

		return ret;
	}


	public PeriodType getPeriodDivision() {
		return periodDivision;
	}

	public DataFunction getOperationPerformed() {
		return operationPerformed;
	}

	public int[] getOpValues() {
		return opValues;
	}

	public boolean[] getOpValid() {
		return opValid;
	}

	public int[] getOpValueCount() {
		return opValueCount;
	}

	public int[] getOpMaxDay() {
		return opMaxDay;
	}
	
    public int getMaxidx() {
		return maxidx;
	}

	public DateTime[] getPeriods() {
		return periods;
	}
}

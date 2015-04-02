package y.elf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import y.elf.datafunctions.DataFunction;
import y.utils.Config;

public abstract class MeasurementDb {

	public MeasurementDb()
	{
		clear();
	}
	
	public enum PeriodType { NONE, FROMTO, DAILY, WEEKLY, MONTHLY, YEARLY };
	
	private DataFunction operationPerformed;
	private PeriodType periodDivision;
	protected TimeValue[] periods;
	protected int[] opValues;
	protected int[] opValueCount;
	protected int[] opMaxDay;
	private boolean[] opValid;
	private int maxidx;

	
	public boolean add(String[] filenames, int valuefieldn, int low)
	{
		boolean ret = true;
		
		for (String s : filenames)
			ret &= add(s, valuefieldn, low);
		
		return ret;
	}
	
	/**
	 * read a given file, adding value to db
	 */
	public abstract boolean add(String filename, int valuefieldn, int low);
	
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
	 * clear entire db
	 */
	public void clear()
	{
		periodDivision = PeriodType.NONE;
		periods = new TimeValue[0];
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
	
	
	public abstract TimeValue getStartDate();
	public abstract TimeValue getEndDate();
	
	
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
			case WEEKLY: return cutDo(values, 1); // TODO: cut weeks
			case MONTHLY: return cutDo(values, 1);
			case YEARLY: return cutDo(values, 0);
			default: return null;
		}
	}
	
	private <T extends MeasurementValue> List<List<T>> cutDo(Collection<T> values, int cutType)
	{
		final Map<TimeValue, List<T>> map = new HashMap<TimeValue, List<T>>();
		
		for (T v: values) {
			final TimeValue time = v.getTime();
			final TimeValue keytime = new TimeValue(time.getY(), cutType >= 1 ? time.getM() : 0, cutType >= 2 ? time.getD() : 0,
																 cutType >= 3 ? time.getH() : 0, cutType >= 4 ? time.getMm() : 0);
			
			List<T> day = map.get(keytime);
			if (day == null) {
				day = new ArrayList<T>();
				map.put(keytime, day);
			}
			
			day.add(v);
		}
		
		// sorted periods
		periods = map.keySet().toArray(new TimeValue[map.keySet().size()]);
		Arrays.sort(periods);
		
		// map to list
		final List<List<T>> ret = new ArrayList<List<T>>();
		for (TimeValue t : periods) {
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

	public TimeValue[] getDays() {
		return periods;
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

	public TimeValue[] getPeriods() {
		return periods;
	}
}

package y.elf.filterfunctions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import y.elf.MeasurementValue;

public class FilterFunctionRemoveDuplicates implements FilterFunction {
	public static final String NAME = "Remove duplicates";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	
	@Override
	public <T extends MeasurementValue> void filter(List<T> rawData) {
		HashSet<DateTime> times = new HashSet<DateTime>();
		
		for (Iterator<T> mv = rawData.iterator(); mv.hasNext(); ) {
			final T cv = mv.next();
			final DateTime dt = cv.getTime();
			
			if (times.contains(dt))
				mv.remove();
			else
				times.add(dt);
		}
	}
}

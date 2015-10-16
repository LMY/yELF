/*
	This file is part of yAmbElf.

	yAmbElf is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	yAmbElf is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with yAmbElf.  If not, see <http://www.gnu.org/licenses/>.
	
	Copyright 2014 Miro Salvagni
*/
package y.elf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;


public class DbReader {

	public final static String DEFAULT_CSV_SEPARATOR = ";";
	public final static String DEFAULT_DATE_SEPARATOR = "/";
	public final static String DEFAULT_TIME_SEPARATOR = ":";
	
	
	public static List<ElfValue> readFiles(String[] filenames, int valuefieldn, int low)
	{
		List<ElfValue> ret = new ArrayList<ElfValue>();
		
		if (filenames == null || filenames.length == 0)
			return ret;
		
		for (int i=0; i<filenames.length; i++)
			ret.addAll(readFile(filenames[i], low, valuefieldn));
		
		Collections.sort(ret);

		return ret;
	}
	
	public static List<CurrentValue> readCurrentFiles(String[] filenames, int valuefieldn, int low)
	{
		List<CurrentValue> ret = new ArrayList<CurrentValue>();
		
		if (filenames == null || filenames.length == 0)
			return ret;
		
		for (int i=0; i<filenames.length; i++)
			ret.addAll(readCurrentFile(filenames[i], low, valuefieldn));
		
		Collections.sort(ret);

		return ret;
	}
	
	
	/**
	 * read a single file
	 * @param filename 
	 * @param defaultvalue instrument "low" value
	 * @param fieldnn number of field of value in input file
	 * @return list of values
	 */
	public static List<ElfValue> readFile(String filename, int defaultvalue, int fieldnn) {
		return readFile(filename, defaultvalue, fieldnn, DEFAULT_CSV_SEPARATOR, DEFAULT_DATE_SEPARATOR, DEFAULT_TIME_SEPARATOR);
	}
	
	public static List<ElfValue> readFile(String filename, int defaultvalue, int fieldnn, String separatorCsv, String separatorDate, String separatorTime)
	{
		List<ElfValue> list = new ArrayList<ElfValue>();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line;
			
			while ((line=reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				
				try {
					final String[] parts = line.split(separatorCsv);
					final String[] date = parts[0].split(separatorDate);
					final String[] hour = parts[1].split(separatorTime);
					
					DateTime time = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]),
							Integer.parseInt(hour[0]), Integer.parseInt(hour[1]) );
					
					boolean valid;
					if (fieldnn <= 2)	// if parts[2] is data, there is no "valid" field
						valid = true;
					else				// otherwise, read "valid" field. BUT, only if there are enough parts 
						valid = parts.length > 2 ? parts[2].isEmpty() : true;
					
					int value = translateValue(parts[fieldnn], defaultvalue);
					int maxvalue = fieldnn+1 <= parts.length-1 ? translateValue(parts[fieldnn+1], defaultvalue) : -1;

					if (value > 0)
						list.add(new ElfValue(time, value, maxvalue, valid));
					else
						list.add(new ElfValue(time, 0, 0, false));
				}
				catch (Exception e) { continue; } // on error, skip line
			}
		}
		catch (Exception e) {}
		finally {
			if (reader != null)
				try { reader.close();}
				catch (IOException e) {}
		}
		
		return list;
	}
	
	
	public static List<ElfValue> readOldCentralineFile(String filename, int defaultvalue, int fieldnn)
	{
		final String separatorDate = "/";
		final String separatorTime = ":";
		final int UNUSED = Integer.MIN_VALUE;
		
		List<ElfValue> list = new ArrayList<ElfValue>();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line;
			MutableDateTime lasttime = new MutableDateTime(0, 1, 1, 0, 0, 0, 0);
			final MutableDateTime invalidtime = new MutableDateTime(0, 1, 1, 0, 0, 0, 0);
			
			while ((line=reader.readLine()) != null) {
				if (line.isEmpty())
					continue;

				try {
					boolean valid = true;
					final String[] parts = tokenize(line);
					int value = UNUSED;
					int maxvalue = 0;
					
					for (int i=0; i<parts.length; i++) {
						if (parts[i].equals("*"))
							valid = false;
						else if (parts[i].contains(separatorDate)) {
							final String[] date = parts[i].split(separatorDate); 
							
							lasttime.setDayOfMonth(Integer.parseInt(date[0]));
							lasttime.setMonthOfYear(Integer.parseInt(date[1]));
							lasttime.setYear(Integer.parseInt(date[2]));
						}
						else if (parts[i].contains(separatorTime)) {
							final String[] hour = parts[i].split(separatorTime);
							lasttime.setHourOfDay(Integer.parseInt(hour[0]));
							lasttime.setMinuteOfHour(Integer.parseInt(hour[1]));
						}
						else if (value == UNUSED)
							value = translateValue(parts[i], defaultvalue);
						else
							maxvalue = translateValue(parts[i], defaultvalue);
					}
					
					if (lasttime.equals(invalidtime))	// invalid line (header)
						continue;
					
					final DateTime currentdatetime = lasttime.toDateTime();
					
					if (value > 0) {
						if (fieldnn == 2)
							list.add(new ElfValue(currentdatetime, maxvalue, value, valid));
						else
							list.add(new ElfValue(currentdatetime, value, maxvalue, valid));
					}
					else
						list.add(new ElfValue(currentdatetime, 0, 0, false));
				}
				catch (Exception e) { continue; } // on error, skip line
			}
		}
		catch (Exception e) {
//			System.out.println(e.getMessage());
		}
		finally {
			if (reader != null)
				try { reader.close();}
				catch (IOException e) {}
		}
		
		return list;
	}
	
	public static String[] tokenize(String line)
	{
		final List<String> args = new ArrayList<String>();
		final StringTokenizer tok = new StringTokenizer(line);
		
		while (tok.hasMoreTokens())
			args.add(tok.nextToken());
		
		return args.toArray(new String[args.size()]);
	}
	
	

	public static List<CurrentValue> readCurrentFile(String filename, int fieldnn, int defaultvalue) {
		return readCurrentFile(filename, fieldnn, defaultvalue, DEFAULT_CSV_SEPARATOR, /*DEFAULT_DATE_SEPARATOR,*/ DEFAULT_TIME_SEPARATOR);
	}
	
	
	private enum CurrrentDateFormat { NOTSET, DDMMYYYY, YYYYMMDD };
	
	private static Pattern pattern1 = Pattern.compile("\\d{1,2}(.)\\d{1,2}\\1\\d{4}");
	private static Pattern pattern2 = Pattern.compile("\\d{4}(.)\\d{1,2}\\1\\d{1,2}");
	
	public static List<CurrentValue> readCurrentFile(String filename, int fieldnn, int defaultvalue, String separatorCsv, String separatorTime)
	{
		List<CurrentValue> list = new ArrayList<CurrentValue>();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line;
			String separatorDate = "";
			
			CurrrentDateFormat dateformat = CurrrentDateFormat.NOTSET;
			
			while ((line=reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				
				try {
					final String[] parts = line.split(separatorCsv);
					
					if (separatorDate.isEmpty()) {
						Matcher m = pattern1.matcher(parts[0]);
						if (m.find())
							separatorDate = m.group(1);
						else {
							m = pattern2.matcher(parts[0]);
							if (m.find())
								separatorDate = m.group(1);
							else
								continue;
						}
					}
					
					final String[] date = parts[0].split(separatorDate);
					final String[] hour = parts[1].split(separatorTime);
					
					if (dateformat == CurrrentDateFormat.NOTSET) {
						// if parse fails, or is out of bounds, exception will skip the line
						Integer.parseInt(date[0]);
						Integer.parseInt(date[1]);
						Integer.parseInt(hour[0]);
						Integer.parseInt(hour[1]);
						final int dd = Integer.parseInt(date[2]);
						dateformat = dd > 31 ? CurrrentDateFormat.YYYYMMDD : CurrrentDateFormat.DDMMYYYY;
					}
					
					DateTime time = new DateTime(Integer.parseInt(date[dateformat == CurrrentDateFormat.YYYYMMDD ? 2 : 0]),
							Integer.parseInt(date[1]),
							Integer.parseInt(date[dateformat == CurrrentDateFormat.YYYYMMDD ? 0 : 2]),
							Integer.parseInt(hour[0]), Integer.parseInt(hour[1]) );
					
					final int value = translateValue(parts[fieldnn], defaultvalue);
					
					if (value >= 0)
						list.add(new CurrentValue(time, value));
				}
				catch (Exception e) { continue; } // on error, skip line
			}
		}
		catch (Exception e) {}
		finally {
			if (reader != null)
				try { reader.close();}
				catch (IOException e) {}
		}
		
		return list;
	}
	
	
	
	/**
	 * translate an elfvalue: string "low" is defaultvalue, anything not a number is -1
	 * @param value string to be parsed
	 * @param defaultvalue low value
	 * @return translated value
	 */
	public static int translateValue(String value, int defaultvalue)
	{
		if (value.equalsIgnoreCase("low"))
			return defaultvalue;
		else
			try { return ElfValue.valueStringToInt(value); }
			catch (Exception e) { return -1; } 
	}
}

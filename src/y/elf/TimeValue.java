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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TimeValue implements Comparable<TimeValue>
{
	private int y;
	private int m;
	private int d;
	private int h;
	private int mm;
	
	public TimeValue(Date date)
	{
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    
	    this.y = cal.get(Calendar.YEAR);
		this.m = cal.get(Calendar.MONTH)+1;
		this.d = cal.get(Calendar.DAY_OF_MONTH);
		this.h = cal.get(Calendar.HOUR_OF_DAY);
		this.mm = cal.get(Calendar.MINUTE);
	}
	
	public TimeValue(int y, int m, int d, int h, int mm)
	{
		this.y = y;
		this.m = m;
		this.d = d;
		this.h = h;
		this.mm = mm;
	}
	
	public TimeValue(TimeValue value)
	{
		this(value.y, value.m, value.d, value.h, value.mm);
	}
	
	public TimeValue getDate()
	{
		return new TimeValue(y, m, d, 0, 0);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getM() {
		return m;
	}

	public void setM(int m) {
		this.m = m;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getMm() {
		return mm;
	}

	public void setMm(int mm) {
		this.mm = mm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + d;
		result = prime * result + h;
		result = prime * result + m;
		result = prime * result + mm;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeValue other = (TimeValue) obj;
		if (d != other.d)
			return false;
		if (h != other.h)
			return false;
		if (m != other.m)
			return false;
		if (mm != other.mm)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	public String toDateString() { return String.format("%02d", d)+"/"+String.format("%02d", m)+"/"+y; }
	public String toTimeString() { return String.format("%02d", h)+":"+String.format("%02d", mm); }
	public String toDateTimeString() { return toTimeString()+" "+toDateString(); }

	public long toLong() { return mm+60*(h+24*(d+31*(m+12*y))); }
	
	@Override
	public int compareTo(TimeValue arg0) {
		return (int) (toLong()-arg0.toLong());
		
//		if (y != arg0.y)
//			return (y-arg0.y)*12*30*24*60;
//		if (m < arg0.m)
//			return (m-arg0.m)*30*24*60;
//		if (d < arg0.d)
//			return (d-arg0.d)*24*60;
//		if (h < arg0.h)
//			return (h-arg0.h)*60;
//		else
//			return (mm-arg0.mm);
	}
	
	public Date toDate()
	{
		final Calendar c = Calendar.getInstance();
		c.set(y, m - 1, d, h, mm);
		return c.getTime();
	}
	
	
	public static int[] days_in_months = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	private void normalize()
	{
		while (mm < 0) {
			mm += 60;
			h--;
		}
		while (mm >= 60) {
			mm -= 60;
			h++;
		}
		while (h < 0) {
			h+= 24;
			d--;
		}
		while (h >= 24) {
			h -= 24;
			d++;
		}
		while (d < 0) { // questa parte non mi convince
			d += days_in_months[m-1] + (isBisest(y)&&m-1==2?1:0);
			if (--m < 0) {
				m += 12;
				y--;
			}
		}
		while (d > days_in_months[m]) {
			d -= days_in_months[m] + (isBisest(y)&&m==2?1:0);
			if (++m > 12) {
				m -= 12;
				y++;
			}
		}
		while (m < 0) {
			m += 12;
			y--;
		}
		while (m > 12) {
			m -= 12;
			y++;
		}
	}
	
	public static boolean isBisest(int year) { return (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)); }
	
	
	public void add(TimeValue v)
	{
		mm += v.mm;
		h += v.h;
		d += v.d;
		m += v.m;
		y += v.y;
		normalize();
	}
	
	
	public static TimeValue[] createInterval(TimeValue from, TimeValue to)
	{
		ArrayList<TimeValue> val = new ArrayList<TimeValue>();
		
		final TimeValue deltaT = new TimeValue(0, 0, 0, 0, 1);
		
		for (TimeValue t=from; t.compareTo(to)<=0; t.add(deltaT))
			val.add(new TimeValue(t));
		
		return val.toArray(new TimeValue[val.size()]);
	}
	
	public TimeValue shift(int dt) {
		TimeValue tv = new TimeValue(y, m, d, h, mm+dt);
		tv.normalize();
		return tv;
	}
}

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

import org.joda.time.DateTime;


public class ElfValue extends MeasurementValue
{
	private int max;
	private boolean valid;

	public ElfValue(DateTime time, int value, int max, boolean valid)
	{
		super(time, value);
		this.max = max;
		this.valid = valid;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public ElfValue shift(int dt) {
		return new ElfValue(getTime().plusMinutes(dt), getValue(), max, valid);
	}
}

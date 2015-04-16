package y.elf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import y.elf.filterfunctions.FilterFunction;
import y.utils.Config;
import y.utils.Utils;

public class CurrentElfDb {

	private ElfDb elfDb;
	private CurrentDb currentDb;
	
	
	public CurrentElfDb() {
		this.elfDb = new ElfDb();
		this.currentDb = new CurrentDb();
	}
	
	public boolean loadElfValues(String[] filenames, int valuefieldn, int low, FilterFunction filter) {
		return elfDb.add(filenames, valuefieldn, low, filter);
	}
	
	public boolean loadCurrentValues(String[] filenames, int fieldn, Config config) {
		CurrentDb unfiltered = new CurrentDb();
		if (!unfiltered.add(filenames, fieldn, -1, config.getFilterCurrent()))
			return false;
		
		currentDb = unfiltered.filterLowCut(config);
		return true;
	}
	
	
	public static CurrentElfDb createDb(String[] elfFilenames, String[] currentFilenames, Config config) {
		
		CurrentElfDb db = new CurrentElfDb();
		db.loadElfValues(elfFilenames, config.getElfValuefieldn(), config.getInstrumentLowELF(), config.getFilterELF());
		db.loadCurrentValues(currentFilenames, config.getCurrentValuefieldn(), config);
		
//		if (db.getElfDb().isEmpty())
//			Utils.MessageBox("Db elf vuoto dopo lettura", "WARNING");
//		if (db.getCurrentDb().isEmpty())
//			Utils.MessageBox("Db correnti vuoto dopo lettura", "WARNING");
		
		return db;
	}
	
	public CurrentElfDb cut(DateTime from, DateTime to, int dt) {
		CurrentElfDb newdb = new CurrentElfDb();
		
		final List<ElfValue> elfs = getElfDb();
		final List<CurrentValue> currents = getCurrentDb();
		
		final List<ElfValue> newelfs = newdb.getElfDb();
		final List<CurrentValue> newcurrents = newdb.getCurrentDb();
		
		for (CurrentValue cv : currents) {
			final DateTime tv = cv.getTime();
			if (tv.compareTo(from) >= 0 && tv.compareTo(to) <= 0)
				newcurrents.add(cv);
		}
				
		for (ElfValue ev : elfs) {
			final DateTime tv = ev.getTime();
			if (tv.compareTo(from) >= 0 && tv.compareTo(to) <= 0)
				newelfs.add(dt == 0 ? ev : ev.shift(dt));
		}
		
		return newdb;
	}
	
	
	// delete from ElfValues and currentValues all entries with TimeValue not in both lists
	public int match() {
		
		Set<Instant> etv = new HashSet<Instant>();
		Set<Instant> ctv = new HashSet<Instant>();
		final List<ElfValue> elfs = getElfDb();
		final List<CurrentValue> currents = getCurrentDb();
		
		for (CurrentValue cv : currents)
			ctv.add(cv.getTime().toInstant());
		for (ElfValue ev : elfs)
//			if (ev.getValue() >= 0.10)	// low cut done in loadCurrentValues() // solo valori > 0.10, come secondo normativa punto 1
				etv.add(ev.getTime().toInstant());
		
		Iterator<CurrentValue> itcur = currents.iterator();
		while (itcur.hasNext()) {
			final CurrentValue curvalue = itcur.next();
			
			if (!etv.contains(curvalue.getTime().toInstant()))
				itcur.remove();
		}
		
		Iterator<ElfValue> itelf = elfs.iterator();
		while (itelf.hasNext()) {
			final ElfValue curvalue = itelf.next();
			
			if (!ctv.contains(curvalue.getTime().toInstant()))
				itelf.remove();
		}
		
		final int csz = currentDb.size();
		final int esz = elfDb.size();
		
//		if (csz == 0 && esz == 0)
//			Utils.MessageBox("Db di corrente e elf vuoti dopo match", "WARNING");
//		else if (csz == 0)
//			Utils.MessageBox("Db di corrente vuoto dopo match", "WARNING");
//		else if (esz == 0)
//			Utils.MessageBox("Db elf vuoto dopo match", "WARNING");
//		else if (csz != esz)
//			Utils.MessageBox("Db elf/correnti con lunghezze differenti: "+esz+" "+csz, "WARNING");
		
		return csz == esz ? csz : -Math.abs(csz - esz); // -delta if different, size otherwise
	}

	public DateTime getStartDate() { return currentDb.getStartDate(); }
	public DateTime getEndDate() { return currentDb.getEndDate(); }
	
	public boolean saveAs(String filename, String csvSeparator) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			
			bw.write("Date" + csvSeparator + "Time" + csvSeparator + "B" + csvSeparator + "I" + "\n");
			
			final List<ElfValue> elfs = getElfDb();
			final List<CurrentValue> currents = getCurrentDb();
			
			for (int i=0; i<elfDb.size(); i++) {
				final ElfValue ev = elfs.get(i);
				final CurrentValue cv = currents.get(i);
				
				bw.write(Utils.toDateString(ev.getTime()) + csvSeparator + Utils.toTimeString(ev.getTime()) + csvSeparator);
				bw.write(ElfValue.valueIntToString(ev.getValue()) + csvSeparator + cv.getValue() + "\n");
			}
			
			bw.close();
			
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public double correlation() {
		
		if (currentDb.size() != elfDb.size()) {
			Utils.MessageBox(Config.getResource("MsgDifferentLength"), Config.getResource("TitleErrorInternal"));
			return -999;
		}
		final List<ElfValue> elfs = getElfDb();
		final List<CurrentValue> currents = getCurrentDb();
		
		final double meancur = meanSerie(currents); // new AverageFunction().functionCurr(currentDb);
		final double meanelf = meanSerie(elfs); // new AverageFunction().function(elfDb);
		
		double num = 0;
		double dcur = 0;
		double delf = 0;
		
		for (int i=0; i<currentDb.size(); i++) {
			final double cc = currents.get(i).getValue();
			final double ce = elfs.get(i).getValue(); //ElfValue.valueIntToDouble( elfDb.get(i).getValue() );
			
			final double dc = cc-meancur;
			final double de = ce-meanelf;
			
			num +=  dc*de;
			dcur += dc*dc;
			delf += de*de;
		}
		
		try {
			return num/Math.sqrt(dcur*delf);
		}
		catch (Exception e) {
			return -999;
		}
	}
	
	
	public static double meanSerie(List<? extends MeasurementValue> ista) {
		double n = 0;
		for (MeasurementValue e : ista)
			n += e.getValue();
		
		return ista.size() <= 0 ? 0 : n/ista.size();
	}
	
	public double getRm() {
		double rm = 0;
		final List<ElfValue> elfs = getElfDb();
		final List<CurrentValue> currents = getCurrentDb();
		
		for (int i=0; i<currentDb.size(); i++) {
			final double cc = elfs.get(i).getValue();
			final double ce = currents.get(i).getValue();
			
			rm += ce/cc;
		}
		
		return currentDb.size() <=0 ? 0 : rm/currentDb.size();
	}

	public double getURm2() {
		
		final int size = currentDb.size();
		if (size <= 0)
			return 0;
		
		double Rm2 = 0;
		final List<ElfValue> elfs = getElfDb();
		final List<CurrentValue> currents = getCurrentDb();

		for (int i=0; i<size; i++) {
			final double Ii = elfs.get(i).getValue();
			final double Bi = currents.get(i).getValue();
			final double Ri = Bi/Ii;
			
			Rm2 += Ri*Ri;
		}
		
		return Rm2/size/size;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ElfValue> getElfDb() {
		return (List<ElfValue>) elfDb.getRawData();
	}

	@SuppressWarnings("unchecked")
	public List<CurrentValue> getCurrentDb() {
		return (List<CurrentValue>) currentDb.getRawData();
	}
}

package y.elf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import y.utils.Config;
import y.utils.Utils;

public class CurrentElfDb {

	private List<ElfValue> elfDb;
	private List<CurrentValue> currentDb;
	
	
	public CurrentElfDb(List<ElfValue> elfDb, List<CurrentValue> currentDb) {
		this.elfDb = elfDb;
		this.currentDb = currentDb;
	}
	
	public CurrentElfDb() {
		this(new ArrayList<ElfValue>(), new ArrayList<CurrentValue>());
	}
	
	public void loadElfValues(String[] filenames, int fieldn) {
		elfDb.addAll(DbReader.readFiles(filenames, fieldn, Config.getInstance().getInstrumentLowELF()));
	}
	
	public void loadCurrentValues(String[] filenames, int fieldn, int threshold) {
		final List<CurrentValue> unfiltered = DbReader.readCurrentFiles(filenames, fieldn, -1);
		for (CurrentValue c : unfiltered)
			if (c.getValue() >= threshold)
				currentDb.add(c);
	}
	
	
	public static CurrentElfDb createDb(String[] elfFilenames, int elfFieldn, String[] currentFilenames, int currentFieldn, int currentThreshold) {
		
		CurrentElfDb db = new CurrentElfDb();
		db.loadElfValues(elfFilenames, elfFieldn);
		db.loadCurrentValues(currentFilenames, currentFieldn, currentThreshold);
		
		if (db.getElfDb().isEmpty())
			Utils.MessageBox("Db elf vuoto dopo lettura", "WARNING");
		if (db.getCurrentDb().isEmpty())
			Utils.MessageBox("Db correnti vuoto dopo lettura", "WARNING");
		
		return db;
	}
	
	public CurrentElfDb cut(DateTime from, DateTime to, int dt) {
		CurrentElfDb db = new CurrentElfDb();
		
		for (CurrentValue cv : currentDb) {
			final DateTime tv = cv.getTime();
			if (tv.compareTo(from) >= 0 && tv.compareTo(to) <= 0)
				db.currentDb.add(cv);
		}
				
		for (ElfValue ev : elfDb) {
			final DateTime tv = ev.getTime();
			if (tv.compareTo(from) >= 0 && tv.compareTo(to) <= 0)
				db.elfDb.add(dt == 0 ? ev : ev.shift(dt));
		}
		
		return db;
	}
	
	
	// delete from ElfValues and currentValues all entries with TimeValue not in both lists
	public int match() {
		
		Set<DateTime> etv = new HashSet<DateTime>();
		Set<DateTime> ctv = new HashSet<DateTime>();
		
		for (CurrentValue cv : currentDb)
			ctv.add(cv.getTime());
		for (ElfValue ev : elfDb)
			if (ev.getValue() >= 0.10)	// solo valori > 0.10, come secondo normativa punto 1
				etv.add(ev.getTime());
		
		Iterator<CurrentValue> itcur = currentDb.iterator();
		while (itcur.hasNext()) {
			final CurrentValue curvalue = itcur.next();
			
			if (!etv.contains(curvalue.getTime()))
				itcur.remove();
		}
		
		Iterator<ElfValue> itelf = elfDb.iterator();
		while (itelf.hasNext()) {
			final ElfValue curvalue = itelf.next();
			
			if (!ctv.contains(curvalue.getTime()))
				itelf.remove();
		}
		
		final int csz = currentDb.size();
		final int esz = elfDb.size();
		
		if (csz == 0 && esz == 0)
			Utils.MessageBox("Db di corrente e elf vuoti dopo match", "WARNING");
		else if (csz == 0)
			Utils.MessageBox("Db di corrente vuoto dopo match", "WARNING");
		else if (esz == 0)
			Utils.MessageBox("Db elf vuoto dopo match", "WARNING");
		else if (csz != esz)
			Utils.MessageBox("Db elf/correnti con lunghezze differenti: "+esz+" "+csz, "WARNING");
		
		return csz == esz ? csz : -1; // -1 if different, size otherwise
	}

	public DateTime getStartDate() { return currentDb.size() <= 0 ? new DateTime(2000, 1, 1, 0, 0) : currentDb.get(0).getTime(); }
	public DateTime getEndDate() { return currentDb.size() <= 0 ? new DateTime(2999, 12, 12, 23, 59) : currentDb.get(currentDb.size()-1).getTime(); }
	
	public boolean saveAs(String filename, String csvSeparator) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			
			bw.write("Date" + csvSeparator + "Time" + csvSeparator + "B" + csvSeparator + "I" + "\n");
			
			for (int i=0; i<elfDb.size(); i++) {
				final ElfValue ev = elfDb.get(i);
				final CurrentValue cv = currentDb.get(i);
				
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
		
		final double meancur = meanSerie(currentDb); // new AverageFunction().functionCurr(currentDb);
		final double meanelf = meanSerie(elfDb); // new AverageFunction().function(elfDb);
		
		double num = 0;
		double dcur = 0;
		double delf = 0;
		
		for (int i=0; i<currentDb.size(); i++) {
			final double cc = currentDb.get(i).getValue();
			final double ce = elfDb.get(i).getValue(); //ElfValue.valueIntToDouble( elfDb.get(i).getValue() );
			
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
		
		for (int i=0; i<currentDb.size(); i++) {
			final double cc = currentDb.get(i).getValue();
			final double ce = elfDb.get(i).getValue();
			
			rm += ce/cc;
		}
		
		return currentDb.size() <=0 ? 0 : rm/currentDb.size();
	}

	public double getURm2() {
		
		final int size = currentDb.size();
		if (size <= 0)
			return 0;
		
		double Rm2 = 0;

		for (int i=0; i<size; i++) {
			final double Ii = currentDb.get(i).getValue();
			final double Bi = elfDb.get(i).getValue();
			final double Ri = Bi/Ii;
			
			Rm2 += Ri*Ri;
		}
		
		return Rm2/size/size;
	}
	
	
	public List<ElfValue> getElfDb() {
		return elfDb;
	}

	public List<CurrentValue> getCurrentDb() {
		return currentDb;
	}
}

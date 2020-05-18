package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
	List<Report> reports;
	AtomicInteger total;

	public void clear() {
		reports.clear();
		total.set(0);
	}

	/**
	 * Retrieves the single instance of this class.
	 */


	private static class SingletonHolder {
		private static Diary instance = new Diary();
	}
	private Diary(){
		reports = new LinkedList<>();
		total = new AtomicInteger();
		total.set(0);
	}

	public static Diary getInstance() {
		return SingletonHolder.instance;
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		//TODO: Implement this
		reports.add(reportToAdd);
		System.out.println(" REPORT ADD");
		System.out.println(reportToAdd.gadgetName + "," + reportToAdd.getMissionName() + " ," + reportToAdd.getM());
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){

			try {
				Writer writer = new FileWriter(filename);
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				gson.toJson(this, writer);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}


	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return  total.intValue();
	}

	/**
	 * Increments the total number of received missions by 1
	 */
	public void incrementTotal(){
		total.incrementAndGet();
	}
}

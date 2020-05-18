package bgu.spl.mics.application.passiveObjects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;

	/**
	 * Retrieves the single instance of this class.
	 */
	private static class SingletonHolder {
		private static Squad instance = new Squad();
	}

	public static Squad getInstance() {

		return Squad.SingletonHolder.instance;

	}
	private Squad()
	{
		agents = new HashMap<>();
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		// TODO Implement
		for (Agent a: agents) {
			this.agents.put(a.getSerialNumber(),a);
		}
	}

	/**
	 * Releases agents.
	 */
	public synchronized void releaseAgents(List<String> serials){

		for (String serial: serials ) {
			agents.get(serial).release();
		}
		notifyAll();
		System.out.println("agents Realesed");
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		for (String serial: serials) {
			agents.get(serial).acquire();
		}
		System.out.println("agent sent");
		try {
			Thread.sleep(time * 100);
		}
		catch (InterruptedException e)
		{

		}
		releaseAgents(serials);
		System.out.println(" agents done mission and released");
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		List<String> sortedSerials = sortSerials(serials);

		// TO CHANGE SORT TO A DEFAULT SORT ************************************

		boolean acquired;
		for (String serialNum: serials ) {
			if (!agents.containsKey(serialNum))
				return false;
		}
		for (String serialNum: sortedSerials) {
			Agent a = agents.get(serialNum);
			acquired = false;

			while (!acquired)
			{
				if (a.isAvailable())
				{
					synchronized (a) {
						if (a.isAvailable()) {
							a.acquire();
							acquired = true;
						}
					}
				}
				else {
					try{
						wait();
					}
					catch (Exception e)
					{

					}
				}
			}

		}
		/// ----- RESOURCE ORDERING --

		return true;

	}

    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){

		List<String> names = new LinkedList<>();
		for (String serial: serials ) {
			names.add(agents.get(serial).getName());
		}
		return names;
    }

    private List<String> sortSerials(List<String> serials)
	{
		List<String> sorted = new LinkedList<>();
		for (int k = 0; k < serials.size(); k++) {
			sorted.add(serials.get(k));
		}

		int i, j;
		for (i = 0; i < sorted.size(); i++) {
			// Last i elements are already in place
			for (j = 0; j < sorted.size() - i - 1; j++) {
				if (serialCompare(sorted.get(j) , sorted.get(j+1)) == 1) {
					String tmp = sorted.get(j);
					sorted.set(j,sorted.get(j+1));
					sorted.set(j+1, tmp);
				}
			}
		}
		return  sorted;
	}
	private int serialCompare(String serial1, String serial2)
	{
		if (serial1.length() > serial2.length())
			return 1;
		if (serial1.length() < serial2.length())
			return 0;
		else
		{
			for (int i = 0; i < serial1.length(); i++)
			{
				if (serial1.charAt(i) > serial2.charAt(i))
					return 1;
				if (serial1.charAt(i) < serial2.charAt(i))
					return 0;
			}
			return 0;
		}
	}
}

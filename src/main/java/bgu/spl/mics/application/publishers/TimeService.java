package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private int duration;
	private int currentTimeTick;
	private int tickTimeInMilliSec;
	public TimeService(int duration) {
		super("TimeService");
		// TODO Implement this
		this.duration = duration;
		currentTimeTick = 0;
		tickTimeInMilliSec = 100;
	}

	@Override
	protected void initialize() {
		// TODO Implement this
		System.out.println("Sender " + getName() + " started");

		
	}

	@Override
	public void run() {  // TO CHANGE THE TIMER TO A JAVA TIMER INSTEAD OF SLEEP
		initialize();
		try {
			Thread.sleep(2000);
			System.out.println("TIME SERVICE WAKE UP");
		}
		catch (Exception e)
		{

		}
		while (currentTimeTick < duration)
		{
			try {
				Thread.sleep(100);

			}
			catch (InterruptedException e)
			{

			}
			getSimplePublisher().sendBroadcast(new TickBroadcast());
			currentTimeTick++;
		}
		getSimplePublisher().sendBroadcast(new TerminateBroadcast());
	}

}

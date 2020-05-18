package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.AgentAvailableEvent;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.Report;
import bgu.spl.mics.application.passiveObjects.Squad;


import java.util.HashMap;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private int timeTicks;
	public Q() {
		super("Q");
		// TODO Implement this
	}

	@Override
	protected void initialize() {
		// TODO Implement this


		System.out.println("Event Handler " + getName() + " started");

		subscribeBroadcast(TickBroadcast.class, ev ->{
			timeTicks++;
		});
		subscribeBroadcast(TerminateBroadcast.class, ev ->{
			terminate();
		});
		subscribeEvent(GadgetAvailableEvent.class, ev -> {
			Report report = new Report();
			report.setQTime(timeTicks);
			Boolean answer = Inventory.getInstance().getItem(ev.getInfo().getGadget());


			complete(ev, new Object[] {answer,report});
		});
	}

}

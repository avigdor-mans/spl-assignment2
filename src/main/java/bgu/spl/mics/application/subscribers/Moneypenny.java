package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	private static AtomicInteger counterOfCheckers = new AtomicInteger(0);

	private int serialNumber;
	public Moneypenny(String s, int serialNumber) {
		super(s);
		this.serialNumber = serialNumber;
	}

	@Override
	protected void initialize() {



		System.out.println("Event Handler " + getName() + " started");
		if (serialNumber%2 == 0) {
			counterOfCheckers.incrementAndGet();
			subscribeBroadcast(TerminateBroadcast.class, ev ->{
				int num = counterOfCheckers.decrementAndGet();
				if (num == 0) {
					getSimplePublisher().sendBroadcast(new TerminateForSendersBroadcast());
				}
				terminate();

			});

			subscribeEvent(AgentAvailableEvent.class, ev -> {
				System.out.println(getName() + " start to handle AgentAvailableEvent");
				Squad squad = Squad.getInstance();
				Boolean answer = squad.getAgents(ev.getMissionInfo().getSerialAgentsNumbers());
				Report report = new Report();
				if (answer) {
					report.setAgentsNames(Squad.getInstance().getAgentsNames(ev.getMissionInfo().getSerialAgentsNumbers()));
					report.setMoneypenny(serialNumber);
				}

				complete(ev, new Object[] {answer,report});
			});
		}
		else {
			subscribeBroadcast(TerminateForSendersBroadcast.class, ev ->{
				terminate();

			});

			subscribeEvent(SendAgentsEvent.class, ev -> {
				if (ev.getToSend()) {
					Squad.getInstance().sendAgents(ev.getMissionInfo().getSerialAgentsNumbers()
							, ev.getMissionInfo().getDuration());
					complete(ev,true);
				}
				else {
					Squad.getInstance().releaseAgents(ev.getMissionInfo().getSerialAgentsNumbers());
					complete(ev,false);
				}

			});
		}
	}

}

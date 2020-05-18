package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;


import java.util.HashMap;


/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

	private int serialNumber;
	private int timeTicks;

	public M(String name, int serialNumber) {
		super(name);
		this.serialNumber = serialNumber;
		timeTicks = 0;
	}

	@Override
	protected void initialize() {
		// TODO Implement this

		System.out.println("Event Handler " + getName() + " started");
		subscribeBroadcast(TickBroadcast.class,ev ->{
			timeTicks++;
			System.out.println(getName() + "GOT TIME TICK" + timeTicks);
		});
		subscribeBroadcast(TerminateBroadcast.class, ev ->{
			terminate();
		});

		subscribeEvent(MissionReceivedEvent.class, ev -> {

			Diary.getInstance().incrementTotal();

			Future<Object[]> future = getSimplePublisher().sendEvent(new AgentAvailableEvent(ev.getMissionInfo()));
			System.out.println("AgentAvailableEvent Sent from " + getName());
			Object[] answerForAgents = null;
			if (future != null) {
				answerForAgents = future.get();
			}

			Object[] answerForGadgets = null;
			if (answerForAgents != null && (Boolean)answerForAgents[0]) {
				Future<Object[]> future2 = getSimplePublisher().sendEvent(new GadgetAvailableEvent(ev.getMissionInfo()));
				System.out.println("GadgetAvailableEvent Sent from " + getName());
				if (future2 != null) {
					answerForGadgets = future2.get();
				}
				if (future2 == null)
				{
					System.out.println("future 2 is null ***");
				}

			}

			if (!(answerForAgents == null || answerForGadgets == null)) {
				if ((Boolean)answerForAgents[0] && (Boolean)answerForGadgets[0]) {
					System.out.println(ev.getMissionInfo().getMissionName() + " TRUE FOR GADGETS AND AGENTS");

					if (ev.getMissionInfo().getTimeExpired() > timeTicks) {
						Future<Boolean> futureForSending = getSimplePublisher().sendEvent(new SendAgentsEvent(ev.getMissionInfo(), true));
						if (futureForSending != null && futureForSending.get() != null) {
							Report report = new Report();
							report.setMissionName(ev.getMissionInfo().getMissionName());
							report.setTimeIssued(ev.getMissionInfo().getTimeIssued());
							report.setTimeCreated(timeTicks);
							report.setM(serialNumber);
							report.setAgentsSerialNumbers(ev.getMissionInfo().getSerialAgentsNumbers());
							report.setMoneypenny(((Report) answerForAgents[1]).getMoneypenny());
							report.setAgentsNames(((Report) answerForAgents[1]).getAgentsNames());
							report.setGadgetName(ev.getMissionInfo().getGadget());
							report.setQTime(((Report) answerForGadgets[1]).getQTime());

							Diary.getInstance().addReport(report);
						}
					} else {
						getSimplePublisher().sendEvent(new SendAgentsEvent(ev.getMissionInfo(), false));

					}
				}
				else
				{
					if ((Boolean)answerForAgents[0]) {
					getSimplePublisher().sendEvent(new SendAgentsEvent(ev.getMissionInfo(), false));
					System.out.println(" abort mission for false");
					}
				}
			}
			else
			{
				System.out.println("entered to the else 1 ---");
				if (answerForAgents != null && (Boolean)answerForAgents[0]){
					System.out.println("entered to the if 2 ---");
					Future<Boolean> future3 = getSimplePublisher().sendEvent(new SendAgentsEvent(ev.getMissionInfo(), false));
					System.out.println("sent SendAGENT-EVENT");
				}
			}
			System.out.println(" I EXIT FROM MISSION " + getName());
		});

	}

}

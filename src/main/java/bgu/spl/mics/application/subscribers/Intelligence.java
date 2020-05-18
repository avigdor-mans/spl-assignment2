package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {



	private int timeTicks;
	List<MissionInfo> missionInfos;

	public Intelligence(String s, List<MissionInfo> missionInfos) {
		super(s);
		this.missionInfos = missionInfos;
		timeTicks = 0;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, ev ->{
			timeTicks++;
			for (MissionInfo info: missionInfos ) {
				if (info.getTimeIssued() == timeTicks) {
					getSimplePublisher().sendEvent(new MissionReceivedEvent(info));
					System.out.println("Mission sent :" + info.getMissionName());
				}
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, ev ->{
			terminate();
		});
	}
}
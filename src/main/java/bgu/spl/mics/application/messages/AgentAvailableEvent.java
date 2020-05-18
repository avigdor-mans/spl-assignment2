package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;





public class AgentAvailableEvent implements Event<Object[]> {
    private MissionInfo info;


    public AgentAvailableEvent(MissionInfo info)
    {
        this.info = info;

    }
    public MissionInfo getMissionInfo ()
    {
        return info;
    }




}

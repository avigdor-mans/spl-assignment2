package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class SendAgentsEvent implements Event<Boolean> {
    private MissionInfo info;
    private Boolean toSend;

    public SendAgentsEvent(MissionInfo info,Boolean toSend)
    {
        this.info = info;
        this.toSend = toSend;
    }
    public MissionInfo getMissionInfo ()
    {
        return info;
    }

    public Boolean getToSend ()
    {
        return toSend;
    }

}

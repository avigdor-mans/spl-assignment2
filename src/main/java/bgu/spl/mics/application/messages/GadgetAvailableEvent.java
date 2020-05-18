package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;





public class GadgetAvailableEvent implements Event<Object []> {

    private MissionInfo info;

    public GadgetAvailableEvent(MissionInfo info)
    {
        this.info = info;

    }
    public MissionInfo getInfo()
    {
        return  info;
    }
}

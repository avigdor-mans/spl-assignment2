package bgu.spl.mics.application;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
        // TODO Implement this

            Gson gson = new Gson();
            List<Thread> threads = new LinkedList<>();
            try {
                JsonObject parser = new JsonParser().parse(new FileReader(args[0])).getAsJsonObject();
                //inventory
                String[] gadegets = gson.fromJson(parser.getAsJsonArray("inventory"), String[].class);
                Inventory inven = Inventory.getInstance();
                inven.load(gadegets);

                //squad
                Agent[] agents = gson.fromJson(parser.getAsJsonArray("squad"), Agent[].class);
                Squad.getInstance().load(agents);

                //services
                JsonObject services = parser.getAsJsonObject("services");


                //Q
                Q q = new Q();
                threads.add(new Thread(q));
                //M
                for (int i = 0; i < services.get("M").getAsInt(); i++) {
                    M m = new M("M" + i, i);
                    threads.add(new Thread(m));
                }
                //Moneypenny
                for (int i = 0; i < services.get("Moneypenny").getAsInt(); i++) {
                    Moneypenny moneypenny = new Moneypenny("Moneypenny" + i, i);
                    threads.add(new Thread(moneypenny));
                }
                //Intelligence
                int intelCounter = 0;
                for (JsonElement intelligences : services.getAsJsonArray("intelligence")) {
                    JsonObject temp = intelligences.getAsJsonObject();
                    MissionInfo[] missionInfos = gson.fromJson(temp.getAsJsonArray("missions"), MissionInfo[].class);
                    List<MissionInfo> listMissionInfos = new LinkedList<>();
                    for (int i = 0; i < missionInfos.length; i++) {
                        listMissionInfos.add(missionInfos[i]);
                    }
                    Intelligence intelligence = new Intelligence("Intelligence" + intelCounter, listMissionInfos);
                    intelCounter++;
                    threads.add(new Thread(intelligence));

                }

                //timeService
                TimeService timeService = new TimeService(services.get("time").getAsInt());
                threads.add(new Thread(timeService));

            } catch (Exception e) {
                System.out.println(e);

            }

            for (Thread temp11 : threads) {
                temp11.start();
            }
            for (Thread temp11 : threads) {
                try {
                    temp11.join();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            boolean isAllDEad = false;

            while (!isAllDEad) {
                int counter = 0;
                for (Thread temp11 : threads) {
                    if (temp11.isAlive())
                        counter++;
                }
                isAllDEad = counter == 0;
            }
            System.out.println(isAllDEad);

            Diary.getInstance().printToFile("REPORTS");
            Inventory.getInstance().printToFile("GADGETS");
          //  MessageBrokerImpl.getInstance().getMesListBySub();

            System.out.println("finished");



    }
}
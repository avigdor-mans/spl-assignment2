package bgu.spl.mics;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
@SuppressWarnings("unchecked")
public class MessageBrokerImpl implements MessageBroker {

	private ConcurrentHashMap <Class<? extends Message>, ConcurrentLinkedQueue<Subscriber>> subListByType;
	private ConcurrentHashMap <Subscriber, BlockingQueue<Message>> mesListBySub;
	private ConcurrentHashMap <Event<?>,Future> messageFuture;
	private ConcurrentHashMap <Class<? extends Message>,Class<? extends Message>> listOfTypes;

	private static class SingletonHolder{
		private static MessageBrokerImpl instance= new MessageBrokerImpl();
	}

	private MessageBrokerImpl(){
		subListByType=new ConcurrentHashMap<>();
		mesListBySub=new ConcurrentHashMap<>();
		messageFuture = new ConcurrentHashMap<>();
		listOfTypes = new ConcurrentHashMap<>();

	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {

		subListByType.putIfAbsent(type,new ConcurrentLinkedQueue<>());
		subListByType.get(type).add(m);
		listOfTypes.putIfAbsent(type,type);
		System.out.println(m.getName() + " subscribed to event " + type.getName());

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {

		subListByType.putIfAbsent(type,new ConcurrentLinkedQueue<>());
		subListByType.get(type).add(m);
		listOfTypes.putIfAbsent(type,type);
		System.out.println(m.getName() + " subscribed to broadcast " + type.getName());

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
		messageFuture.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (!subListByType.get(b.getClass()).isEmpty()) {
			for (Subscriber subscriber : subListByType.get(b.getClass())) {
				mesListBySub.get(subscriber).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> willResolved = new Future<>();
		messageFuture.put(e,willResolved);
		Subscriber subscriber;
		if (!subListByType.get(e.getClass()).isEmpty()) { // if there isn't subscriber to that event type

			synchronized (listOfTypes.get(e.getClass()))
			{
				if (!subListByType.get(e.getClass()).isEmpty())
				{
				//	System.out.println("queue type isn't empty");
					subscriber = subListByType.get(e.getClass()).poll();

					subListByType.get(e.getClass()).add(subscriber);

					//	System.out.println("subscriber isnt null *********");
					//	System.out.println(e.getClass() + " - > the event to add");
				//		if(mesListBySub.get(subscriber) != null) {

							mesListBySub.get(subscriber).add(e);
						//	System.out.println(subscriber.getName() + " added message to his queue");

			  //			}
				//		else {
				//			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%" + subscriber.getName() + " has removed his queue");
				//			return null;
				//		}

				}
				else {
					return null;
				}
			}


			return  willResolved;
		}
		System.out.println("no one recieved the event " + e.getClass());
		return null;
	}

	@Override
	public void register(Subscriber m) {

		mesListBySub.putIfAbsent(m,new ArrayBlockingQueue<>(100));

		System.out.println("register of " + m.getName() + mesListBySub.get(m).toString());
	}

	@Override
	public void unregister(Subscriber m) {
		// TODO Auto-generated method stub

		boolean removedMessages = false;
		for (Class<? extends Message> c: listOfTypes.values())
		{
			if (subListByType.get(c).contains(m))
			{
				synchronized (listOfTypes.get(c)) {
					subListByType.get(c).remove(m);

				/*	if (!removedMessages)
					{
						for (Message msg: mesListBySub.get(m)) {
							messageFuture.get(msg).resolve(null);
						}
					//	System.out.println(m.getName() + "is about to UNREGISTER");

						mesListBySub.remove(m);
					//	System.out.println(m.getName() + " UNREGISTERED");
						removedMessages = true;
					} */
				//	System.out.println("ENTERED IN UN REGISTER");
				}
			}
		}
		for (Message msg: mesListBySub.get(m)) {
			messageFuture.get(msg).resolve(null);
		}
		mesListBySub.remove(m);
	//	System.out.println(m.getName() + " UNREGISTERED");

	}

	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		// TODO Auto-generated method stub

		if (!mesListBySub.containsKey(m)) {
			System.out.println("SUBSCRIBER NOT REGISTERED AND tried to take message");
			throw new IllegalStateException();
		}
		return mesListBySub.get(m).take();


	}

}

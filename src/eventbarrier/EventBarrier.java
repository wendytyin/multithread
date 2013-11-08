package eventbarrier;

public class EventBarrier extends AbstractEventBarrier{

	private int waiters;
	protected Boolean signalled;
	protected String fakeevent;
	
	public EventBarrier(){
		super();
		waiters=0;
		signalled=false;
	}
	
	@Override
	public synchronized void arrive() {
		waiters++;
		System.out.println(waiters);
		while (!signalled){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return;
	}
	
	/* for testing, producer adds an event to the eventbarrier, then calls raise
	 * Note this is not synchronized so event might change between calls of set and raise
	 * We are assuming a single producer for now
	 * TODO: change that assumption
	 */
	public void setEvent(String e){
		System.out.println("set: "+e);
		fakeevent=e;
	}
	public String getEvent(){
//		System.out.println("get");
		return fakeevent;
	}
	
	@Override
	public synchronized void raise() {
		assert(!signalled); //if signalled, event already in progress. Only one event at a time
		signalled=true;

//		System.out.println(waiters);
		notifyAll(); //wake up consumers to respond to event
		while (waiters>0){
			try {
				wait(); //put producer to sleep
			} catch (InterruptedException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		//notifyAll(); //wake everyone up

//		System.out.println(waiters);
		signalled=false;
		return;
	}

	@Override
	public synchronized void complete() {
		waiters--;

//		System.out.println(waiters);
		if (waiters<=0){
			notifyAll(); //wake up producer to finish raise()
		}
		try {
			wait(); //put finished consumers back to sleep while event in progress
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int waiters() {
		return waiters; //read/writes are atomic
	}

}

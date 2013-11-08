import eventbarrier.AbstractEventBarrier;


/*
 * Only one producer should have access to this barrier. 
 * Producer thread calls setEvent, then raise. If there are no waiting rider threads, producer blocks. Otherwise it 
 * notifies a rider thread then waits for rider thread to call complete, then raise returns.
 * 
 * Rider threads arrive at barrier and wait for signal. 
 * When woken up, thread will getEvent, change its properties accordingly, then call complete.
 * 
 * When producer is done feeding in input, it calls quit before terminating itself. 
 * Quit sets up a special rider case [-1,-1,-1], wakes up all rider threads, prevents arrive() threads from waiting.
 */
public class IOBarrier extends AbstractEventBarrier{

	private int waiters;
	protected Boolean signalled;
	private volatile int[] riderProp; // [riderID, starting floor, end floor]
	private Object producerLock=new Object();
	private Object riderLock = new Object();

	public IOBarrier(){
		super();
		waiters=0;
		signalled=false;
		riderProp=new int[3];
	}

	@Override
	public void arrive() {
		synchronized(riderLock){
			waiters++;
			while (!signalled){
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return;
	}

	/* Producer adds event (rider properties read from input file) to eventBarrier, then calls raise. 
	 */
	public void setEvent(int[] e){
		synchronized(producerLock){
			riderProp[0]=e[0];
			riderProp[1]=e[1];
			riderProp[2]=e[2];
		}
	}
	public int[] getEvent(){
		return riderProp;
	}
	public void quit(){
		for (int i=0;i<3;i++){
			riderProp[i]=-1; //reserve this set of properties [-1,-1,-1] as the quit signal for rider threads
		}
		synchronized(riderLock){
			notifyAll();
		}
		signalled=true; //all threads that missed the notifyAll will call arrive() which returns immediately, then getEvent
	}

	@Override
	public void raise() {
		assert(!signalled); //if it is signalled, something has gone wrong because there is only one producer thread
		while (waiters<1){
			waitProd();
		}
		synchronized(riderLock){
			signalled=true;
			notify(); // get one rider thread
		}
		waitProd();

		signalled=false;
		return;
	}

	private void waitProd() {
		synchronized(producerLock){ //put producer to sleep in its own special lock
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void complete() {
		synchronized(producerLock){ //wake the producer back up
			waiters--;
			notifyAll();
		}
	}

	@Override
	public int waiters() {
		return waiters; //read/writes are atomic
	}

}

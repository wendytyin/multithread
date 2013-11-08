package eventbarrier;

/*
 * For testing the EventBarrier
 */
public class Consumer implements Runnable{
	private EventBarrier myBarrier;
	
	public Consumer(EventBarrier eb){
		myBarrier=eb;
	}

	@Override
	public void run() {
		printString("arrive");
		myBarrier.arrive();
		String s=myBarrier.getEvent();
		printString(s);
		myBarrier.complete();
		printString("I'm finished.");
	}
	protected void printString(String s){
		System.out.println("consumer: "+s);
	}

}

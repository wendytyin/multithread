package eventbarrier;

/*
 * For testing EventBarrier
 */
public class Producer implements Runnable{
	private EventBarrier myBarrier;
	
	public Producer (EventBarrier eb){
		myBarrier=eb;
	}


	@Override
	public void run() {
		String s="testing eventbarrier";
		myBarrier.setEvent(s);
		myBarrier.raise();
		printString("producer out");
		
	}
	protected void printString(String s){
		System.out.println(s);
	}

}

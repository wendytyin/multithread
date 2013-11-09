package elevator;
import eventbarrier.AbstractEventBarrier;

/*
 * One per elevator and floor and direction
 */
public class DoorEventBarrier extends AbstractEventBarrier{
	private int riders;
	protected boolean doorOpen;
	protected boolean upDir;
	
	
	public DoorEventBarrier(){
		riders=0;
		upDir=true;
		doorOpen=false;
	}
	
	/**
	 * Please do not directly call this, use the method with arguments
	 */
	@Override
	public synchronized void arrive() {
		riders++;
		while(!doorOpen){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized void raise() {
		assert(doorOpen==false);
		doorOpen=true;
		notifyAll();
		while (riders>0){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		doorOpen=false;
	}

	@Override
	public synchronized void complete() {
		riders--;
		if (riders==0){
			notifyAll();
		}
		else {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void setDir(boolean up){
		upDir=up;
	}
	/**
	 * @return true for up, false for down
	 */
	public boolean getDir(){
		return upDir;
	}
	
	@Override
	public int waiters() {
		return riders;
	}

}

import java.util.concurrent.Semaphore;
import java.util.Random;

public class SampleThread extends Thread
{
	private int id;
	private Semaphore[] barriers;
	
	public SampleThread(int i, Semaphore[] bar)
	{
		id = i;
		barriers = bar;
	}
	
	public void do_something()
	{
		int waitTime = new Random().nextInt(5000); // Duration of its job.
		
		System.out.println("Thread " + id + " is doing something in " + waitTime + " milliseconds.");
		
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();  // Write something.
		}		
	}
	
	public void wait_other_threads()  // Barrier implementation.
	{
		for (int j = 0; j < 2; j++)
		{
			barriers[id].release();  // Up the semaphore (n-1 times).
		}
		
		for (int j = 0; j < 3; j++)  //
		{
			if (id != j)  // If id is equal to j then it means it is its own semaphore.
			{
				try {
					barriers[j].acquire();  // Down all semaphores except your own.
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void run()
	{
		System.out.println("Thread " + id + " has started working.");
		do_something();
		System.out.println("Thread " + id + " is waiting other threads.");
		wait_other_threads();
		System.out.println("Thread " + id + " is finished waiting other threads.");
	}
}
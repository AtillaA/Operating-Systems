
public class MainApplication
{
	public static volatile int counter = 0;
	// public int counter = 0 

	public MainApplication()
	{
		// TODO Auto-generated constructor stub
	}
	
	public int incrementValue()
	{
		counter = counter + 1;
		return counter;
	}
	
	public static void main(String[] args)
	{
		System.out.println("Main Thread: " + counter + ".");
		SampleThread thread = new SampleThread();
		thread.start();		
		Thread anotherThread = new Thread(new SampleRunnable());
		anotherThread.start();
	}
}

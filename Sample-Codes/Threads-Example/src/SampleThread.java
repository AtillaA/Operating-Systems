
public class SampleThread extends Thread
{	
	public void run()
	{
		MainApplication m1 = new MainApplication();
		int result = m1.incrementValue();
		System.out.println("Sample Thread is changing the value.");
		System.out.println("New value is " + result + ".");
	}
}


public class SampleRunnable implements Runnable
{
	@Override
	public void run()
	{
		MainApplication m1 = new MainApplication();
		int result = m1.incrementValue();
		System.out.println("Sample Runnable is changing the value.");
		System.out.println("new value is " + result + ".");
	}
}

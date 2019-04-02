import java.util.concurrent.Semaphore;
import java.util.Random;

public class MainApplication
{
	public MainApplication()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		Semaphore[] barray = new Semaphore[3];
		SampleThread[] threads = new SampleThread[3];
		
		for (int i = 0; i < 3; i++)
		{
			barray[i] = new Semaphore(0);
		}
		
		for (int i = 0; i < 3; i++)
		{
			threads[i] = new SampleThread(i, barray);
			threads[i].start();
		}
	}
}
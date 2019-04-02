
import java.util.Random;

public class Producer extends Thread
{
	private Storage storage;
	private Random random;
	
	Producer(Storage storage)
	{
		this.storage = storage;
		this.random = new Random();
	}
	
	private String produce_item()
	{
		String line = null;
		try {
			sleep(random.nextInt(3000) + 1000);
			line = "item";
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	public void run()
	{
		while (true)
		{
			String item = produce_item();
			storage.insertItem(item);
			
		}
	}
}

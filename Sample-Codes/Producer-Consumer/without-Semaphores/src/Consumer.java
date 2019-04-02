
import java.util.Random;

public class Consumer extends Thread
{
	private Storage storage;
	private Random random;
	
	Consumer(Storage storage)
	{
		this.storage = storage;
		this.random = new Random();
	}
	
	private void consume_item (String item)
	{
		try {
			sleep(random.nextInt(3000) + 1000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while (true)
		{
			String item = storage.removeItem();
			consume_item(item);
		}
	}
}
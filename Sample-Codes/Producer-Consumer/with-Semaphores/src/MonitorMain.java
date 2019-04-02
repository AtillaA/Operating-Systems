package semaphore;
import java.util.concurrent.Semaphore;

public class MonitorMain 
{
	public static void main(String[] args)
	{
		Storage storage = new Storage();
		
		Consumer consumer = new Consumer(storage);
		Producer producer = new Producer(storage);
		
		consumer.start();
		producer.start();
	}
}

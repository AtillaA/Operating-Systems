package semaphore;
import java.util.concurrent.Semaphore;

public class Storage 
{
	private int MAX_STORAGE = 10;
	private int itemSize = 0;
	private String[] boundedBuffer;
	private int insertionIndex;
	private int removalIndex;
	private Semaphore mutex = new Semaphore(1);
	private Semaphore full = new Semaphore(0);
	Semaphore empty = new Semaphore (MAX_STORAGE);
	
	
	public Storage()
	{
		MAX_STORAGE = 10;
		itemSize = 0;
		insertionIndex = 0;
		removalIndex = 0;
		boundedBuffer = new String[MAX_STORAGE];
	}
	
	public void insertItem(String item)
	{
		try {
			
			empty.acquire();
			mutex.acquire();
			
			boundedBuffer[insertionIndex] = item;
			insertionIndex = (insertionIndex + 1) % MAX_STORAGE;
			itemSize++;
			
			System.out.println("An item is added to the storage. Storage size: " + itemSize);
			
			mutex.release();
			full.release();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			mutex.release();
			empty.release();
		}
	}
	
	public String removeItem()
	{
		String item = null;
		
		try {
			
			full.acquire();
			mutex.acquire();
			
			item = boundedBuffer[removalIndex];
			removalIndex = (removalIndex + 1) % MAX_STORAGE;
			itemSize--;
			
			System.out.println("An item has been removed from the storage. Storage size: " + itemSize);
			
			mutex.release();
			empty.release();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			mutex.release();
			full.release();
		}
		
		return item;
	}
}

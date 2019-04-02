
public class Storage 
{
	private int MAX_STORAGE = 10;
	private int itemSize = 0;
	private String[] boundedBuffer;
	private int insertionIndex;
	private int removalIndex;
	
	public Storage()
	{
		MAX_STORAGE = 10;
		itemSize = 0;
		insertionIndex = 0;
		removalIndex = 0;
		boundedBuffer = new String[MAX_STORAGE];
	}
	
	public synchronized void insertItem(String item)
	{
		if(MAX_STORAGE == itemSize)
			goToSleep();
		
		boundedBuffer[insertionIndex] = item;
		insertionIndex = (insertionIndex + 1) % MAX_STORAGE;
		itemSize++;
		
		System.out.println("An item is added to the storage. Storage size: " + itemSize);
		
		if(itemSize == 1)
			notify();
	}
	
	public synchronized String removeItem()
	{
		if(itemSize == 0)
			goToSleep();
		
		String item = null;
		item = boundedBuffer[removalIndex];
		removalIndex = (removalIndex + 1) % MAX_STORAGE;
		itemSize--;
		
		System.out.println("An item has been removed from the storage. Storage size: " + itemSize);
		
		if(itemSize == MAX_STORAGE - 1)
			notify();
		
		return item;
	}
	
	private void goToSleep()
	{
		try {
			wait();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}

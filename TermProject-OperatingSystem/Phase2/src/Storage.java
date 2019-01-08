import java.lang.reflect.Array;


public class Storage<Type> {

	private int MAX_STORAGE = 10;
	private int itemSize = 0;
	private Type [] boundedBuffer;
	private int insertionIndex;
	private int removalIndex;
   
	@SuppressWarnings("unchecked")
	public Storage (){
		MAX_STORAGE = 10;
		itemSize = 0;
		insertionIndex = 0;
		removalIndex = 0;
		
		boundedBuffer =(Type[])new Object[MAX_STORAGE];
	}

	public synchronized Type removeItem() {  // Both methods lock the same monitor. Therefore, we can't simultaneously execute them on the same object from different threads (one of the two methods will block until the other is finished).
		Type tweet = null;
	 if (itemSize == 0)
			goToSleep();

		tweet = boundedBuffer[removalIndex];
		removalIndex = (removalIndex + 1) % MAX_STORAGE;
		itemSize--;
		System.out.println("An item is removed from storage. Storage size: " + itemSize);
		
		if(itemSize == MAX_STORAGE - 1)
			notify();	
		
		return tweet;
	}

	public synchronized void insertItem(Type item) { //If you have two different instance methods marked synchronized and different threads are calling those methods concurrently, those threads will be racing  for the same lock. Once one thread gets the lock all other threads are shut out of all synchronized instance methods on that object.
		
		if(itemSize == MAX_STORAGE)
			goToSleep();
		
		boundedBuffer[insertionIndex] = item;
		insertionIndex = (insertionIndex + 1) % MAX_STORAGE;
		itemSize++;
		System.out.println("An item is added to storage. Storage size: " + itemSize);
		
		if(itemSize == 1)
			notify(); 
	}

	private void goToSleep() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}

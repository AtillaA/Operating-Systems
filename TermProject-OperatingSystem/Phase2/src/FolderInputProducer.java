import java.io.File;
import java.util.concurrent.Semaphore;
import java.io.FilenameFilter;

public class FolderInputProducer extends Thread {

	private volatile Storage<String> folderInputQueue;
	
	private Semaphore mutex;
	
	private volatile boolean isRunning;

	public FolderInputProducer(Semaphore mtx, Storage<String> folderInputQ) {
		this.mutex = mtx;
		this.folderInputQueue = folderInputQ;
	}

	@Override
	public void run(){
		isRunning = true;
		
		try {
			File[] files = new File(".").listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			});
			
			for (File file : files) {
				folderInputQueue.insertItem(file.getName());
				Thread.sleep(1000);
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void stopThread() {
		isRunning = false;
	}
}

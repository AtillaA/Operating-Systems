import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileReader;
import java.io.FilenameFilter;

public class FolderInputConsumer extends Thread {

	private volatile Storage<String> folderInputQueue;
	
	private Semaphore mutex;
	
	private FileInputProducer fileInputProducer;
	
	private volatile boolean isRunning;

	public FolderInputConsumer(Semaphore mtx, Storage<String> folderInputQ, FileInputProducer fileInputProducer) {
		this.mutex = mtx;
		this.folderInputQueue = folderInputQ;
		this.fileInputProducer = fileInputProducer;
	}

	@Override
	public void run(){
		isRunning = true;
		
		try {
			while (isRunning) {
				String processListFile = folderInputQueue.removeItem();
				
				fileInputProducer.wakeForProcessFile(processListFile);
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void stopThread() {
		isRunning = false;
	}
}

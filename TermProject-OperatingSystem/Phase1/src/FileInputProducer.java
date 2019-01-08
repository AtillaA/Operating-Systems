import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileInputProducer extends Thread {

	private volatile Storage<ProcessImage> fileInputQueue;
	private volatile Assembler assembler;
	private volatile List<String> assemblyFileList;
	private volatile List<Integer> assemblyFileDelayList;

	private Semaphore mutex;
	
	private volatile boolean isRunning;

	public FileInputProducer(Semaphore mtx, Storage<ProcessImage> fileInputQ, String processListFile, Assembler assembler) {
		this.mutex = mtx;
		this.fileInputQueue = fileInputQ;
		this.assembler = assembler;
		this.assemblyFileList = new ArrayList<>();
		this.assemblyFileDelayList = new ArrayList<>();
		
		try {
			File file = new File(processListFile); 
			BufferedReader br = new BufferedReader(new FileReader(file)); 
			
			String line; 
			
			while (true) {
				line = br.readLine();
				
				if (line != null) {
					String asmName = line.split(" ")[0];
					String delayString = line.split(" ")[1];
					
					assemblyFileList.add(asmName);
					assemblyFileDelayList.add(Integer.parseInt(delayString));
				}
				
				else {
					break;
				}
			}
			
			br.close();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void run(){
		isRunning = true;
		try {
			while (isRunning) {
				if (!assemblyFileList.isEmpty()) {
					String assemblyFile = assemblyFileList.get(0);
					int delay = assemblyFileDelayList.get(0);
					assemblyFileList.remove(0);
					assemblyFileDelayList.remove(0);

					String binFile = assemblyFile.replaceFirst(".asm", ".bin");

					int instructionSize = assembler.createBinaryFile(assemblyFile, binFile);

					fileInputQueue.insertItem(new ProcessImage(binFile, 0, instructionSize));

					Thread.sleep(delay);
				}
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void stopThread() {
		isRunning = false;
	}
}

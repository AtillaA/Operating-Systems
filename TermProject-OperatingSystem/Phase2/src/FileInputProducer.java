import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public FileInputProducer(Semaphore mtx, Storage<ProcessImage> fileInputQ, Assembler assembler) {
		this.mutex = mtx;
		this.fileInputQueue = fileInputQ;
		this.assembler = assembler;
		this.assemblyFileList = new ArrayList<>();
		this.assemblyFileDelayList = new ArrayList<>();
	}
	
	public void wakeForProcessFile(String processListFile) {
		try {
			Path path = Paths.get(processListFile);

			// Byte buffer
			FileChannel fc = FileChannel.open(path);
			ByteBuffer buffer = ByteBuffer.allocate(16);
			String content = "";
			int noOfBytesRead = fc.read(buffer);

			while(noOfBytesRead != -1) {
				buffer.flip();
				
				while(buffer.hasRemaining()) {
					content = content + (char) buffer.get();
				}

				buffer.clear();
				noOfBytesRead = fc.read(buffer);
			}
			
			fc.close();
			
			content = content.replaceAll("\r", "");
			String[] lines = content.split("\n");
			
			for (String line : lines) {
				if (!line.trim().isEmpty()) {
					String asmName = line.split(" ")[0];
					String delayString = line.split(" ")[1];

					mutex.acquire();
					assemblyFileList.add(asmName);
					assemblyFileDelayList.add(Integer.parseInt(delayString));
					mutex.release();
				}
				
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void run(){
		isRunning = true;
		try {
			while (isRunning) {
				mutex.acquire();
				
				if (!assemblyFileList.isEmpty()) {
					String assemblyFile = assemblyFileList.get(0);
					int delay = assemblyFileDelayList.get(0);
					assemblyFileList.remove(0);
					assemblyFileDelayList.remove(0);
					mutex.release();

					String binFile = assemblyFile.replaceFirst(".asm", ".bin");

					int instructionSize = assembler.createBinaryFile(assemblyFile, binFile);

					fileInputQueue.insertItem(new ProcessImage(binFile, 0, instructionSize));

					Thread.sleep(delay);
				}
				
				else {
					mutex.release();
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

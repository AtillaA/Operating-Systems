import java.util.List;
import java.util.concurrent.Semaphore;

public class FileInputConsumer extends Thread {

	private volatile Storage<ProcessImage> fileInputQueue;
	private volatile List<ProcessImage> readyQueue;
	private volatile Memory memory;
	private volatile Assembler assembler;

	private Semaphore mutex;
	
	private volatile boolean isRunning;

	public FileInputConsumer(Semaphore mtx, Storage<ProcessImage> fileInputQ, List<ProcessImage> readyQ, Memory memory, Assembler assembler) {
		this.mutex = mtx;
		this.fileInputQueue = fileInputQ;
		this.readyQueue = readyQ;
		this.memory = memory;
		this.assembler = assembler;
	}

	@Override
	public void run(){
		isRunning = true;
		try {
			while (isRunning) {
				ProcessImage p = fileInputQueue.removeItem();
				int instructionSize = p.LR;
				
				while (true) {
					mutex.acquire();
					int availableMemoryIndex = memory.getEmptyIndexForGivenSize(instructionSize);
					mutex.release();

					if (availableMemoryIndex != -1) {
						char[] process = assembler.readBinaryFile(instructionSize, p.processName);
						p.BR = availableMemoryIndex;
						p.LR = p.LR + availableMemoryIndex;

						mutex.acquire();
						memory.addInstructions(process, instructionSize, availableMemoryIndex); // memory e koydu.
						readyQueue.add(p);
						System.out.println("Process " + p.processName + " is loaded !");
						mutex.release();
						break;
					}

					else {
						Thread.sleep(2000);
					}
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

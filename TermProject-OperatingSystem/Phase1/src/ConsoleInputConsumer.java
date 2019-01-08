import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class ConsoleInputConsumer extends Thread {

	private volatile Storage<Integer> consoleInputQueue;
	private volatile List<ProcessImage> blockedQueue;
	private volatile List<ProcessImage> readyQueue;

	private Semaphore mutex;

	private volatile boolean isRunning;

	public ConsoleInputConsumer(Semaphore mtx, Storage<Integer> consoleInputQ, List<ProcessImage> blockedQ, List<ProcessImage> readyQ) {
		this.mutex = mtx;
		this.consoleInputQueue = consoleInputQ;
		this.blockedQueue = blockedQ;
		this.readyQueue = readyQ;
	}

	@Override
	public void run(){
		isRunning = true;
		try {
			while (isRunning) {
				int i = consoleInputQueue.removeItem();
				
				while (true) {
					mutex.acquire();
					boolean isBlockedQueueEmpty = blockedQueue.isEmpty();
					mutex.release();

					if (!isBlockedQueueEmpty) {
						mutex.acquire();
						ProcessImage p = blockedQueue.get(0);
						blockedQueue.remove(0);
						p.V = i;
						readyQueue.add(p);
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

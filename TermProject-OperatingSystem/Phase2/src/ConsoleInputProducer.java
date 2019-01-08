import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class ConsoleInputProducer extends Thread {

	private volatile Storage<Integer> consoleInputQueue;

	private Semaphore mutex;

	private volatile boolean isRunning;

	public ConsoleInputProducer(Semaphore mtx, Storage<Integer> consoleInputQ) {
		this.mutex = mtx;
		this.consoleInputQueue = consoleInputQ;
	}

	@Override
	public void run(){
		isRunning = true;
		Scanner in = new Scanner(System.in);
		
		while (isRunning) {
			if (in.hasNextInt()) {
				int i = in.nextInt();
				consoleInputQueue.insertItem(i);
			}
		}
		
		in.close();
	}

	public void stopThread() {
		isRunning = false;
	}
}

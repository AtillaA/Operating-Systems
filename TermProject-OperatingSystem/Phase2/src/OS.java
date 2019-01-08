import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class OS extends Thread {
	
	private final int QUANTUM = 5;

	private CPU cpu;
	private Memory memory;
	private	Assembler assembler;
	
	private volatile List<ProcessImage> readyQueue;
	private volatile List<ProcessImage> blockedQueue;
	private volatile Storage<String> folderInputQueue;
	private volatile Storage<ProcessImage> fileInputQueue;
	private volatile Storage<Integer> consoleInputQueue;
	
	private Semaphore mutex;
	
	private FolderInputProducer folderInputProducer;
	private FolderInputConsumer folderInputConsumer;
	private FileInputProducer fileInputProducer;
	private FileInputConsumer fileInputConsumer;
	private ConsoleInputProducer consoleInputProducer;
	private ConsoleInputConsumer consoleInputConsumer;

	public OS(int size) {
		this.memory = new Memory(size);
		this.cpu = new CPU(memory);
		this.assembler = new Assembler();
		
		this.mutex=new Semaphore(1);
		
		this.readyQueue = new ArrayList<ProcessImage>();
		this.blockedQueue = new ArrayList<ProcessImage>();
		this.fileInputQueue = new Storage<ProcessImage>();
		this.folderInputQueue = new Storage<String>();
		this.consoleInputQueue = new Storage<Integer>();
		
		
		this.fileInputProducer = new FileInputProducer(mutex, fileInputQueue, assembler);
		fileInputProducer.start();
		
		this.fileInputConsumer = new FileInputConsumer(mutex, fileInputQueue, readyQueue, memory, assembler);
		fileInputConsumer.start();
		
		this.folderInputProducer = new FolderInputProducer(mutex, folderInputQueue);
		folderInputProducer.start();
		
		this.folderInputConsumer = new FolderInputConsumer(mutex, folderInputQueue, fileInputProducer);
		folderInputConsumer.start();
		
		this.consoleInputConsumer = new ConsoleInputConsumer(mutex, consoleInputQueue, blockedQueue, readyQueue);
		consoleInputConsumer.start();
		
		this.consoleInputProducer = new ConsoleInputProducer(mutex, consoleInputQueue);
		consoleInputProducer.start();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(100);
			boolean willClose = false;
			
			while (true) {

				mutex.acquire();
				boolean isBlockedQueueEmpty = blockedQueue.isEmpty();
				boolean isReadyQueueEmpty = readyQueue.isEmpty();
				mutex.release();

				if(isBlockedQueueEmpty && isReadyQueueEmpty) {
					if (!willClose) {
						willClose = true;
					}
					
					else {
						break;
					}
				}
				
				else {
					willClose = false;
				}

				if (isReadyQueueEmpty) {
					Thread.sleep(2000);
				}
				
				else {
					mutex.acquire();
					System.out.println("Executing " + (readyQueue.get(0)).processName);
					mutex.release();
					
					cpu.transferFromImage(readyQueue.get(0));
					for (int i = 0; i < QUANTUM; i++) {
						if (cpu.getPC() < cpu.getLR()) {
							mutex.acquire();
							cpu.fetch(); 
							mutex.release();
							
							int returnCode = cpu.decodeExecute();

							if (returnCode == 0)  {
								mutex.acquire();
								System.out.println("Process " + readyQueue.get(0).processName + " made a system call for ");
								mutex.release();
								
								if (cpu.getV() == 0) {
									mutex.acquire();
									System.out.println( "Input, transfering to blocked queue and waiting for input...");
									mutex.release();
									
									ProcessImage p=new ProcessImage();
									this.cpu.transferToImage(p);
									
									mutex.acquire();
									readyQueue.remove(0);
									blockedQueue.add(p);
									mutex.release();
								} 
								else { //syscall for output
									mutex.acquire();
									System.out.print("Output Value: ");
									mutex.release();
									
									ProcessImage p=new ProcessImage();
									cpu.transferToImage(p);

									mutex.acquire();
									readyQueue.remove(0);
									System.out.println( p.V +"\n");
									readyQueue.add(p);
									mutex.release();
								}
								//Process blocked, need to end quantum prematurely
								break;
							}
						}
						else {
							mutex.acquire();
							System.out.println("Process " + readyQueue.get(0).processName +" has been finished! Removing from the queue...\n" );
							mutex.release();
							
							ProcessImage p = new ProcessImage();
							cpu.transferToImage(p);
							p.writeToDumpFile();

							mutex.acquire();
							readyQueue.remove(0);
							memory.removeInstructions(p.LR - p.BR, p.BR);
							mutex.release();
							break;
						}

						if (i == QUANTUM - 1) {
							//quantum finished put the process at the end of readyQ
							mutex.acquire();
							System.out.println ("Context Switch! Allocated quantum have been reached, switching to next process...\n");
							mutex.release();
							
							ProcessImage p = new ProcessImage();
							cpu.transferToImage(p);  

							mutex.acquire();
							readyQueue.remove(0);
							readyQueue.add(p);
							mutex.release();
						}
					}
				}
			}
			
			if(fileInputProducer != null) fileInputProducer.stopThread();
			fileInputConsumer.stopThread();
			consoleInputProducer.stopThread();
			consoleInputConsumer.stopThread();
			System.out.println("Execution of all processes has finished!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

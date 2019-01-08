public class MainApplication {

	public static void main(String[] args) {
		OS os = new OS(5000);
		os.loadProcessList("inputSequence.txt");
		
		os.start();
	}
}

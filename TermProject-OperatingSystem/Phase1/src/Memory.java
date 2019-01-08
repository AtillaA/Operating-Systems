public class Memory {

	private int memorySize;
	private char[] memory;
	private char[] bits;

	public Memory(int size) {
		memorySize = size;
		memory = new char[size];

		bits = new char[size / 8];
		
		for (int i = 0; i < bits.length; i++) {
			bits[i] = 0;
		}
	}

	public void addInstructions(char[] buffer, int bufferSize, int BR)
	{
		for (int i = BR; i < bufferSize+BR; i++)
		{
			this.memory[i] = buffer[i - BR];
		}

		for (int i = BR / 8; i < (BR + bufferSize) / 8 + 1 ; i++) {
			bits[i] = 1;
		}
	}
	
	public void removeInstructions(int size, int BR)
	{
		for (int i = BR / 8; i < (BR + size) / 8 + 1 ; i++) {
			bits[i] = 0;
		}
	}

	public char[]getInstruction(int PC, int BR)
	{
		char[]instruction = new char[4];
		instruction[0]=memory[PC+BR];
		instruction[1]=memory[PC+BR+1];
		instruction[2]=memory[PC+BR+2];
		instruction[3]=memory[PC+BR+3];

		return instruction;

	}

	public int getEmptyIndexForGivenSize(int size)
	{
		for (int i = 0; i < bits.length; i++) {
			int j;
			
			for (j = 0; j < size / 8 + 1; j++) {
				if (bits[i + j] == 1) {
					break;
				}
			}
			
			if (j == size / 8 + 1) {
				return i * 8;
			}
		}
		
		return -1;
	}

	public int getMemorySize() {
		return memorySize;
	}

}

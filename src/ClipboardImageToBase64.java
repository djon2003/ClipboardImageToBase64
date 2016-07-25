public class ClipboardImageToBase64 {

	public static void main(String[] args) throws InterruptedException {
		if (!System.getProperty("java.runtime.version").startsWith("1.7")) {
			System.out.println("This program has to be launched using Java 1.7 version");
			return;
		}
		
		System.out.println("Launched");
		ClipBoardListener c = new ClipBoardListener();
		c.run();
		
		Boolean quit = true;
		while(quit) {
			Thread.sleep(10000000);
		}
		System.out.println("Ended");
	}

}




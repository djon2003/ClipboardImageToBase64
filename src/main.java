import java.io.Console;

public class main {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
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




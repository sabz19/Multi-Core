import java.util.concurrent.Semaphore;

public class Test implements Runnable {

	private static final int MAX_INT = 1;
	static Semaphore s0 = new Semaphore(MAX_INT),s1 = new Semaphore(0),s2 = new Semaphore(0);
	static int n = 0;
	int id ;
	public Test(int id) {
		this.id = id;
	}
	public void executeA() throws InterruptedException {
		System.out.println(Thread.currentThread().getName() + " executeA");
		while(true) {
			s0.acquire();
			System.out.println(++n);
			s1.release();
			s2.release();
		}
	}
	public void executeB() throws InterruptedException {
		System.out.println(Thread.currentThread().getName() + " executeB");
		for(int i = 1;i <=10 ;i++) {
			s1.acquire();
			s0.release();
		}
		System.out.println("Finished");
	}
	public void executeC() throws InterruptedException {
		System.out.println(Thread.currentThread().getName() + " executeC");
		for(int i = 1;i <= 10 ;i++) {
			s2.acquire();
			s0.release();
		}
		System.out.println("Finished");
	}
	public static void main(String args[]) throws InterruptedException {
		
		Thread thread[] = new Thread[3];
		for(int i = 1; i < 3;i++) {
			Thread t = new Thread(new Test(i));
			t.start();
			thread[i] = t;
		}
		Thread t = new Thread(new Test(0));
		t.start();

	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.id == 0) {
			try {
				executeA();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (this.id == 1) {
			try {
				executeB();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				executeC();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

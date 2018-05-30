package LazySync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest implements Runnable {

	
	Lock lock = new ReentrantLock();
	
	public void lockNode() {
		lock.lock();
		System.out.println("Hello universe");
		lock.lock();
		System.out.println("Hello world");
	}
	public static void main(String args[]) {
		
		Thread t = new Thread(new LockTest());
		t.start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		lockNode();
	}
}

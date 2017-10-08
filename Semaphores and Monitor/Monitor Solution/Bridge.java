import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bridge {

	static int west_count = 0,east_count = 0,cs = 0;;
	static Lock lock = new ReentrantLock();
	static Condition west_queue = lock.newCondition(),east_queue = lock.newCondition();
	static int time_stamp = 0;
	/* 
	 * Enter monitor to check if a thread can enter critical section
	 */ 
	public void get_Bridge_Access(Direction direction) throws InterruptedException {
		
		try {
		lock.lock();
		arrive_Bridge(direction); //enter monitor to check if cs available
		}finally {
			lock.unlock();
		}
		
		cross_Bridge(direction);  //Exit monitor and enter cs

		try {
		lock.lock();
		leave_Bridge(direction); //Enter monitor to signal waiting threads
		}finally{
			lock.unlock();
		}
		
	}
	
	public void arrive_Bridge(Direction direction) throws InterruptedException {
		

		if(direction == Direction.EAST) {
			east_count ++;
			
		}else {
			west_count ++;
		}
		while(cs > 0) {
			System.out.println(Thread.currentThread().getName() + " " + direction + " is waiting");
			return_Queue(direction).await();
			//System.out.println(Thread.currentThread().getName()+ " " + direction + " is up");
		}
	
		cs ++;
		if(direction == Direction.EAST) {
			east_count --;
		}else {
			west_count --;
		}
	}
	/*
	 * Critical Section (Vehicle passing through the bridge)
	 */
	public void cross_Bridge(Direction direction) throws InterruptedException {
		
		System.out.println(time_stamp + " " + Thread.currentThread().getName() + " " + direction);
		Thread.sleep(250);
	}
	/*
	 * Vehicle leaves the bridge
	 */
	public void leave_Bridge(Direction direction) {
		

		cs --;
		
		if(direction == Direction.EAST) {
		
			if(west_count > 0) {
				west_queue.signal();
			}else {
				east_queue.signal();
			}
		}
		else if(direction == Direction.WEST){
		
			if(east_count > 0) {
				east_queue.signal();
			}else {
				west_queue.signal();
			}
		}
	}
	/*
	 * Return conditional queue based on direction
	 */
	public Condition return_Queue(Direction direction) {
		
		if(direction == Direction.EAST)
			return east_queue;
		else 
			return west_queue;
	}
	public int return_Count(Direction direction) {
		
		if(direction == Direction.EAST) {
			return east_count;
		}else {
			return west_count;
		}
	}
	/*
	 * Randomly choose time for a cs request
	 */
	public int seek_time_to_cross() {
		Random rand = new Random();
		
		return rand.nextInt(500); 
	}
	
	/*
	 * Method to generate a random direction in the bridge for a vehicle
	 */
	public static int seek_direction_code() {
		Random rand = new Random();
		return rand.nextInt(2);
	}
	
	public static void main(String[] args) throws Exception {
		
		int num_vehicles = 10;
		
		Thread[] thread = new Thread[num_vehicles];
		//i is assigned as the number plate value for a vehicle
		for(int i = 0;i < num_vehicles;i++) {
			thread[i] = new Thread(new Vehicle(i,Direction.valueOf(seek_direction_code() )));
			thread[i].start();
		}	
		for(int i = 0;i < num_vehicles;i++) {
			thread[i].join();
		}
		
	}

}

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bridge {

	static int west_count = 0,east_count = 0,w_waiting = 0,e_waiting = 0;
	static Lock lock = new ReentrantLock(),file_write = new ReentrantLock();
	static Condition west_queue = lock.newCondition(),east_queue = lock.newCondition();
	static boolean wsignal = false, esignal = false;
	static int cs_count = 0;
	static BufferedWriter f;
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
			
			e_waiting ++;
			if(w_waiting > 0 ) {
				return_Queue(direction).await();
			}
			east_count ++;
		
		}else {
			w_waiting ++;
			if(e_waiting > 0) {
				return_Queue(direction).await();
			}
			west_count ++;
		}
	}
	/*
	 * Critical Section (Vehicle passing through the bridge)
	 */
	public void cross_Bridge(Direction direction) throws InterruptedException {
		
		file_write.lock();
		  try {
	            f.write(direction.toString() +" "+cs_count++);
	            f.newLine();
	           
	        }
	        catch(IOException e) {
	           System.out.println("Something went wrong with write");
	        }
		file_write.unlock();
		Thread.sleep(250);
	}
	/*
	 * Vehicle leaves the bridge
	 */
	public void leave_Bridge(Direction direction) {
		
		if(direction == Direction.EAST) {
			east_count --;
			e_waiting --;
			if(w_waiting > 0) {

				if(east_count == 0 ) {
					cs_count = 0;
					west_queue.signal();
				}
			}else {
				if(e_waiting > 0) {
					east_queue.signalAll();
				}
			}
		}
		else if(direction == Direction.WEST){
			west_count --;
			w_waiting --;
			if(e_waiting > 0) {

				if(west_count == 0 ) {
					cs_count = 0;
					east_queue.signal();
				}
			}else {
				if(w_waiting > 0) {
					west_queue.signalAll();
				}
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
		f = new BufferedWriter(new FileWriter("critical.txt"));
		Thread[] thread = new Thread[num_vehicles];
		//i is assigned as the number plate value for a vehicle
		for(int i = 0;i < num_vehicles;i++) {
			thread[i] = new Thread(new Vehicle(i,Direction.valueOf(seek_direction_code() )));
			thread[i].start();
		}	
		for(int i = 0;i < num_vehicles;i++) {
			thread[i].join();
		}
		f.close();
		
	}

}

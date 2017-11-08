import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Bridge {

	static Semaphore mutex = new Semaphore(1),wx = new Semaphore(1),ex = new Semaphore(1);
	static Semaphore wmutex = new Semaphore(1),emutex = new Semaphore(1),file_write = 
			new Semaphore(1);
	static boolean wsignal = false,esignal = false;
	static int west_count = 0,east_count = 0,cs_count = 0;
	static AtomicInteger w_waiting = new AtomicInteger(),e_waiting = new AtomicInteger();
	static BufferedWriter f;
	
	/*
	 * Thread that wants to enter bridge needs to go through these three methods
	 */ 
	public void get_Bridge_Access(Direction direction) throws InterruptedException, IOException {
		
		arrive_Bridge(direction);
		cross_Bridge(direction);
		leave_Bridge(direction);
	}
	
	public void arrive_Bridge(Direction direction) throws InterruptedException {
		 
		
		if(direction == Direction.WEST) {
			
			w_waiting.getAndIncrement();
			wx.acquire();
			wmutex.acquire();
			west_count ++;
			if(west_count == 1) {
				mutex.acquire();
			}
			if(e_waiting.get() == 0){
				wx.release();
			}else {
				wsignal = true;
			}
			w_waiting.getAndDecrement();
			wmutex.release();
			
		}else if(direction == Direction.EAST){
			
			e_waiting.getAndIncrement();
			ex.acquire();
			emutex.acquire();
			east_count ++;
			if(east_count == 1) {
				mutex.acquire();
			}
			if(w_waiting.get() == 0) {
				ex.release();
			}else {
				esignal = true;
			}
			e_waiting.getAndDecrement();
			
			emutex.release();
		}
	}
	
	/*
	 * Critical Section (Vehicle passing through the bridge)
	 * Multiple vehicles from one direction can enter the cs
	 */
	
	public void cross_Bridge(Direction direction) throws InterruptedException {
		
		file_write.acquire();
		  try {
	            f.write(direction.toString() +" "+cs_count++);
	            f.newLine();
	           
	        }
	        catch(IOException e) {
	           System.out.println("Something went wrong with write");
	        }
		file_write.release();
		Thread.sleep(500);

	}
	
	/*
	 * Vehicle leaves the bridge
	 */
	public void leave_Bridge(Direction direction) throws InterruptedException, IOException {
		
		
		if(direction == Direction.WEST) {
		
			wmutex.acquire();
			
			west_count --;
			if(west_count == 0) {
		      cs_count = 0;
				if(esignal) {
					ex.release();
					esignal = false;
				}
				mutex.release();
			}
			
			wmutex.release();
			
		}else if(direction == Direction.EAST){
			
			emutex.acquire();
			
			east_count --;
			
			if(east_count == 0) {
				cs_count = 0;
			     			   
				if(wsignal)
				 { 
					wx.release();
					wsignal = false;
				}
				 mutex.release();
			}
			
			emutex.release();
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
		f = new BufferedWriter(new FileWriter("critical.txt"));
		
		
		Thread[] thread = new Thread[num_vehicles];
		//i is assigned as the number plate value for a vehicle
	
		for(int i = 0;i < num_vehicles;i++) {
			thread[i] = new Thread(new Vehicle(i,Direction.valueOf(seek_direction_code() )));
			thread[i].start();
		}	
		
		for(int i = 0;i < num_vehicles; i++) {
			thread[i].join();
		}
		f.close();
		
	}

}

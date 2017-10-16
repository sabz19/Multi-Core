import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.omg.Messaging.SyncScopeHelper;


public class Bridge {

	static AtomicInteger west_count = new AtomicInteger(0),east_count = new AtomicInteger(0);
	static int wftl = 0,eftl = 0,temp_west,temp_east,wtemp_count = 0,etemp_count = 0;
	static int time_stamp = 0;
	static Semaphore mutex = new Semaphore(1);
	static Semaphore wmutex = new Semaphore(1),emutex = new Semaphore(1);
	static Semaphore wx = new Semaphore(1), ex = new Semaphore(1);
	static boolean wsignal = false,esignal = false,ftl_w = false,ftl_e = false;
	/*
	 * Enter monitor to check if a thread can enter critical section
	 */ 
	public void get_Bridge_Access(Direction direction) throws InterruptedException {
		
		arrive_Bridge(direction);
		cross_Bridge(direction);
		leave_Bridge(direction);
	}
	
	public void arrive_Bridge(Direction direction) throws InterruptedException {
		 
		
		if(direction == Direction.WEST) {
			
			west_count.getAndIncrement();
			System.out.println(Thread.currentThread().getName()+ " WEST ");
			wx.acquire();
			
			wmutex.acquire();
			wtemp_count = west_count.get();
			System.out.println(Thread.currentThread().getName()+" acquired wmutex count = "+ west_count);
			
			if(wtemp_count == 1) {
				mutex.acquire();
			}
			System.out.println(Thread.currentThread().getName()+" releasing wmutex");
			wmutex.release();
			wx.release();
			
		}else if(direction == Direction.EAST){
			
		
			east_count.getAndIncrement();
			System.out.println( Thread.currentThread().getName() + " EAST ");
			ex.acquire();
			emutex.acquire();
			
			etemp_count = east_count.get();
			System.out.println(Thread.currentThread().getName()+" acquired emutex count = "+ east_count);
			if(east_count.get() == 1) {
				mutex.acquire();
			}
			System.out.println(Thread.currentThread().getName()+" releasing emutex");
			
			emutex.release();
			ex.release();
		}
		
	}
	/*
	 * Critical Section (Vehicle passing through the bridge)
	 */
	public void cross_Bridge(Direction direction) throws InterruptedException {
		
	
		Thread.sleep(1000);
		System.out.println(" critical section " + direction + " Thread name:" +
		Thread.currentThread().getName());

	}
	/*
	 * Vehicle leaves the bridge
	 */
	public void leave_Bridge(Direction direction) throws InterruptedException {
		
		if(direction == Direction.WEST) {
			wmutex.acquire();
			west_count.decrementAndGet();
			wtemp_count --;
			System.out.println(Thread.currentThread().getName() + " leaving bridge"
					+ "West count = "+west_count);
			if(east_count.get() > 0) {
				
				if(ftl_w == false) {
					System.out.println("Some guy waiting at the east" );
					ftl_w = true;
					wx.acquire();
					wsignal = true;
				}
			}
			if(wtemp_count == 0) {
				
				ftl_w = false;
				mutex.release();
				if(esignal == true) {

					esignal = false;
					ex.release();
				}
			}
			System.out.println(Thread.currentThread().getName()+ " releasing wmutex");
			wmutex.release();
		}else if(direction == Direction.EAST){
			
			emutex.acquire();
			east_count.getAndDecrement();
			etemp_count --;
			System.out.println(Thread.currentThread().getName() + " leaving bridge"
					+ "East count = "+east_count);
			if(west_count.get() > 0) {
			
				if(ftl_e == false) {
					System.out.println("Some guy waiting at the west " );
					ftl_e = true;
					ex.acquire();
					esignal = true;
				}
			}
			if(etemp_count == 0) {
				
				ftl_e = false;
				mutex.release();
				if(wsignal == true) {

					wsignal = false;
					wx.release();
				}
			}
			System.out.println(Thread.currentThread().getName()+ " releasing emutex");
			emutex.release();
		}
	
		
	}

	/*
	 * Randomly choose time for a cs request
	 */
	public int seek_time_to_cross() {
		Random rand = new Random();
		
		return rand.nextInt(1000); 
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
		
	}

}

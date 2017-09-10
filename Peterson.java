import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

class Process extends Peterson implements Runnable {
	
	int id,id_array[] = new int[num_levels];
	public Process(int id ){
		super();
		this.id = id;
	}
	@Override
	public void run() {
		
		int new_id = 0,lock_id = 0;
		for(int i = 0;i < num_levels;i++ ){
		
			if(i == 0){
				new_id = this.id; 
			}
			if(new_id % 2 == 0){
				new_id = (new_id - 1) / 2;	
			}else{
				new_id = new_id / 2;
			}

			lock_id = new_id % 2;
			id_array[i] = new_id;
			locks[new_id].lock(lock_id);
			
			System.out.println("New id for thread "+this.id + " is "+new_id);
			
		}
		try {
			System.out.println("Thread"+ id +"Entering mutex");
			mutex();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		unlock(id_array);
	}	
}

public class Peterson  {

	static int n = 8,new_n = 0,num_levels = 0;
	public static Peterson[] locks;
	int level[] = new int[n];
	boolean flag[] ;
	int victim = 0;
	

	public Peterson(){
		
		flag = new boolean[2];
	}
	
	public void lock(int i){
		
		flag[i] = true;
		victim = i;
		while(flag[1-i] && victim == i){
			//No op
		}
		return;
	}
	
	public void mutex() throws InterruptedException{
		
		Thread.sleep(1000);
		// Write something
	}
	
	/*
	 * Unlock the lock held by a thread at every level
	 */
	public void unlock(int id_array[]){
		
		for(int i = num_levels - 1;i >= 0;i--){
			int id = id_array[i];
			locks[id].flag[id % 2] = false;
		}
		return;
	}
	
	public static int base10_to_base2(){
		double k = Math.log(new_n)/Math.log(2);
		return (int)k;
	}
	
	public static boolean check_power_of_two(int num){
		return num != 0 && (((num) & (num-1))==0);
	}
	
	public static void main (String args[]) throws InterruptedException{
		/*
		 *Initialize all object locks at every level
		 */
		new_n = n;
		if(!check_power_of_two(n)){
			while(!check_power_of_two(new_n)){
				new_n++;
			}
			System.out.println(new_n);
		}
		
		locks = new Peterson[new_n+1];
		
		for(int i = 0;i < new_n;i++){
			Peterson p = new Peterson();
			locks[i] = p;
		}
		num_levels = base10_to_base2(); // get number of levels for the tree
		System.out.println(num_levels);
		for(int i = 0;i < n;i++){
			
			Process p = new Process(i+n);
			Thread thread = new Thread(p);
			thread.start();
			
		}
	}
}
  
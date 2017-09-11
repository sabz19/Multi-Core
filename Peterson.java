
class Process extends Peterson implements Runnable {
	
	int id,id_array[] = new int[num_levels],unlock_id_array[] = new int[num_levels+1];
	public Process(int id ){
		super();
		this.id = id;
	}
	@Override
	public void run() {
		
		int new_id = 0,lock_id = 0;
		int round = 0;
		for(int i = 0;i < num_levels;i++ ){
		
			if(i == 0){
				new_id = this.id; 
			}
			unlock_id_array[i] = new_id;
			if(new_id % 2 == 0){
				lock_id = new_id % 2;
				new_id = (new_id - 1) / 2;	
			}else{
				lock_id = new_id % 2;
				new_id = new_id / 2;
			}
			id_array[i] = new_id;
			locks[new_id].lock(lock_id);
			System.out.println(this.id +" Won the round " + ++round);
			
			//System.out.println("New id for thread "+this.id + " is "+new_id);
			
		}
		try {
			System.out.println("Thread "+ id +" Entering mutex ");
			mutex();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		System.out.println("Thread " + id + " About to unlock "+ " Time stamp is "+ ++time_stamp);
		System.out.println("----------------------------");
		unlock(id_array,unlock_id_array);
	}	
}

public class Peterson  {

	static int n = 8, new_n = 0,num_levels = 0;
	static int time_stamp = 0;
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
			//System.out.println(flag[1-i]);
			//No op
		}
		return;
	}
	
	public void mutex() throws InterruptedException{
		
		Thread.sleep(10);
		// Write something
	}
	
	/*
	 * Unlock the lock held by a thread at every level
	 */
	public void unlock(int id_array[],int unlock_array[]){
		
		for(int i = num_levels - 1;i >= 0;i--){
			int id = id_array[i];	
			locks[id].flag[unlock_array[i] % 2] = false;
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
		
		locks = new Peterson[new_n + 1];
		num_levels = base10_to_base2(); // get number of levels for the tree
		
		int num_locks = (int) ((Math.pow(2, num_levels))-1);
		for(int i = 0;i <=num_locks;i++){
			Peterson p = new Peterson();
			locks[i] = p;
		}
		
		Thread[] t = new Thread[n+1];

		for(int i = 0;i < n;i++){
			
			Process p = new Process(i+num_locks);
			t[i] = new Thread(p);
		}
		
		for(int i = 0;i < n; i++){
			t[i].start();
		}
	}
}
  
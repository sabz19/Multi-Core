import java.io.FileWriter;
import java.util.HashSet;

import org.omg.Messaging.SyncScopeHelper;

public class Bakery implements Runnable {

	static int n = 10;
	static boolean choosing[] = new boolean[n+1];
	static int token[] = new int[n+1];
	int id;
	public Bakery(int id){
		this.id = id;
	}
	@Override
	public void run() {
			
			lock(id);
			try {
				mutex_func();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			unlock(id);
	}
	
	/*
	 * Obtain lock for a particular thread
	 */
	public void lock(int i){
		
		choosing[i] = true;
		int max = Integer.MIN_VALUE;
		for(int x = 0; x < n;x++){
			if(i != x){
				//System.out.println("Token of " + x+ " is"+  + token[x]);
				if(max < token[x])
					max = token[x];
			}
		}
		token[i] = max + 1;
		
		// Give priority to ID in case of clash
		
		choosing[i] = false;
	
		for(int j = 0;j < n;j++){
		
			while(choosing[j]){
				
			}
			if(token[j] == token[i]){
				if(i < j){
					token[j]++;
				}else{
					token[i]++;
				}
			}
			while(token[j] != 0 && (token[j] < token[i])){
					//Do nothing
				//System.out.println(id);	
			}
			
		}
		System.out.println(i + " going to enter mutual exclusion");
		return;
	
	}
	/*
	 * Mutex func
	 */
	public void mutex_func() throws InterruptedException{
		//System.out.println("Current thread in mutex"+ id);
		Thread.sleep(0);
	}
	
	/*
	 * Unlock lock
	 */
	public void unlock(int i){
		//System.out.println("Unlocking for "+ i);
		token[i] = 0;
	}
	
	public static void main(String args[]) throws InterruptedException{
	
		Thread t[] = new Thread[n+1];
		for(int i = 0; i<n ;i++){
			Bakery b = new Bakery(i);
			t[i] = new Thread(b);
			t[i].start();
			
		}
	}
}

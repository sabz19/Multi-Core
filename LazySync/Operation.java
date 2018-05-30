package LazySync;

import java.util.HashSet;
import java.util.Random;
/**
 * 
 * @author sabarish
 * This is a class that performs insert, delete, search or replace operations
 */
public class Operation implements Runnable {

	/**
	 * list object is the object that contains the linked list
	 * set is used for contains operation to store all nodes visited
	 */
	private Linked_List list;
	private HashSet<Integer> set;
	private boolean test;

	private int id;
	private int opId;

	public static int randomNum = 100;   //Highest number for range 1 to randomNum
	
	public Operation(Linked_List list,boolean test,int id,int opId) {
		this.list = list;
		set = new HashSet<Integer>();
		this.test = test;
		this.id = id;
		this.opId = opId;
	}

	public Operation(Linked_List list,int opId,int id) {
		this.test = true;
		this.opId = opId;
		this.id = id;
		this.list = list;
	}

	/**
	 * Generate a random number between 1 to 3 
	 * @return random integer
	 */
	public int generateRandomOperation() {
		Random random = new Random();
		return random.nextInt(4)+1;
	}

	/**
	 * Run method for the thread
	 */
	@Override
	public void run() {

		if(test == false) {
			Random random = new Random();
			switch(opId) {
			case 0:
				try {
					int key = random.nextInt(randomNum) + 1;
					list.insert(key,this);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			case 1:
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				list.replace(random.nextInt(randomNum) + 1, random.nextInt(randomNum) + 1,false);
				break;
			case 2:
				try {
					Thread.sleep(3000);
					list.contains(random.nextInt(randomNum) + 1,false);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				break;
			case 3:
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				list.delete(random.nextInt(randomNum) + 1);
				break;
			default: System.out.println("Invalid operation");
			}
		}else {

			if(this.opId ==  0) { 
				try {
					list.insert(this.id, this);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
			}

			else if (this.opId ==  1) {
				list.replace(Linked_List.testRemoveKey, Linked_List.testAddKey,true);
			}
			else if (this.opId ==  2) {
				try {
					Thread.sleep(30);
				}catch (Exception e) {
					// TODO: handle exception
				}
				list.contains(Linked_List.testRemoveKey,true);
			}
			else if(this.opId == 3) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				list.contains(Linked_List.testAddKey,true);

			}

		}
	}


}

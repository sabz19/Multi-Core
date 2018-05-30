package LazySync;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

/**
 * @author sabarish
 * Class to implement the linked list with multiple concurrent operations operating simultaneously
 * Head and Tail nodes are sentinel nodes and have a constant key value of 0 and Integer.MAX_VALUE respectively
 */
public class Linked_List {

	private Node head;
	private Node tail;
	private int n = 0;

	static FileOperation writeReplace;
	static File f = new File("replace.txt");
	static int systemStateCounter = 0;
	static int testRemoveKey = -1,testAddKey = -1;

	public Linked_List() {
		head = new Node(0);
		tail = new Node(Integer.MAX_VALUE);
		head.next = tail;
	}

	public static void initiateFileWriters() {
		try {
			writeReplace = new FileOperation(f);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	/**
	 * Locks predecessor and current nodes
	 * if successful then returns true
	 * @param current
	 * @param pred
	 * @param key
	 * @param isFirst
	 * @return
	 */
	private boolean lockTargets(Node current,Node pred) {

		try {
			pred.lock.lock(); 
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			current.lock.lock();
		}catch(Exception e ) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Validates whether a node that is supposed to be present is present
	 * Check if the predecessor node is marked
	 * If not then check if pred is still pointing to current
	 * Else return false since node has been removed
	 * @param pred
	 * @param current
	 * @return
	 */
	private boolean validate(Node current,Node pred) {

		if(pred.isRemoved == false) {
			if(pred.next == current) {
				return true;
			}
		}
		return false;
	}

	/**
	 * public method for calls from Operation class objects
	 * @param key
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void insert(int key,Operation op) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException {
		insert(head,op,key);
	}
	/**
	 * Insert operation requires a lock on two nodes
	 * @param head
	 * @param key
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private boolean insert(Node head,Operation op,int key) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException {

		while(true) {
			Node pred = head; //predecessor node
			Node current = head.next; //current node 
			while(current != null) { 
				if(current.key == key) { // If key matching then return since no duplicates allowed
					return false;
				}
				else if(current.key > key) {

					boolean lockSuccess = false;
					boolean validateSuccess = false;

					try {
						/*
						 * Attempt to lock
						 */
						lockSuccess = lockTargets(current,pred);

						if(!lockSuccess) {
							break; // Retry
						}
						validateSuccess = validate(current,pred);

						if(validateSuccess) {
							System.out.println("Key added : " + key);

							systemStateCounter ++;  //increment node count in the list

							Node newNode = new Node(key);
							newNode.next = current;
							pred.next = newNode;
							return true;
						}else {
							break; //retry
						}
					}finally {
						if(lockSuccess) {
							pred.lock.unlock();
							current.lock.unlock();
						}
					}
				}
				pred = current; //Move pred pointer to current
				current = current.next; //Move current to next item in the list
			}
		}	
	}
	/**
	 * Modified contains operation
	 * Stores a list of all nodes that have been traversed
	 * Checks if the target node is marked as deleted or replaced
	 * If it is replace, checks if the the node to be removed is marked. If not then returns false, else returns true
	 * If it is marked, it means the node has been officially removed from the list, so returns false
	 * if test variable is set to true, the contains is doing look up for test operation
	 * @param key
	 * @param op
	 */
	public void contains(int key,boolean test) {
		contains(head,key,test);
	}
	/**
	 */
	private boolean contains(Node head,int key,boolean test) {

		Node current = head.next;
		boolean keyFound = false;
		while(current.key <= key) {
			if(current.key == key) {

				keyFound = true;
				if(current.isRemoved) {
					if(test == true) {
						writeReplace.writeToFile(key + " false");
					}
					if(!test)
						System.out.println("Contains for key :" + key +" = false");
					return false;
				}
				if(current.isReplacing) {
					Node removeKey = current.replaceKey;
					if(removeKey.isRemoved == true) {

						if(test == true) {
							writeReplace.writeToFile("Linearized " + key+ " true");
						}
						if(!test) {
							System.out.println("Contains for key :" + key +" = true");
						}
						return true;
					}else {
						writeReplace.writeToFile(key + " false");
						if(!test)
							System.out.println("Contains for key :" + key +" = false");
						return false;
					}
				}
				if(test) {
					writeReplace.writeToFile("Before Linearization" + key + " true");
				}
				if(!test) {
					System.out.println("Contains for key :" + key +" = true");
				}
				return true;

			}
			current = current.next;
		}

		if(!keyFound) {
			writeReplace.writeToFile(key + " false");
		}
		return false;
	}
	/**
	 * 
	 * @param key
	 */
	public void delete(int key) {
		delete(head,key);
	}

	private boolean delete(Node head,int key) {

		while(true) {
			//System.out.println("Deleting key :" + key);
			Node current = head.next;
			Node pred = head;
			boolean validateSuccess = false;
			boolean lockSuccess = false;
			try {
				while(current.key <= key) {
					if(current.key == key) {
						if(current.isRemoved == true) {
							return false;
						}
						lockSuccess = lockTargets(current,pred);				
						validateSuccess = validate(current,pred);

						if(!validateSuccess) {
							//	System.out.println("Validation unsuccessful for delete :" + key);
							break; //Retry
						}else {
							System.out.println("Key Deleted:"+ key);
							systemStateCounter --;
							current.isRemoved = true;
							pred.next = current.next;

							return true;
						}
					}
					pred = current;
					current = current.next;
				}
				if(current.key > key)
					return false;
			}finally {
				if(lockSuccess) {
					current.lock.unlock();
					pred.lock.unlock();
				}
			}
		}

	}
	/**
	 * 
	 * @param key1
	 * @param key2
	 */
	public void replace(int key1, int key2,boolean test) {
		replace(head,key1,key2,test);
	}

	private boolean replace(Node head,int removeKey, int addKey,boolean test) {

		if(test) {
			writeReplace.writeToFile("replace " + removeKey + " with "+ addKey);
		}
		if(removeKey == addKey) { //return false if both keys are same
			return false;
		}
		if(contains(head,addKey,false) == true) {
			return false;
		}

		while(true) {

			Node currentRemoveKey = head.next, currentAddKey = head.next;
			Node predRemoveKey = head, predAddKey = head;
			Node currentLocked = null, predLocked = null;

			boolean lockRemoveKeySuccess = false, validateRemoveKeySuccess = false;
			boolean lockAddKeySuccess = false, validateAddKeySuccess = false;
			boolean keyFound = false, addKeyFound = false;
			boolean consecutive = false;

			System.out.println("Attempting replacement: " + removeKey + " with "+ addKey);
			try {
				while(currentRemoveKey != null ) {	//First obtain the window for removing the node
					try {
						if(currentRemoveKey.key == removeKey) {
							keyFound = true;
							lockRemoveKeySuccess = lockTargets(currentRemoveKey,predRemoveKey);
							if(contains(head,addKey,false) == true) {
								return false;
							}
							validateRemoveKeySuccess = validate(currentRemoveKey,predRemoveKey);
							break;
						}
						predRemoveKey = currentRemoveKey;
						currentRemoveKey = currentRemoveKey.next;
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				/*
				 * If remove key not found, then nothing to replace
				 */
				if(keyFound == false) {
					//System.out.println("Did not find key:"+ removeKey +" for replace");
					return false;
				}

				if(validateRemoveKeySuccess == false) {
					//System.out.println("Validation fail for remove key :" + removeKey + ", retrying");
				}else {
					//System.out.println("Validation for remove successful :" + removeKey + "->" + addKey);
				}


				/*
				 * Check for corner case where the keys to be replaced are consecutive
				 */

				Node current = head.next, pred = head;
				while(current.key <= removeKey) {
					if(current.key == removeKey) {
						if(pred.key < addKey && current.key > addKey || current.key < addKey && current.next.key > addKey)
							consecutive = true;
					}
					pred = current;
					current = current.next;
				}

				if(lockRemoveKeySuccess) {  //if first window was successfully obtained find the window for adding the key

					if(consecutive == true) {
						//System.out.println("Consecutive is true");
						while(currentAddKey.next != null) {

							if(currentAddKey.key == removeKey) {
								if(currentAddKey.next.key == addKey || predAddKey.key == addKey) {
									addKeyFound = true;
								}
								currentLocked = currentAddKey.next;
								predLocked = predAddKey;
								//System.out.println("Attemping to get add lock by operation : "+ removeKey + " ->" + addKey);
								lockAddKeySuccess = lockTargets(currentLocked, predLocked);
								validateAddKeySuccess = true;
								//System.out.println("Attempt add lock successful : "+ removeKey + " ->" + addKey);
								break;

							}
							predAddKey = currentAddKey;
							currentAddKey = currentAddKey.next;
						}

					}else{ //if nodes to be replaced are not consecutive
						while(currentAddKey != null) {
							if(currentAddKey.key == addKey) {
								addKeyFound = true;
							}
							if (currentAddKey.key > addKey){
								currentLocked = currentAddKey;
								predLocked = predAddKey;
								//System.out.println("Current key:" + currentAddKey.key + " "+ predAddKey.key);
								//System.out.println("Attemping to get add lock by operation : "+ removeKey + " ->" + addKey);
								lockAddKeySuccess = lockTargets(currentLocked,predLocked);
								//System.out.println("Attempt add lock successful : "+ removeKey + " ->" + addKey);
								validateAddKeySuccess = validate(currentLocked,predLocked);
								break;

							}

							predAddKey = currentAddKey;
							currentAddKey = currentAddKey.next;
						}
					}
				}
				if(addKeyFound == true) {
					return false;
				}

				if(validateAddKeySuccess == false) {
					//System.out.println("Validation fail for add key :" + addKey + ", retrying");
				}else {
					//System.out.println("Validation add key successful");
				}


				if(validateAddKeySuccess && validateRemoveKeySuccess) {
					//System.out.println("Both the validations successful !");

					/*
					 * Create new node to be added to the list
					 * Mark node as replacing
					 * Insert the information of the key that was replaced with this 
					 * Add the new node to the list
					 */
					Node addNode = new Node(addKey);
					addNode.isReplacing = true;
					addNode.replaceKey = currentRemoveKey;
					addNode.next = currentLocked;
					predLocked.next = addNode;
					//System.out.println("Window of add key "+ addKey + " = "+ currentLocked.key + " " + predLocked.key);

					/*
					 * Remove old node from the list
					 */
					if(!consecutive) { //Because replace nodes are consecutive there is no need to change anything
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						currentRemoveKey.isRemoved = true;
						predRemoveKey.next = currentRemoveKey.next;
					}
					//System.out.println("Window of remove key "+ removeKey + " = "+ currentRemoveKey.key + " " + predRemoveKey.key);
					/*
					 * Mark added node as not replacing
					 */
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					addNode.isReplacing = false;
					System.out.println("Replace successful! "+removeKey + " " + addKey );
					return true;
				}	
			}finally {
				if(lockRemoveKeySuccess) {
					currentRemoveKey.lock.unlock();
					predRemoveKey.lock.unlock();
				}
				if(lockAddKeySuccess) {
					currentLocked.lock.unlock();
					predLocked.lock.unlock();
				}
			}	
		}
	}
	/**
	 * Traverse the entire list and print out all the elements
	 */
	public int traversal() {

		System.out.println("----------------------");
		System.out.println("Final linked list");
		System.out.println("---------------------");
		Node node = head.next;
		int counter = 0;
		while(node != null) {
			if(node.next == tail) {
				System.out.println(node.key);
				counter ++;
				break;
			}
			counter ++;
			System.out.print(node.key + " -> ");
			node = node.next;
		}
		System.out.println();
		return counter;
	}

	/**
	 * Testing the algorithm with two inserts : 2 and 3
	 * Replace operation that replaces 2 with 5
	 * Multiple contains operations running concurrently
	 * @throws InterruptedException
	 */
	public static void testAlg() throws InterruptedException {


		int n = 2000; //number of contains threads
		Linked_List list = new Linked_List();
		ArrayList<Integer> randList = new ArrayList<Integer>();
		int numInserts;

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the number of keys in the linked list for test operation:");

		numInserts = sc.nextInt();

		Thread threads [] = new Thread[n + 1];
		Thread insertThread [] = new Thread[numInserts];
		System.out.println("Enter the keys to be inserted into the linked list");

		for(int i = 0; i < numInserts;i++) {
			int num = sc.nextInt();
			Operation operation = new Operation(list,0,num);
			insertThread[i] = new Thread(operation);
			insertThread[i].start();
		}
		

		/*
		 * Add numbers for contains operation to either search for add key or remove key
		 * The values 2 and 3 are the opIds in Operation class
		 */
		randList.add(2);
		randList.add(3);
		
		for(int i = 0; i < numInserts ;i ++) {
			insertThread[i].join();
		}
		list.traversal();
		System.out.println("Enter the remove key for replace operation:");
		testRemoveKey = sc.nextInt();
		System.out.println("Enter the add key for replace operation:");
		testAddKey = sc.nextInt();


		for(int i = 0; i < n + 1 ;i++) {
			Operation operation = null;
			if(i == 0) {
				operation = new Operation(list,1,i);
			}else {
				Collections.shuffle(randList);
				operation = new Operation(list,randList.get(0),i);
			}

			threads[i] = new Thread(operation);
			threads[i].start();
		}
		for(int i = 0; i < n;i++) {
			threads[i].join();
		}
		list.traversal();
		writeReplace.fileClose();

	}
	/**
	 * Create n threads of Object operation and start all threads
	 * @throws InterruptedException 
	 */
	public static void main(String args[]) throws InterruptedException {

		initiateFileWriters();

		Random random = new Random();
		Scanner sc = new Scanner(System.in);
		Linked_List list = new Linked_List();

		System.out.println("Enter number of insert operation (Threads are randomly assigned different operations apart from insert.):");
		list.n = sc.nextInt();
		int n = list.n;

		System.out.println("Enter the highest value for a range of numbers to be inserted");
		Operation.randomNum = sc.nextInt();

		Thread insertThread[] = new Thread[n];

		for(int i = 0 ;i < n;i++) {
			Operation operation = new Operation(list,false,i,0);
			insertThread[i] = new Thread(operation);
			insertThread[i].start();
		}
		for(int i = 0; i < n; i++) {
			insertThread[i].join();
		}
		System.out.println("Number of elements after insert :" +systemStateCounter);
		Thread threads [] = new Thread[n + 1];

		for(int i = 0; i < n;i++) {

			Operation operation = new Operation(list,false,i,random.nextInt(3) + 1);
			threads[i] = new Thread(operation);
			threads[i].start();
		}


		for(int i = 0; i < n;i++) {
			threads[i].join();
		}
		int count = list.traversal();

		System.out.println();
		System.out.println("Current number of elements in the system :" + systemStateCounter);
		if(count == systemStateCounter) {
			System.out.println("State of the system is as expected");
		}else {
			System.out.println("Violation");
		}
		System.out.println();
		System.out.println("-----------------------------------");
		testAlg();

	}
}

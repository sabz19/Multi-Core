package LazySync;
/**
 * Node to store in the linked list
 */
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node{

	Lock lock;
	Node next;
	Node replaceKey;  //Pointer to node that is going to be removed in the replace operation
	int key;
	boolean isRemoved ;  //Used by Replace and Delete operation
	boolean isReplacing; //Used by replace operation

	public Node(int key) {
		this.key = key;
		next = null;
		lock = new ReentrantLock();
		isRemoved = false;
		isReplacing = false;
	}

}

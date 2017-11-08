import java.io.IOException;

public class Vehicle extends Bridge implements Runnable{

	int number_plate;
	Direction direction;
 
	Vehicle(int number_plate,Direction direction){
		this.direction = direction;
		this.number_plate = number_plate;
	}

	@Override
	public void run() {
		
		long time = seek_time_to_cross();
		try {
			Thread.sleep(time);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		/*
		 * Enter monitor to request cs access
		 */
		try {
			try {
				get_Bridge_Access(direction);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	

}


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
		System.out.println(" I am thread "+ number_plate +" I am about to request access");
		try {
			get_Bridge_Access(direction);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("I am thread" + number_plate + " I am done crossing");
		
	}
	

}

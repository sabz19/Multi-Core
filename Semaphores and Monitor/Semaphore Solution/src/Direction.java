
public enum Direction {
	
	EAST,WEST ;
	
	public static Direction valueOf(int direction_code) throws Exception {
		switch(direction_code) {
		case 0: return EAST;
		case 1: return WEST;
		default: throw new Exception("Bad direction code");
		}
	}
	
}

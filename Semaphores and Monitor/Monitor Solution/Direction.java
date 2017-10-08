
public enum Direction {
	
	EAST (0),
	WEST (1),
	;
	private int direction_code;
	Direction(int direction_code){
		this.direction_code = direction_code;
	}
	
	public static Direction valueOf(int direction_code) throws Exception {
		switch(direction_code) {
		case 0: return EAST;
		case 1: return WEST;
		default: throw new Exception("Bad direction code");
		}
	}
	
}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class CritTest {

	public static void main(String[] args) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader("critical.txt"));
		String line="";
		int first_line = 0;
		String current_direction = "";
		while((line = br.readLine()) != null) {
			String line_split[] = line.split("\\s+");
		
			if(first_line == 0) {
				current_direction = line_split[0];
			}
			first_line ++;
			if(!(line_split[0].equalsIgnoreCase(current_direction))) {
				if(Integer.parseInt(line_split[1]) > 0) {
					throw new Exception("Violation!");
				}
				first_line = 0;
			}
		}
		System.out.println("No Violation");
	}

}

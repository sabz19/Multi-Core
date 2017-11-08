import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CritTest {

	public static void main(String[] args) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader("critical.txt"));
		String line="";
		String current_direction = "";
		int first_line = 0;
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

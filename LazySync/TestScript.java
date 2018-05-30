package LazySync;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestScript {

	public static void main(String args[]) throws IOException {
		boolean violation = false;
		BufferedReader br = new BufferedReader(new FileReader("replace.txt"));
		int addKey = 0, removeKey = 0;
		String line ="";
		while((line = br.readLine() )!= null) {
			if(line.contains("replace")) {
				String split[] = line.split("\\s");
				removeKey = Integer.parseInt(split[1]);
				addKey = Integer.parseInt(split[3]);
			}
			if(line.contains("Linearized")) {
				while((line = br.readLine()) != null) {
					if(line.equalsIgnoreCase(removeKey +" true")) {
						System.out.println(line);
						violation = true;
						break;
					}
				}
			}
			if(violation) {
				System.out.println("VIOLATION!");
				break;
			}
		}
		if(!violation) {
			System.out.println("No violation");
		}
	}
}

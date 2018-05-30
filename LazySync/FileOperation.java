package LazySync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class FileOperation {

	FileOutputStream fileWriter ;
	
	public FileOperation(File file) throws IOException {
		
		fileWriter = new FileOutputStream(file);
		
	}
	/**
	 * Write a string s to the file
	 * @param s
	 */
	synchronized public void writeToFile(String s) {
		try {
			fileWriter.write(s.getBytes());
			fileWriter.write("\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void fileClose() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

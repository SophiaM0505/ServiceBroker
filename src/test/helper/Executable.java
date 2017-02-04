package test.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Executable {
	
	public static void writeout(String content) {
		try {

			//String content = "hello sophie.";

			File file = new File("/Users/zeqianmeng/Documents/workspace/AHE3/test_data/executable.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*public static void main(String[] args) {
		writeout("hello sophia");
	}*/

}

package lifr.util.examples.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SimpleFileReader {
	
	public static String readFile(String filename){
		BufferedReader fileReader = null;
		StringBuffer readBuffer = new StringBuffer();
		try{
			fileReader = new BufferedReader(new FileReader(filename));
		} catch(FileNotFoundException e){
			e.printStackTrace();
			return "";
		}
		String line;
		try {
			while ( (line = fileReader.readLine()) != null){
				if (line.trim().length() > 0){
					try{
						readBuffer.append(new String(line.getBytes(), "UTF8"));
						readBuffer.append('\n');
					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		readBuffer.append('\n');
		try{
			fileReader.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return readBuffer.toString();
    }

}

package lifr.util.tests.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class FileIO {
	
	public static void writeFile(String input, String filepath, String filename){
		PrintWriter writer = null;
		String[] in = input.split("/n");
		try{
			writer = new PrintWriter(filepath+filename, "UTF-8");
			for(int i = 0; i < in.length; i++){
				writer.println(in[i]);
			}
		    writer.close();
		}catch (IOException e){
			e.printStackTrace();
		}
    }
	
	public static void writeNewFile(String input, String filepath, String filename){
		PrintWriter writer = null;
		
		try{
			File file= new File(filepath + filename);
			file.delete();
    		file.createNewFile();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		String[] in = input.split("/n");
		try{
			writer = new PrintWriter(filepath+filename, "UTF-8");
			for(int i = 0; i < in.length; i++){
				writer.println(in[i]);
			}
		    writer.close();
		}catch (IOException e){
			e.printStackTrace();
		}
    }
	
	public static void appendToFile(String input, String filepath, String filename, boolean initialize){
		PrintWriter out = null;
		try{
			if(initialize){
				out = new PrintWriter(
						new BufferedWriter(
								new FileWriter(filepath + filename, false))) ;
			}else{
			out = new PrintWriter(
					new BufferedWriter(
							new FileWriter(filepath + filename, true))) ;
			}
		    out.println(input);
		}catch (IOException e) {
		    e.printStackTrace();
		}finally{
			out.close();
		}
    }
	
	public static String readFileWLines(String filename){
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
	
	public static String readFile(String filename){
		BufferedReader fileReader = null;
		StringBuffer readBuffer = new StringBuffer();
		try{
			fileReader = new BufferedReader(new FileReader(filename));
		} catch(FileNotFoundException e){
			e.printStackTrace();
			File file = new File(filename);
			System.err.println(file.getAbsolutePath());
		}
		String line;
		try {
			while ( (line = fileReader.readLine()) != null){
				try{
					readBuffer.append(new String(line.getBytes(), "UTF8"));
//					readBuffer.append('\n');
				}catch(UnsupportedEncodingException e){
					e.printStackTrace();
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
//		readBuffer.append('\n');
		try{
			fileReader.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return readBuffer.toString();
    }
	
	public static HashMap<String, String> readAllFilesFromFolder(String folderpath, String suffix){
		File folder = new File(folderpath);
		File[] listOfFiles = folder.listFiles();
		HashMap<String, String> fileContents = new HashMap<String, String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile() && file.getName().endsWith(suffix)) {
				String filename = file.getName();
				String content = FileIO.readFileWLines(folderpath + filename);
				fileContents.put(filename.replace(suffix, ""), content);
			} 
		}
		return fileContents;
	}
}

package de.mannheim.ids.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;

/**
 * 
 * @author margaretha
 *
 */
public class Utilities {
	public static void createDirectory(String directory){
		
		if (directory==null || directory.isEmpty()){
			throw new IllegalArgumentException("Directory cannot be null or empty.");
		}
		
		File dir = new File(directory);
		if (!dir.exists()) { dir.mkdirs(); }
	}

	public static OutputStreamWriter createWriter (String outputFile) throws IOException {		
		File file = new File(outputFile);		
		if (!file.exists()) file.createNewFile();

		OutputStreamWriter os = new OutputStreamWriter(new BufferedOutputStream(
				new FileOutputStream(file)), "UTF-8");		

		return os;	
	}
		
	public static String normalizeIndex(String input, String[] indexList) throws IOException{
		String normalizedStr = Normalizer.normalize(input,Form.NFKD).toUpperCase();
		normalizedStr = normalizedStr.substring(0,1);	
		
//			if (Character.isLetterOrDigit(normalizedStr.charAt(0))){
//				return normalizedStr.substring(0,1);	
//			}
		if (Arrays.asList(indexList).contains(normalizedStr)){
			return normalizedStr;
		}
		else{ return "Char"; }		
	}
}

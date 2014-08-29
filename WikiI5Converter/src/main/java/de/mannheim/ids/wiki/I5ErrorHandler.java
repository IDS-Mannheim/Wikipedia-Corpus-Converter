package de.mannheim.ids.wiki;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** This class handles the validation error and writes the error messages to 
 *  a text file with name format: 
 *  xces-[language]wiki-[published date]-[articles/discussions]-error.txt.
 * 
 * @author margaretha
 *
 */

public class I5ErrorHandler implements ErrorHandler{
	
	private boolean isValid;
	private String errorMessage;
	OutputStreamWriter errorWriter;
	int numOfInvalidText=0;
	
	public I5ErrorHandler(String wikifile) throws I5Exception {
		File errorFile = new File("logs/i5-"+wikifile+"-error.txt");
		try {
			errorFile.createNewFile();
			errorWriter = new OutputStreamWriter(new FileOutputStream(errorFile));
		} catch (IOException e) {		
			throw new I5Exception("Failed creating the error file.",e);
		}
	}
	
	@Override
	public void warning(SAXParseException exception) throws SAXException {		
		exception.printStackTrace();
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		setErrorMessage(exception.getMessage()+"\n");			
		setValid(false);
		System.err.println("Invalid I5");
		exception.printStackTrace();
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {				
		setErrorMessage(exception.getMessage()+"\n");
		setValid(false);
		System.err.println("Invalid I5: Fatal Error");
		exception.printStackTrace();
	}

	public void write(String xmlPath) throws I5Exception{
		numOfInvalidText++;		
		try {
			errorWriter.append(numOfInvalidText+" ");		
			errorWriter.append(xmlPath);
			errorWriter.append("\n");
			errorWriter.append(getErrorMessage());
			errorWriter.append("\n\n");
		} catch (IOException e) {
			throw new I5Exception("Error writing I5.",e);
		}
	}
	
	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}	
	
	void reset() {
		setValid(true);
		setErrorMessage("");
	}
	
	void close() throws I5Exception {
		System.out.println("Number Of Invalid Text: "+numOfInvalidText);
		try{
			errorWriter.append("Number Of Invalid Text: "+numOfInvalidText);		
			errorWriter.close();
		}
		catch (IOException e) {
			throw new I5Exception(e);
		}		
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}

package de.mannheim.ids.wiki;

import java.io.IOException;

public class WikiXMLConverterExample {
	
	public static void main(String[] args) throws IOException {		
		// Set the language of the Wikipedia		
		String language = "de";		
		// Set output directory
		String xmlOutputDir = "./xml-"+language;		
		// Set wikidump filepath
		String wikidump = "dewiki-20130728-sample.xml";
		String type = "discussions";
		
		WikiXMLConverter.convert(wikidump, language, type, xmlOutputDir);		
	}	
}

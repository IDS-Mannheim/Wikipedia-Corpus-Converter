package de.mannheim.ids.wiki;

import java.util.ArrayList;
import java.util.List;

import de.mannheim.ids.util.LanguageProperties;

public class WikiXMLConverterExample {
	
	public static void main(String[] args) {
		
		// Set the language of the Wikipedia		
		String language = "de";
		
		// Set the Wikipedia namespaces to parse
		// Only articles and discussions are supported.
		List<String> namespaces = new ArrayList<String>();
		namespaces.add("0"); // articles
		namespaces.add("1"); // discussions
		
		// Initialize the language property
		// The language properties have been defined for the following languages:
		// german, french, hungarian, norwegian and polish		
		LanguageProperties lp = new LanguageProperties(language,namespaces);
		
		// Set output directory
		String xmlOutputDir = "./xml-"+language;		
		// Set wikidump filepath
		String wikidump = "dewiki-20130728-sample.xml";			
		
		long startTime = System.nanoTime();
		
		try{
			WikiXMLProcessor wxp = new WikiXMLProcessor(lp,namespaces);
			wxp.createWikiXML(wikidump,xmlOutputDir);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Wikitext XML converter execution time "+duration);
	}	
}

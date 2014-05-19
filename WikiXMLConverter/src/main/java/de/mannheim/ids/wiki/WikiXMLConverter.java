package de.mannheim.ids.wiki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.mannheim.ids.util.LanguageProperties;
import de.mannheim.ids.wiki.WikiXMLProcessor;

/** Main class for Wikitext to XML conversion
 * 
 * @author margaretha
 *
 */

public class WikiXMLConverter {
	
	public static void main(String[] args) throws IOException {
		long startTime = System.nanoTime();
		
		String language = args[0];
		String wikidump = args[1];
		String article_namespace = args[2];
		String talk_namespace = args[3];
		
		List<String> namespaces= new ArrayList<String>();
		if (!article_namespace.equals("null"))
			namespaces.add(article_namespace);
		if (!talk_namespace.equals("null")) 
			namespaces.add(talk_namespace);
		
//		String language = "de";
//		String wikidump = "input/q.xml";
		//String[] namespaces = {"1"};
		
		LanguageProperties lp = new LanguageProperties(language,namespaces);				
		String xmlOutputDir = "./xml"+language; 
		try{
		WikiXMLProcessor wxp = new WikiXMLProcessor(lp,namespaces);
		wxp.createWikiXML(wikidump,xmlOutputDir);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//wxp.createSingleWikiXML(wikidump,xmlOutputDir);		
		
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Wikitext XML converter execution time "+duration);
	}
}

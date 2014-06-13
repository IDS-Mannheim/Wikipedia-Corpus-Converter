package de.mannheim.ids.wiki;

import java.io.File;

public class WikiXCESConverterExample {

	public static void main(String[] args) throws Exception {
		
		// Increase JAXP limits
		System.setProperty("entityExpansionLimit", "0");
		System.setProperty("totalEntitySizeLimit", "0");
		System.setProperty("PARAMETER_ENTITY_SIZE_LIMIT", "0");
		
		// Set parameters
		String xmlFolder= "xml-de";
		String type= "articles";
		
		// The dumpFilename should be in the following format:
		// [2 letter language code]wiki-[year][month][date]-[type]		
		String dumpFilename= "dewiki-20130728-articles";
		String outputFile="xces/dewiki-20130728-articles.xces";
		
		// Set the inflectives filepath or null if not available
		String inflectives="inflectives.xml";
		
		// Download the i5 dtd from: http://corpora.ids-mannheim.de/I5/DTD/i5.dtd 
		String dtdfile="dtd/i5.dtd";
		String encoding="UTF-8";
		
		// Set output filepath
		File output = new File(outputFile);
		// Set the XSLT files
		File xsl = new File ("xslt/Templates.xsl");
		
		// Initialize the processor
		WikiXCESProcessor wikiXCESProcessor = new WikiXCESProcessor(xmlFolder,xsl,
				type,dumpFilename,inflectives,encoding);
		
		long startTime=System.nanoTime();
		
		//Initialize the XCES writer 
		XCESWriter w = new XCESWriter(output,dtdfile,encoding);
		
		// Do the converting and write XCES
		w.write(xmlFolder,type,dumpFilename,wikiXCESProcessor);		
		
		long endTime=System.nanoTime();					
		System.out.println("Transformation time "+ (endTime-startTime));	
		
	}
	
}

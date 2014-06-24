package de.mannheim.ids.wiki;

import java.io.File;

/** An example how to run the WikiXCESConverter
 * 
 * 	Before running this code, please execute:
 *  	./ListGenerator.sh articles xml-de articleList.xml
 *  on a terminal, to generate a list of the article pages in xml-de folder. 
 * 
 * @author margaretha
 * */
public class WikiXCESConverterExample {

	public static void main(String[] args) throws Exception {
		
		// Increase JAXP limits
		System.setProperty("entityExpansionLimit", "0");
		System.setProperty("totalEntitySizeLimit", "0");
		System.setProperty("PARAMETER_ENTITY_SIZE_LIMIT", "0");
		
		// Set parameters
		String xmlFolder= "xml-de";
		String type= "articles";
		String pageList = "articleList.xml";
		
		
		// The dumpFilename should be in the following format:
		// [2 letter language code]wiki-[year][month][date]-[type]		
		String dumpFilename= "dewiki-20130728-sample.xml";
		String outputFile="xces/dewiki-20130728-articles.xces";
		
		// Set the inflectives filepath or null if not available
		String inflectives="inflectives.xml";		
		String encoding="UTF-8";
		
		// Set output filepath
		File output = new File(outputFile);
		// Set the XSLT files
		File xsl = new File ("xslt/Templates.xsl");				
		
		//Process p = Runtime.getRuntime().exec("./ListGenerator.sh articles "+xmlFolder);
		
		// Initialize the processor
		WikiI5Processor wikiXCESProcessor = new WikiI5Processor(xmlFolder,xsl,
				type,dumpFilename,inflectives,encoding);
		
		long startTime=System.nanoTime();
		
		//Initialize the XCES writer 
		I5Writer w = new I5Writer(output,encoding);		
				
		w.open(xmlFolder,type,dumpFilename);
		w.createCorpusHeader(wikiXCESProcessor.korpusSigle,wikiXCESProcessor.corpusTitle, 
				wikiXCESProcessor.lang, dumpFilename, wikiXCESProcessor.textType);
		
		// Do the converting and write
		wikiXCESProcessor.run(pageList, type, w);
		w.close();
		
		long endTime=System.nanoTime();					
		System.out.println("Transformation time "+ (endTime-startTime));	
		
	}
	
}

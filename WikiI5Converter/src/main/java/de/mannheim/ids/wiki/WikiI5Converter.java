package de.mannheim.ids.wiki;

import java.io.File;

/** This is the main class for converting the XML-ized Wikipages to XCES
 *  This class takes 6 arguments as inputs:
 *  1. the folder path of the XML-ized Wikipages,
 *  2. the type of the pages (articles or discussions), 
 *  3. the filename of the Wikipedia dump in format:
 *     [2 character language code]wiki-[year][month][date]-pages-meta-current.xml
 *     example: dewiki-20130728-pages-meta-current.xml
 *  4. the output file,
 *  5. the encoding of the output file, for example UTF-8 or ISO-8859-1
 *  6. the path to the XML file containing the list of inflectives.
 *  
 * @author margaretha 
 */

public class WikiI5Converter {		
		
	public static void main(String[] args) throws Exception {
		
		String xmlFolder=args[0];
		String type=args[1];
		String dumpFilename=args[2];
		String outputFile=args[3];		
		String encoding=args[4];
		String inflectives=args[5];
		
		/*String dumpFilename="dewiki-20130728-pages-meta-current.xml";
		String outputFile="output.xces";
		String type="articles";
		String xmlFolder ="xml-de/";
		String inflectives="../inflectives.xml";
		String dtdfile="dtd/i5.dtd";
		String encoding="ISO-8859-1";
		*/
		
		String pageList = type.equals("articles") ? "articleList.xml" : "discussionList.xml";
		
		File output = new File(outputFile);
		File xsl = new File ("xslt/Templates.xsl");
		
		System.setProperty("entityExpansionLimit", "0");
		System.setProperty("totalEntitySizeLimit", "0");
		System.setProperty("PARAMETER_ENTITY_SIZE_LIMIT", "0");
		
		if (inflectives.equals("null")){
			inflectives = null;
		}		
		
		WikiI5Processor wikiXCESProcessor = new WikiI5Processor(xmlFolder,xsl,
				type,dumpFilename,inflectives,encoding);
		
		long startTime=System.nanoTime();
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

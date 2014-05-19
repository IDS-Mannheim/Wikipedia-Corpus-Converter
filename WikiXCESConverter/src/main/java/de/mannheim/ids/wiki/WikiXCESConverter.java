package de.mannheim.ids.wiki;

import java.io.File;

/** This is the main class for converting the XML-ized Wikipages to XCES
 *  This class takes 7 arguments as inputs:
 *  1. the folder path of the XML-ized Wikipages,
 *  2. the type of the pages (articles or discussions), 
 *  3. the filename of the Wikipedia dump in format:
 *     [2 character language code]wiki-[year][month][date]-pages-meta-current.xml
 *     example: dewiki-20130728-pages-meta-current.xml
 *  4. the output file,   
 *  5. the path to the XML file containing the list of inflectives.
 *  6. the relative path to the DTD file from the location of the XSLT stylesheets 
 * 	7. the encoding of the output file, for example UTF-8 or ISO-8859-1
 * 
 * @author margaretha 
 */

public class WikiXCESConverter {		
		
	public static void main(String[] args) throws Exception {
		
		String xmlFolder=args[0];
		String type=args[1];
		String dumpFilename=args[2];
		String outputFile=args[3];
		String inflectives=args[4];		
		String dtdfile=args[5];
		String encoding=args[6];
		
		/*String dumpFilename="dewiki-20130728-pages-meta-current.xml";
		String outputFile="output.xces";
		String type="articles";
		String xmlFolder ="xml-de/";
		String inflectives="../inflectives.xml";
		String dtdfile="dtd/i5.dtd";
		String encoding="ISO-8859-1";
		*/
		
		File output = new File(outputFile);
		File xsl = new File ("xslt/Templates.xsl");
		
		System.setProperty("entityExpansionLimit", "0");
		System.setProperty("totalEntitySizeLimit", "0");
		System.setProperty("PARAMETER_ENTITY_SIZE_LIMIT", "0");
		
		WikiXCESProcessor wikiXCESProcessor = new WikiXCESProcessor(xmlFolder,xsl,
				type,dumpFilename,inflectives,encoding);
		
		long startTime=System.nanoTime();
		XCESWriter w = new XCESWriter(output,dtdfile,encoding);		
		w.write(xmlFolder,type,dumpFilename,wikiXCESProcessor);		
		long endTime=System.nanoTime();					
		System.out.println("Transformation time "+ (endTime-startTime));		
	}

}

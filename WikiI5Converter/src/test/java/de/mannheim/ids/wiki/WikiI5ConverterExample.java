package de.mannheim.ids.wiki;

/** An example how to run the WikiXCESConverter
 * 
 * 	Before running this code, please execute:
 *  	./WikiCorpusIndexer.sh articles de-articleIndex.xml xml-de
 *  on a terminal, to generate an index of the article pages. 
 * 
 * @author margaretha
 * */
public class WikiI5ConverterExample {

	public static void main(String[] args) throws I5Exception {				
		
		String xmlFolder= "xml-de/articles";
		String type= "articles";
		String index = "de-articleIndex.xml";
				
		// The dumpFilename should be in the following format:
		// [2 letter language code]wiki-[year][month][date]-[type]		
		String dumpFilename= "dewiki-20130728-sample.xml";
		String outputFile="i5/dewiki-20130728-"+type+".i5";
		
		// Set the inflectives filepath or null if not available
		String inflectives="inflectives.xml";		
		String encoding="UTF-8";
		
		WikiI5Converter.convert(xmlFolder, type, dumpFilename, inflectives, 
				encoding, outputFile, index);
	}
	
}

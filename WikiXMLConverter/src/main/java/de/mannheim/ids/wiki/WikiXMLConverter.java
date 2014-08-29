package de.mannheim.ids.wiki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.mannheim.ids.util.LanguageProperties;
import de.mannheim.ids.wiki.WikiXMLProcessor;

/** Main class for Wikitext to XML conversion
 * 
 * @author margaretha
 *
 */

public class WikiXMLConverter {
	
	private Options options;
	
	public WikiXMLConverter() {
		options = new Options();
		options.addOption("l", true, "The language of the Wikipedia");	
		options.addOption("w", true, "Wiki dump file");
		options.addOption("t", true, "The type of Wikipages [articles | discussions | all]");
		options.addOption("o", true, "The xml output directory");
	}
	
	public static void main(String[] args) throws Exception {		
		WikiXMLConverter converter = new WikiXMLConverter();
		converter.run(args);
	}
	
	public void run(String[] args) throws ParseException, IOException {
		
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);		
						
		String language = cmd.getOptionValue("l");	
		String type = cmd.getOptionValue("t");
		String wikidump = cmd.getOptionValue("w");
		String xmlOutputDir = cmd.getOptionValue("o");
		
		convert(wikidump, language, type, xmlOutputDir);
	}
	
	public static void convert(String wikidump, String language, String type, 
			String xmlOutputDir) throws IOException{
		
		if (wikidump == null){
			throw new IllegalArgumentException("Please specify the Wiki dump file.");
		}
		
		String[] languages = {"de","fr","hu","it","pl","no"};
		if (language == null){
			throw new IllegalArgumentException("Please specify the Wikipedia language.");
		}				
		else if (!Arrays.asList(languages).contains(language)){
			throw new IllegalArgumentException("Language is not supported. Supported " +
					"languages are de (german), fr (french), hu (hungarian), it (italian), " +
					"pl (polish), no (norwegian).");
		}
		
		List<Integer> namespaces= new ArrayList<Integer>();
		if (type == null || type.equals("all")){
			namespaces.add(0);
			namespaces.add(1);
		}
		else if (type.equals("articles")){
			namespaces.add(0);
		}
		else if (type.equals("discussions")){
			namespaces.add(1);
		}		
		else {
			throw new IllegalArgumentException("The type is not recognized. " +
					"Please specify the type as: articles, discussions, or all");
		}
		
		if (xmlOutputDir == null){
			throw new IllegalArgumentException("Please specify the XML output directory.");
		}
				
		long startTime = System.nanoTime();
		LanguageProperties lp = new LanguageProperties(language,namespaces);
		
		WikiXMLProcessor wxp = new WikiXMLProcessor(lp,namespaces);
		try {
			wxp.createWikiXML(wikidump,xmlOutputDir);
		} catch (IOException e) {
			throw new IOException("Failed creating WikiXML.", e);
		}
		
		//wxp.createSingleWikiXML(wikidump,xmlOutputDir);	
		
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("Wikitext to XML execution time "+duration);
	}
}

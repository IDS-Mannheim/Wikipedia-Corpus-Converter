package de.mannheim.ids.wiki;

import java.io.IOException;
import java.util.List;

import de.mannheim.ids.util.LanguageProperties;
import de.mannheim.ids.util.Utilities;
import de.mannheim.ids.util.WikiStatistics;

public class WikiXMLProcessor {
	
	private LanguageProperties languageProperties;	
	private List<Integer> namespaces;
	
	public WikiXMLProcessor(LanguageProperties languageProperties,List<Integer> namespaces) {
		
		if (languageProperties == null){
			throw new IllegalArgumentException("LanguageProperties cannot be null.");
		}
		if (namespaces == null){
			throw new IllegalArgumentException("Namespaces cannot be null.");
		}
		
		this.languageProperties = languageProperties;
		this.namespaces = namespaces; 
	}
	
	public void createWikiXML(String inputFile, String xmlOutputDir) throws IOException {
				
		createOutputDirectories(xmlOutputDir);		
		WikiStatistics wikiStatistics = new WikiStatistics();
		WikiXMLWriter wikiXMLWriter = new MultipleXMLWriter(xmlOutputDir,
				languageProperties.getLanguage(), wikiStatistics);
		
		process(inputFile, wikiStatistics, wikiXMLWriter);
	}
	
	public void createSingleWikiXML(String inputFile, String xmlOutputDir) throws IOException {
		Utilities.createDirectory(xmlOutputDir);		
		WikiStatistics wikiStatistics = new WikiStatistics();
		WikiXMLWriter wikiXMLWriter = new SingleXMLWriter(xmlOutputDir,
				languageProperties.getLanguage(), wikiStatistics, namespaces);
		
		process(inputFile, wikiStatistics, wikiXMLWriter);
		wikiXMLWriter.close();
	}
	
	private void process(String inputFile, WikiStatistics wikiStatistics, 
			WikiXMLWriter wikiXMLWriter) throws IOException{
		
		if (inputFile==null || inputFile.isEmpty()){
			throw new IllegalArgumentException("Input file cannot be null or empty.");
		}
		if (wikiStatistics==null){
			throw new IllegalArgumentException("WikiStatistics cannot be null.");
		}
		if (wikiXMLWriter == null){
			throw new IllegalArgumentException("WikiXMLwriter cannot be null.");
		}
		
		WikiPageReader wikiReader = new WikiPageReader(languageProperties,wikiStatistics);
		wikiReader.read(inputFile,wikiXMLWriter);		
		wikiStatistics.printStatistics();
	}
	
	private void createOutputDirectories(String xmlOutputDir){	
		Utilities.createDirectory(xmlOutputDir);		
		
		if (namespaces.contains(0)) { 
			for (String i:WikiPage.indexList) {
				Utilities.createDirectory(xmlOutputDir+"/articles/"+i);
			}
		}
		
		if (namespaces.contains(1)){ 
			for (String i:WikiPage.indexList) {
				Utilities.createDirectory(xmlOutputDir+"/discussions/"+i);
			}
		}	
	}
}

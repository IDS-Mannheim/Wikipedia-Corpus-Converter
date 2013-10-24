package de.mannheim.ids.wiki;

import java.io.IOException;
import java.io.OutputStreamWriter;

import de.mannheim.ids.util.Utilities;
import de.mannheim.ids.util.WikiStatistics;

/** This class writes an XML file for each XML-ized wiki page.
 * 
 * @author margaretha
 *
 */
public class MultipleXMLWriter implements WikiXMLWriter{

	String xmlOutputDir, language;
	int counter;
	WikiStatistics wikiStatistics;
	
	public MultipleXMLWriter(String xmlOutputDir, String language, WikiStatistics wikiStatistics) {
		this.xmlOutputDir = xmlOutputDir;
		this.counter=1;
		this.wikiStatistics = wikiStatistics;
		this.language=language;
	}
	
	@Override
	public void write(WikiPage wikiPage, boolean isDiscussion, String indent)
			throws IOException {	
		
		OutputStreamWriter writer;
		String path;		
		
		if (!wikiPage.isEmpty() && !wikiPage.wikitext.isEmpty()) {		
			
			if (isDiscussion) path = this.xmlOutputDir+"/discussions/";		
			else path = this.xmlOutputDir+"/articles/";		
			writer = Utilities.createWriter(path + wikiPage.getPageIndex()+"/"+wikiPage.getPageId()+".xml");
			System.out.println(path + wikiPage.getPageIndex()+"/"+wikiPage.getPageId()+".xml");
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			
			System.out.println(this.counter++ +" "+ wikiPage.getPageTitle());					
				
			String [] arr = wikiPage.pageStructure.split("<text></text>");
			//System.out.println(wikiPage.pageStructure);
							
			writer.append(indent);
			writer.append(arr[0]);	
			
			if (wikiPage.wikitext.equals("")){
				writer.append("<text lang=\""+language+"\"/>" );
			}
			else {
				writer.append("<text lang=\""+language+"\">\n" );
				writer.append(wikiPage.wikitext+"\n");
				writer.append("      </text>");
			}			
			writer.append(arr[1]);
			writer.close();
		}		
	}

	@Override
	public void close() {}

}

package de.mannheim.ids.wiki;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import de.mannheim.ids.util.Utilities;
import de.mannheim.ids.util.WikiStatistics;

/** This class writes the XML-ized version of all wikipedia articles in one big XML file. 
 *  Similarly, all XML-ized wikipedia talk pages are written in another big XML file. 
 * 
 * @author margaretha
 *
 */
public class SingleXMLWriter implements WikiXMLWriter {

	int counter;
	private String language;	
	private WikiStatistics wikiStatistics;
	private OutputStreamWriter articleWriter, discussionWriter;
	
	public SingleXMLWriter(String xmlOutputDir, String language, WikiStatistics 
			wikiStatistics, List<Integer> namespaces) throws IOException {
		
		if (xmlOutputDir==null || xmlOutputDir.isEmpty()){
			throw new IllegalArgumentException("Xml output directory cannot be null or empty.");
		}		
		if (language == null || language.isEmpty()){
			throw new IllegalArgumentException("Language cannot be null or empty.");
		}
		if (wikiStatistics==null){
			throw new IllegalArgumentException("WikiStatistics cannot be null.");
		}
		if (namespaces==null){
			throw new IllegalArgumentException("Namespaces cannot be null.");
		}
		
		
		this.language = language;
		this.wikiStatistics = wikiStatistics;
		this.counter=1;
		
		if (namespaces.contains(0)) 
			articleWriter = Utilities.createWriter(xmlOutputDir+"/wiki-articles.xml");		
		if (namespaces.contains(1)) 
			discussionWriter = Utilities.createWriter(xmlOutputDir+"/wiki-discussions.xml");		
	}	
	
	@Override
	public void write(WikiPage wikiPage, boolean isDiscussion, String indent)
			throws IOException {		
		
		if (isDiscussion)
			writePage(discussionWriter, wikiPage, indent);		
		else
			writePage(articleWriter, wikiPage, indent);
	}
	
	public void writePage(OutputStreamWriter writer, WikiPage wikiPage, 
			String indent) throws IOException {
		
		if (wikiPage==null){
			throw new IllegalArgumentException("WikiPage cannot be null.");
		}
		if (writer == null){
			throw new IllegalArgumentException("Writer cannot be null.");
		}
		
		writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		if (!wikiPage.isEmpty()) {			
			System.out.println(this.counter++ +" "+ wikiPage.getPageTitle());					
				
			String [] arr = wikiPage.pageStructure.split("<text></text>");
			//System.out.println(wikiPage.pageStructure);
			if (arr.length >1){				
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
			}
			else{ //throw new ArrayIndexOutOfBoundsException();								
				System.out.println("Outer Error: "+wikiPage.getPageTitle());
				wikiStatistics.addPageStructureErrors();
			} 
		}
		else{
			writer.append(wikiPage.pageStructure);
		}
	}
	
	@Override
	public void close() throws IOException{		
		if (articleWriter!=null)  articleWriter.close();
		if (discussionWriter!=null) discussionWriter.close();		
	}
}

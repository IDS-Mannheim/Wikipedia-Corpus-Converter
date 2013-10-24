package de.mannheim.ids.wiki;

import java.io.IOException;

public interface WikiXMLWriter {
	
	public void write(WikiPage wikiPage, boolean isDiscussion, String indent) throws IOException;

	public void close() throws IOException; 
}

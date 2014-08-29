package de.mannheim.ids.wiki;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class TemplateURIResolver implements URIResolver{

	@Override
	public Source resolve(String href, String base) throws TransformerException {		 
		return new StreamSource(
				this.getClass().getClassLoader().getResourceAsStream(href)) ;
	}
}

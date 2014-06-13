package de.mannheim.ids.wiki;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/** This class defines how to write an IDS-XCES corpus for Wikipedia.
 * 
 * @author margaretha
 *
 */

public class XCESWriter {
	XMLEventWriter eventWriter;	
	XMLEventFactory eventFactory;
	XMLEvent newline,tab;
	XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	String dtdfile, encoding;
	
	BufferedOutputStream bos;
	
	public XCESWriter(File outputFile,String dtdfile,String encoding) throws Exception {
		this.encoding = encoding;	
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		bos = new BufferedOutputStream(new FileOutputStream(outputFile),1024*1024);
		eventWriter = outputFactory.createXMLEventWriter(bos,encoding);		
		eventFactory = XMLEventFactory.newInstance();
		newline = eventFactory.createCharacters("\n");
		tab = eventFactory.createCharacters("   ");		
				
		this.dtdfile=dtdfile;
	}
	
	protected void readIdsText(File inputFile) throws IOException, XMLStreamException {
		
		inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);		
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile),1024*1024);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(bis);
		
		eventReader = inputFactory.createFilteredReader(eventReader, createEventFilter()); 
		eventReader.nextEvent(); // ignore processing instruction
		eventReader.nextEvent(); // ignore dtd
		
		while (eventReader.hasNext()){			
			eventWriter.add(eventReader.nextEvent());			
		}
		eventWriter.add(newline);
		bis.close();
		eventReader.close();		
	}
	 
	private EventFilter createEventFilter() {
		EventFilter filter = new EventFilter() {			
			@Override
			public boolean accept(XMLEvent event) {
				if (event.isEndDocument() /*|| event.getEventType() == XMLStreamConstants.DTD*/) return false;
				else return true;
			}
		};
		
		return filter;

	}
	
	void write(String xmlFolder,String type,String dumpFilename,WikiXCESProcessor wikiXCESProcessor) throws Exception {			 
		
		eventWriter.add(eventFactory.createStartDocument(this.encoding));
		eventWriter.add(newline);
		
		String dtd = "<!DOCTYPE idsCorpus PUBLIC \"-//IDS//DTD IDS-XCES 1.0//EN\" \""+this.dtdfile+"\">";
		
		eventWriter.add(eventFactory.createDTD(dtd));
		eventWriter.add(newline);		
		
		eventWriter.add(eventFactory.createStartElement("","","idsCorpus"));
		eventWriter.add(eventFactory.createAttribute("version", "1.0"));
		eventWriter.add(eventFactory.createAttribute("TEIform", "teiCorpus.2"));
		eventWriter.add(newline);
				
		createCorpusHeader(wikiXCESProcessor.korpusSigle,wikiXCESProcessor.corpusTitle, 
				wikiXCESProcessor.lang, dumpFilename, wikiXCESProcessor.textType);
		
		String idList;
		if (type.equals("articles")){
			idList = xmlFolder+"/articleList.xml";
		} else{ idList = xmlFolder+"/discussionList.xml"; }
		
		
		//create documents and texts
		try {
			wikiXCESProcessor.run(idList, type, this);
			
		} catch (XPathExpressionException | SAXException e) {
			e.printStackTrace();
		}
		
		eventWriter.add(eventFactory.createEndElement("","idsCorpus",""));
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
		bos.close();
	}	
	
	private void createLeafNode(int level,String elementName, Iterator<Attribute> attributes,
			String text) throws XMLStreamException {
		
		QName name = new QName(elementName);
		StartElement startElement = eventFactory.createStartElement(name, attributes, null);
		Characters content = eventFactory.createCharacters(text);
		EndElement endElement = eventFactory.createEndElement(name, null);
		
		createIndent(level);
		eventWriter.add(startElement);
		eventWriter.add(content);
		eventWriter.add(endElement);
		eventWriter.add(newline);
	}
	
	void createIndent(int indent) throws XMLStreamException {
		for (int i=0; i<indent;i++){
			eventWriter.add(tab);
		}
	}
	
	void createIdsDocStartElement(String docId) throws XMLStreamException {
		createIndent(1);
		eventWriter.add(eventFactory.createStartElement("", "", "idsDoc"));
		eventWriter.add(eventFactory.createAttribute("type", "text"));
		eventWriter.add(eventFactory.createAttribute("version", "1.0"));
		eventWriter.add(eventFactory.createAttribute("TEIform", "TEI.2"));
		eventWriter.add(eventFactory.createAttribute("id", docId));
		eventWriter.add(newline);
	}
	
	void createIdsDocEndElement() throws XMLStreamException {	
		createIndent(1);
		eventWriter.add(eventFactory.createEndElement("","","idsDoc"));
		eventWriter.add(newline);	
	}
	
	void createIdsDocHeader(String docSigle, String docTitle) throws XMLStreamException {
		int level=2;		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","idsHeader"));
		eventWriter.add(eventFactory.createAttribute("type", "document"));
		eventWriter.add(eventFactory.createAttribute("pattern", "text"));
		eventWriter.add(eventFactory.createAttribute("status", "new"));
		eventWriter.add(eventFactory.createAttribute("version", "1.0"));
		eventWriter.add(eventFactory.createAttribute("TEIform", "teiHeader"));
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","fileDesc"));
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","titleStmt"));
		eventWriter.add(newline);
		level++;
		createLeafNode(level,"dokumentSigle", null, docSigle);
		createLeafNode(level,"d.title", null, docTitle);
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","titleStmt",""));
		eventWriter.add(newline);
		
		createStaticIdsHeader(level);
		
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","fileDesc"));
		eventWriter.add(newline);
		
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","idsHeader"));
		eventWriter.add(newline);
	}
	
	String createIdsDocTitle(String type,String index,int docNr){
		if (type.equals("articles")){			
			try {
				Integer.parseInt(index);
				return "Wikipedia, Anfangszahl "+index;
			} catch (Exception e) {
				return "Wikipedia, Anfangsbuchstabe "+index+" Teil "+String.format("%02d",docNr); 
			}
		}
		else{
			try {
				Integer.parseInt(index);
				return "Wikipedia, Diskussionen zu Artikeln mit Anfangszahl " + index;
			}catch (Exception e) {
				return "Wikipedia, Diskussionen zu Artikeln mit Anfangsbuchstabe " +index+" Teil "+String.format("%02d",docNr);
			}
		} 		
	}
	
	private void createStaticIdsHeader(int level) throws XMLStreamException {		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","publicationStmt"));
		eventWriter.add(newline);
		
		level++;
		createLeafNode(level,"distributor", null, "");		
		createLeafNode(level,"pubAddress", null, "");
				
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(eventFactory.createAttribute("region", "world"));
		attributes.add(eventFactory.createAttribute("status", "restricted"));
		createLeafNode(level,"availability", attributes.iterator(), "");
		attributes.clear();
				
		createLeafNode(level,"pubDate", null, "");
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","publicationStmt",""));
		eventWriter.add(newline);
		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","sourceDesc"));
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","biblStruct"));
		eventWriter.add(eventFactory.createAttribute("Default", "n"));
		eventWriter.add(newline);
		
		createIndent(level);		
		eventWriter.add(eventFactory.createStartElement("","","monogr"));
		eventWriter.add(newline);

		level++;
		attributes.add(eventFactory.createAttribute("type", "main"));
		createLeafNode(level,"h.title", attributes.iterator(), null);
		attributes.clear();
				
		createLeafNode(level,"imprint", null, "");
		
		level--; createIndent(level);		
		eventWriter.add(eventFactory.createEndElement("","","monogr"));
		eventWriter.add(newline);
		
		level--; createIndent(level);		
		eventWriter.add(eventFactory.createEndElement("","","biblStruct"));
		eventWriter.add(newline);
			
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","sourceDesc"));
		eventWriter.add(newline);
	}
	
	private void createCorpusHeader(String korpusSigle, String korpusTitel, String lang, 
			String dumpFilename, String textType) throws XMLStreamException {		
		int level=1;		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","idsHeader"));
		eventWriter.add(eventFactory.createAttribute("type", "corpus"));
		eventWriter.add(eventFactory.createAttribute("pattern", "allesaußerZtg/Zschr"));
		eventWriter.add(eventFactory.createAttribute("status", "new"));
		eventWriter.add(eventFactory.createAttribute("version", "1.0"));
		eventWriter.add(eventFactory.createAttribute("TEIform", "teiHeader"));
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","fileDesc"));
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","titleStmt"));
		eventWriter.add(newline);
		level++;
		createLeafNode(level,"korpusSigle", null, korpusSigle);		
		createLeafNode(level,"c.title", null, korpusTitel);
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","titleStmt",""));
		eventWriter.add(newline);
				
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(eventFactory.createAttribute("version", "1.0"));
		createLeafNode(level,"editionStmt", attributes.iterator(), null);
		attributes.clear();
		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","publicationStmt"));
		eventWriter.add(newline);
		
		level++;
		createLeafNode(level, "distributor", null, "Institut für Deutsche Sprache");		
		createLeafNode(level,"pubAddress", null, "Postfach 10 16 21, D-68016 Mannheim");			
		createLeafNode(level,"telephone", null, "+49 (0)621 1581 0");
		
		attributes.add(eventFactory.createAttribute("type", "www"));
		createLeafNode(level,"eAddress", attributes.iterator(), "http://www.ids-mannheim.de");
		createLeafNode(level,"eAddress", attributes.iterator(), "http://www.ids-mannheim.de/kl/projekte/korpora/");		
		attributes.clear();
		
		attributes.add(eventFactory.createAttribute("type", "email"));
		createLeafNode(level,"eAddress", attributes.iterator(), "dereko@ids-mannheim.de");
		attributes.clear();
		
		attributes.add(eventFactory.createAttribute("status", "restricted"));
		createLeafNode(level,"availability", attributes.iterator(), "This document, the IDS-Wikipedia."+lang+
				"-Corpus, is part of the Archive of General Reference Corpora at the IDS. It is " +
				"published under the Creative Commons Attribution-ShareAlike License. See " +
				"http://creativecommons.org/licenses/by-sa/3.0/legalcode for details. See " +
				"http://www.ids-mannheim.de/kl/projekte/korpora/releases.html on how to refer to " +
				"this document.");
		attributes.clear();
				
		attributes.add(eventFactory.createAttribute("type", "year"));
		createLeafNode(level,"pubDate", attributes.iterator(), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		attributes.clear();
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","publicationStmt",""));
		eventWriter.add(newline);
		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","sourceDesc"));
		eventWriter.add(eventFactory.createAttribute("Default", "n"));		
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","biblStruct"));
		eventWriter.add(eventFactory.createAttribute("Default", "n"));
		eventWriter.add(newline);
		
		level++; createIndent(level);		
		eventWriter.add(eventFactory.createStartElement("","","monogr"));
		eventWriter.add(newline);

		level++;
		attributes.add(eventFactory.createAttribute("type", "main"));
		createLeafNode(level,"h.title", attributes.iterator(), "Wikipedia");
		attributes.clear();
				
		createLeafNode(level,"h.author", null, "");
		createLeafNode(level,"editor", null, "wikipedia.org");
		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","edition"));				
		eventWriter.add(newline);
		level++;
		createLeafNode(level,"further", null, "Dump file &#34;"+dumpFilename+"&#34; retrieved from http://dumps.wikimedia.org");
		createLeafNode(level,"kind", null, "");
		createLeafNode(level,"appearance", null, "");
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","edition"));
		eventWriter.add(newline);
		
		createIndent(level);	
		eventWriter.add(eventFactory.createStartElement("","","imprint"));				
		eventWriter.add(newline);
		level++;
		createLeafNode(level,"publisher", null, "Wikipedia");
		//createLeafNode(level,"pubPlace", null, "URL:http://"+lang+".wikipedia.org");
		level--; createIndent(level);	
		eventWriter.add(eventFactory.createEndElement("","","imprint"));
		eventWriter.add(newline);
		
		level--; createIndent(level);		
		eventWriter.add(eventFactory.createEndElement("","","monogr"));
		eventWriter.add(newline);
		
		level--; createIndent(level);		
		eventWriter.add(eventFactory.createEndElement("","","biblStruct"));
		eventWriter.add(newline);
			
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","sourceDesc"));
		eventWriter.add(newline);
		
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","fileDesc"));
		eventWriter.add(newline);
		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","encodingDesc"));				
		eventWriter.add(newline);
		
		level++; createIndent(level);		
		eventWriter.add(eventFactory.createStartElement("","","editorialDecl"));				
		eventWriter.add(newline);
		level++;
		createLeafNode(level,"conformance", null, "This document conforms to I5 (see" +
				" http://jtei.revues.org/508)");
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","editorialDecl"));
		eventWriter.add(newline);
		
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","encodingDesc"));
		eventWriter.add(newline);
		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","profileDesc"));				
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","langUsage"));				
		eventWriter.add(newline);
		
		level++; createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","language"));				
		eventWriter.add(eventFactory.createAttribute("id", lang));
		eventWriter.add(eventFactory.createAttribute("usage", "100"));		
		eventWriter.add(eventFactory.createCharacters(selectLanguage(lang)));
		eventWriter.add(eventFactory.createEndElement("","","language"));
		eventWriter.add(newline);
		
		level--; createIndent(level); 
		eventWriter.add(eventFactory.createEndElement("","","langUsage"));
		eventWriter.add(newline);	
		
		createIndent(level);
		eventWriter.add(eventFactory.createStartElement("","","textDesc"));				
		eventWriter.add(newline);
		
		level++;
		createLeafNode(level, "textType", null, textType);
		createLeafNode(level, "textTypeRef", null, "");
		
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","textDesc"));
		eventWriter.add(newline);
		
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","profileDesc"));
		eventWriter.add(newline);		
		
		level--; createIndent(level);
		eventWriter.add(eventFactory.createEndElement("","","idsHeader"));
		eventWriter.add(newline);	
	}
	
	private String selectLanguage(String lang) {		
		if (lang.equals("de")){
			return "Deutsch";			
		}
		else if (lang.equals("fr")){
			return "Französisch";
		}
		else if (lang.equals("hu")){
			return "Ungarisch";			
		}
		else if (lang.equals("it")){
			return "Italienisch";			
		}
		else if (lang.equals("no")){
			return "Norwegisch";			
		}
		else if (lang.equals("pl")){
			return "Polnisch";			
		}
		return "Unbekannt"; 
	}
}

package de.mannheim.ids.wiki;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/** This class defines the transformation and validation procedures.
 * 
 * @author margaretha
 *
 */

public class WikiI5Processor {
	
	private I5ErrorHandler errorHandler;	
	
	private String[] indexes = {"A","B","C","D","E","F","G","H","I","J","K","L",
		    "M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
		    "0","1","2","3","4","5","6","7","8","9"};
	
	private Processor processor;
	private Serializer serializer;	
	private XsltTransformer transformer;
	private DocumentBuilder xmlBuilder;
	
	private XPathFactory xPathFactory;
	private XPath xPath;
	private XPathExpression lastId,group;	
	private File tempI5;
	
	private XMLReader reader;
	private I5Corpus corpus;
	
	public WikiI5Processor(I5Corpus corpus, String inflectives) throws I5Exception {
		if (corpus == null){
			throw new IllegalArgumentException("I5Corpus cannot be null.");
		}
		
		this.corpus = corpus;
		this.xPathFactory = XPathFactory.newInstance();
		this.xPath = xPathFactory.newXPath();
		this.processor = new Processor(true);
		this.serializer = new Serializer();
		
		String errorFilename = corpus.getDumpFilename().substring(0,15) + "-"+ 
				corpus.getType(); 
		errorHandler = new I5ErrorHandler(errorFilename);
		// Set temporary i5 file
		tempI5 = new File(corpus.getLang()+"wiki-"+corpus.getType()+"-temp.i5");
				
		setSerializer(corpus.getEncoding());
		setTransformer(inflectives);		
		setXmlBuilder(); // Setting a document builder for reading XML 		
		setXmlReader(); // Setting an XML reader for DTD validation
	}	
	
	private void setSerializer(String encoding){
		serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
		serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
		serializer.setOutputProperty(Serializer.Property.ENCODING, encoding);
	}
	
	private void setTransformer(String inflectives) throws I5Exception{
		
		XsltCompiler compiler = processor.newXsltCompiler();
		compiler.setXsltLanguageVersion("3.0");
		compiler.setURIResolver(new TemplateURIResolver());
		
		XsltExecutable executable;		
		try {			
			InputStream is = this.getClass().getClassLoader().
					getResourceAsStream("Templates.xsl");
			executable = compiler.compile(new StreamSource(is));
		} catch (SaxonApiException e) {
			throw new I5Exception("Failed compiling the XSLT Stylesheet.",e);
		}
		
		transformer = executable.load();
		transformer.setDestination(serializer);
		try {
			transformer.setInitialTemplate(new QName("main"));
		} catch (SaxonApiException e) {
			throw new I5Exception("Failed setting the initial template for " +
					"a transformer.",e);
		}
		transformer.setParameter(new QName("type"), 
				new XdmAtomicValue(corpus.getType()));		
		transformer.setParameter(new QName("origfilename"), 
				new XdmAtomicValue(corpus.getDumpFilename()));
		transformer.setParameter(new QName("korpusSigle"), 
				new XdmAtomicValue(corpus.getKorpusSigle()));		
		transformer.setParameter(new QName("lang"), 
				new XdmAtomicValue(corpus.getLang()));
		transformer.setParameter(new QName("pubDay"), 
				new XdmAtomicValue(corpus.getDumpFilename().substring(11,13)));
		transformer.setParameter(new QName("pubMonth"), 
				new XdmAtomicValue(corpus.getDumpFilename().substring(13,15)));
		transformer.setParameter(new QName("pubYear"), 
				new XdmAtomicValue(corpus.getYear()));				
		transformer.setParameter(new QName("inflectives"), 
					new XdmAtomicValue(inflectives));		
	}
	
	private void setXmlBuilder() throws I5Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
			xmlBuilder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new I5Exception("Failed building a document builder.",e);
		}
	}
	
	private void setXmlReader() throws I5Exception{
		
		SAXParserFactory saxfactory = SAXParserFactory.newInstance();		
		saxfactory.setValidating(true);
		saxfactory.setNamespaceAware(true);
		
		try {
			saxfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		} catch (SAXNotRecognizedException | SAXNotSupportedException
				| ParserConfigurationException e) {
			throw new I5Exception("Failed setting the secure processing " +
					"feature to a sax factory.", e);
		}
		
		SAXParser parser = null;		
		try {			
			parser = saxfactory.newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			throw new I5Exception("Failed creating a SAX parser.",e);
		}
		
		try{
			reader = parser.getXMLReader();
		} catch (SAXException e) {
			throw new I5Exception("Failed getting the XML reader from " +
					"a SAX parser.",e);			
		}		
		//reader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		reader.setErrorHandler(errorHandler);
	}
	
	public void run(String xmlFolder, String index, I5Writer i5Writer) throws  I5Exception, 
		XPathExpressionException{
		
		if (xmlFolder == null || xmlFolder.isEmpty()){
			throw new IllegalArgumentException("Xmlfolder cannot be null or empty.");
		}
		if (index == null || index.isEmpty()){
			throw new IllegalArgumentException("Index cannot be null or empty.");
		}	
		
		if (i5Writer == null){
			throw new IllegalArgumentException("I5 writer cannot be null.");
		}
		
		String type = corpus.getType();
		Document wikiPageIndexes;
		try {
			wikiPageIndexes = xmlBuilder.parse(index);
		} catch (SAXException | IOException e) {
			throw new I5Exception(e);
		}
		
		// Sort by index
		for (String idx :indexes){
			lastId = xPath.compile(type+"/index[@value='"+idx+"']/id[last()]");
			int n = (int)(double) lastId.evaluate(wikiPageIndexes, XPathConstants.NUMBER);
			if (n<1) continue; 
			
			// Group docs per 100000
			for (int i=0; i < n/100000+1; i++){
				int docNr = i;				
				String docSigle = idx + String.format("%02d",docNr) ;
				System.out.println("DocId "+docSigle);				
				
				group = xPath.compile(type+"/index[@value='"+idx+"']/id[xs:integer" +
						"(xs:integer(.) div 100000) = "+docNr+"]");
				NodeList pagegroup = (NodeList) group.evaluate(wikiPageIndexes,
						XPathConstants.NODESET);
				
				if (pagegroup.getLength()<1) {continue;}
				
				i5Writer.createIdsDocStartElement(createDocId(idx, docSigle));
				String docTitle = i5Writer.createIdsDocTitle(type, idx, docNr);
				i5Writer.createIdsDocHeader(corpus.getKorpusSigle()+"/"+docSigle, docTitle);		
								
				// Do transformation and validation for each page in the group
				for (int j = 0; j < pagegroup.getLength(); j++) {					
					String xmlPath= idx+"/"+pagegroup.item(j).getTextContent()+".xml";
					System.out.println(xmlPath);	
										
					errorHandler.reset();
					
					// Do XSLT transformation
					transform(idx,new File(xmlFolder+"/"+xmlPath));
					if (!errorHandler.isValid()) {	
						errorHandler.write(xmlPath);
						continue;
					}
					
					// Validate the resulting i5 file
					validate(tempI5);
					if (!errorHandler.isValid()) {											
						errorHandler.write(xmlPath);
						continue;												
					}
					
					// read and copy the i5 content to the corpus file
					try {
						i5Writer.readIdsText(tempI5);
					} catch (IOException | XMLStreamException e) {
						throw new I5Exception("Error reading and copying temp content " +
								"to the I5 corpus.", e);
					}					
				}				
				i5Writer.createIdsDocEndElement();						
			}		
		}
		errorHandler.close();
	}
	
	private String createDocId(String index,String docSigle) {
		try{
			Integer.parseInt(index);
			return "_"+docSigle;
		}
		catch (Exception e) {
			return docSigle;
		}		
	}

	private void transform(String index,File xml) {		
		serializer.setOutputFile(tempI5);		
		try {			
			XdmNode source = processor.newDocumentBuilder().build(xml);			
			transformer.setInitialContextNode(source);
			transformer.setParameter(new QName("letter"), new XdmAtomicValue(index));
			transformer.transform();
		} catch (Exception e) {			
			errorHandler.setValid(false);
			errorHandler.setErrorMessage("Transformation error.");
		}
	} 
	
	private void validate(File i5) throws I5Exception {
		try {			
			reader.parse(i5.getName());			
		} 
		catch (SAXException e) {
			errorHandler.setValid(false);
			throw new I5Exception("Found an invalid I5 of a Wikipage", e);
		}
		catch (IOException e) {
			errorHandler.setValid(false);
			throw new I5Exception(e);
		}
	}
	
}

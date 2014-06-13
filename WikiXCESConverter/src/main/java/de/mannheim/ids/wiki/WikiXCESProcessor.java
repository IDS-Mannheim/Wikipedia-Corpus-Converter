package de.mannheim.ids.wiki;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/** This class defines the transformation and validation procedures.
 * 
 * @author margaretha
 *
 */

public class WikiXCESProcessor {
	
	private XCESErrorHandler errorHandler;	
	
	private String[] indexList = {"A","B","C","D","E","F","G","H","I","J","K","L",
		    "M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
		    "0","1","2","3","4","5","6","7","8","9"};
	
	private Processor processor = new Processor(true);
	private Serializer serializer = new Serializer();	
	private XsltTransformer transformer;
	private DocumentBuilder xmlBuilder;
	
	private XPathFactory xPathFactory = XPathFactory.newInstance();
	private XPath xPath = xPathFactory.newXPath();
	private XPathExpression lastId,group;	
	private File xces;
	
	public String lang,korpusSigle,corpusTitle,textType,xmlFolder;
	private XMLReader reader;
	
	public WikiXCESProcessor(String xmlFolder, File xsl,String type, String dumpFilename, 
			String inflectives,String encoding) throws Exception {
		
		this.xmlFolder=xmlFolder;		
		String origfilename = dumpFilename.substring(0,15);	//dewiki-20130728 
		String year = dumpFilename.substring(7,11);
		lang = dumpFilename.substring(0,2);
		korpusSigle = createKorpusSigle(type, lang.substring(0,1).toUpperCase(), dumpFilename.substring(9,11));
		corpusTitle = createCorpusTitle(type,lang,year);
		textType = createTextType(type);
		
		//Setup XSLT serializer and compiler
		serializer.setOutputProperty(Serializer.Property.METHOD, "xml");
		serializer.setOutputProperty(Serializer.Property.INDENT, "yes");
		serializer.setOutputProperty(Serializer.Property.ENCODING, encoding);
		
		XsltCompiler compiler = processor.newXsltCompiler();
		compiler.setXsltLanguageVersion("3.0");
		XsltExecutable executable = compiler.compile(new StreamSource(xsl));		
		// Setup transformer
		transformer = executable.load();
		transformer.setDestination(serializer);
		transformer.setInitialTemplate(new QName("main"));
		transformer.setParameter(new QName("type"), new XdmAtomicValue(type));		
		transformer.setParameter(new QName("origfilename"), new XdmAtomicValue(origfilename));
		transformer.setParameter(new QName("korpusSigle"), new XdmAtomicValue(korpusSigle));		
		transformer.setParameter(new QName("lang"), new XdmAtomicValue(lang));
		transformer.setParameter(new QName("pubDay"), new XdmAtomicValue(dumpFilename.substring(11,13)));
		transformer.setParameter(new QName("pubMonth"), new XdmAtomicValue(dumpFilename.substring(13,15)));
		transformer.setParameter(new QName("pubYear"), new XdmAtomicValue(year));
		
		if (inflectives !=null)
			transformer.setParameter(new QName("inflectives"), new XdmAtomicValue("../"+inflectives));

		// Setup temporary xces file
		xces = new File(lang+"wiki-"+type+"-temp.xces");
		errorHandler = new XCESErrorHandler(type,origfilename);
		// Setup documentbuilder for reading xml
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		xmlBuilder = builderFactory.newDocumentBuilder();
		
		// Setup saxparser for DTD validation
		SAXParserFactory saxfactory = SAXParserFactory.newInstance();		
		saxfactory.setValidating(true);
		saxfactory.setNamespaceAware(true);
		saxfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		SAXParser parser = saxfactory.newSAXParser();		
		reader = parser.getXMLReader();
		//reader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		reader.setErrorHandler(errorHandler);
	}	
	
	public void run(String path, String type, XCESWriter xcesWriter) throws 
		SAXException, IOException, XPathExpressionException, XMLStreamException {
				
		Document articleList = xmlBuilder.parse(path);				
		
		// Sort by index
		for (String index :indexList){
			lastId = xPath.compile(type+"/index[@value='"+index+"']/id[last()]");
			int n = (int)(double) lastId.evaluate(articleList, XPathConstants.NUMBER);
			if (n<1) continue; 
			
			// Group docs per 100000
			for (int i=0; i < n/100000+1; i++){
				int docNr = i;				
				String docSigle = index + String.format("%02d",docNr) ;
				System.out.println("DocId "+docSigle);				
				
				group = xPath.compile(type+"/index[@value='"+index+"']/id[xs:integer(xs:integer(.) div 100000) = "+docNr+"]");
				NodeList pagegroup = (NodeList) group.evaluate(articleList,XPathConstants.NODESET);
				
				if (pagegroup.getLength()<1) {continue;}
				
				xcesWriter.createIdsDocStartElement(createDocId(index, docSigle));
				String docTitle = xcesWriter.createIdsDocTitle(type, index, docNr);
				xcesWriter.createIdsDocHeader(korpusSigle+"/"+docSigle, docTitle);		
								
				// Do transformation and validation for each page in the group
				for (int j = 0; j < pagegroup.getLength(); j++) {					
					String xmlPath= index+"/"+pagegroup.item(j).getTextContent()+".xml";
					System.out.println(xmlPath);	
					// Do XSLT transformation					
					transform(index,new File(xmlFolder+"/"+type+"/"+xmlPath));
					errorHandler.reset();
					// Validate the resulting xces file
					validate(xces);
					if (!errorHandler.isValid()) {											
						errorHandler.write(xmlPath);
						continue;												
					}
					// read and copy the xces content to the corpus file
					xcesWriter.readIdsText(xces);					
				}				
				xcesWriter.createIdsDocEndElement();						
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
	
	private String createKorpusSigle(String type,String lang,String year) {
		if (type.equals("articles")) {
			return "WP"+lang+year;
		}
		return "WD"+lang+year;
	}
	
	private String createCorpusTitle(String type,String lang,String year) {
		if (type.equals("articles")) {
			return "Wikipedia."+lang+" "+year+" Artikel";
		}
		return "Wikipedia."+lang+" "+year+" Diskussionen";
	}
	
	private String createTextType(String type) {
		if (type.equals("articles")) {
			return "Enzyklopädie";
		}
		return "Diskussionen zu Enzyklopädie-Artikeln";
	}

	private void transform(String index,File xml) {		
		serializer.setOutputFile(xces);		
		try {			
			
			XdmNode source = processor.newDocumentBuilder().build(xml);			
			transformer.setInitialContextNode(source);
			transformer.setParameter(new QName("letter"), new XdmAtomicValue(index));
			transformer.transform();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 
	
	private void validate(File xces) throws IOException {
		try {			
			reader.parse(xces.getName());			
		} catch (Exception e) {
			System.out.println("Invalid");
			e.printStackTrace();			
		}
	}
	
}

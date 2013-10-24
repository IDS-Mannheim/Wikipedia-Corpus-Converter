package de.mannheim.ids.parser;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngine;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngCompiledPage;
import org.sweble.wikitext.engine.output.HtmlRendererCallback;
import org.sweble.wikitext.engine.output.MediaInfo;
import org.sweble.wikitext.engine.utils.DefaultConfigEn;
import org.sweble.wikitext.parser.parser.LinkTargetException;

/** Convert wikitext to XML
 *  
 *  @author margaretha
 *  
 * */
public class Sweble2Parser {
	
	/** Generate an Abstract Syntax Tree representation (AST) representation 
	 *  of a given wikitext using the Sweble Parser 2.0.0-alpha-2-SNAPSHOT version,
	 *  and eventually generates an XML representation using a visitor class.
	 * 
	 * @param wikitext
	 * @param pagetitle
	 * @return wikitext in XML
	 * @throws JAXBException
	 * @throws CompilerException
	 * @throws LinkTargetException
	 * @throws IOException
	 */
	public String parseText(String wikitext, String pagetitle, String language) 
			throws JAXBException, CompilerException, LinkTargetException, IOException {
				
		WikiConfig config = DefaultConfigEn.generate();

		// Instantiate Sweble parser
		WtEngine engine = new WtEngine(config);

		PageTitle pageTitle = PageTitle.make(config, pagetitle);		
		PageId pageId = new PageId(pageTitle, -1);
		// Parse Wikitext into AST
		EngCompiledPage cp = engine.postprocess(pageId, wikitext, null);		
		
		// Render AST to XML		
		String uri = language+".wikipedia.org/wiki/";
		String wikiXML = XMLRenderer.print(new MyRendererCallback(), config, pageTitle, cp.getPage(),uri);
		
		return wikiXML;
	}	
	
	private static final class MyRendererCallback
	implements
		HtmlRendererCallback
		{
		@Override
		public boolean resourceExists(PageTitle target)
		{
			return false;
		}
		
		@Override
		public MediaInfo getMediaInfo(
				String title,
				int width,
				int height) throws Exception
		{
			return null;
		}
	}
}

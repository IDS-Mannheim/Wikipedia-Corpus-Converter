package de.mannheim.ids.wiki;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jxpath.xml.DOMParser;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import de.mannheim.ids.parser.Sweble2Parser;
import de.mannheim.ids.parser.TagSoupParser;
import de.mannheim.ids.util.WikiStatistics;

/** This class implements methods for handling Wikipages including
 *  reading page content, cleaning wikitext (pre-processing), parsing, 
 *  and XML validation. 
 * 
 * @author margaretha
 *
 */
public class WikiPageHandler {

	private static Pattern pattern =  Pattern.compile("<([^!!/a-zA-Z\\s])");
	private static Pattern stylePattern = Pattern.compile("(\\[\\[.+>\\]\\])");
	private static Pattern textPattern = Pattern.compile("<text.*\">");
	
	private TagSoupParser tagSoupParser;
	private Sweble2Parser swebleParser;
	private DOMParser dp;
	
	private boolean textFlag;
	private String language;
	private WikiStatistics wikiStatistics;

	public WikiPageHandler(String language, WikiStatistics wikiStatistics) {
		
		if (language==null || language.isEmpty()){
			throw new IllegalArgumentException("Language cannot be null or empty.");
		}
		if (wikiStatistics == null){
			throw new IllegalArgumentException("WikiStatistics cannot be null.");
		}
		
		tagSoupParser = new TagSoupParser();
		swebleParser = new Sweble2Parser();
		dp = new DOMParser();
		
		this.language=language;
		this.wikiStatistics=wikiStatistics;
	}
	
	public void handlePageContent(WikiPage wikiPage, String strLine, String trimmedStrLine) 
			throws IOException {
		
		if (wikiPage == null){
			throw new IllegalArgumentException("WikiPage cannot be null.");
		}
		
		// Finish collecting text
		if (trimmedStrLine.endsWith("</text>")){ 
			if (trimmedStrLine.startsWith("<text")){ // text starts and ends at the same line
				trimmedStrLine = cleanTextStart(trimmedStrLine);				
			}						
			trimmedStrLine = StringUtils.replaceOnce(trimmedStrLine, "</text>", ""); // remove </text>
			
			wikiPage.wikitext += (trimmedStrLine + "\n").trim(); 
			if (wikiPage.wikitext.equals("")){ // empty text
				wikiPage.setEmpty(true); 
				return; 
			} 
						
			wikiPage.wikitext= parseToXML(wikiPage.wikitext, wikiPage.getPageTitle());
			// To do: if wikitext is empty after parsing?
			wikiPage.pageStructure += "      <text/>\n";
			textFlag=false;
		}
		
		// Continue collecting text
		else if (textFlag){
			wikiPage.wikitext += strLine+"\n";
		}		
		
		else if(trimmedStrLine.startsWith("<text")) {
			// empty text
			if (trimmedStrLine.endsWith("<text/>") || 
					trimmedStrLine.equals("<text xml:space=\"preserve\" />")){ 				
				wikiPage.pageStructure += "        <text lang=\""+language+"\"/>\n";
				wikiPage.wikitext="";
				wikiPage.setEmpty(true);
			}
			else { // start collecting text
				wikiPage.wikitext += cleanTextStart(trimmedStrLine);
				this.textFlag=true;
			}
		}
		else{ // copy page metadata			
			wikiPage.pageStructure += strLine + "\n";
		}	

	}
	
	private String parseToXML(String wikitext, String pagetitle){
		
		wikitext = StringEscapeUtils.unescapeXml(wikitext); // unescape XML tags
		wikitext = cleanPattern(wikitext);		
		
		try{
			// italic and bold are not repaired because they have wiki-mark-ups
			wikitext = tagSoupParser.generate(wikitext,true);
			wikitext = swebleParser.parseText(wikitext.trim(), pagetitle, language);
		}
		catch (Exception e) {
			wikiStatistics.addSwebleErrors();
			wikiStatistics.errorPages.add(pagetitle);
			wikitext="";
		}
		return wikitext;
	}
	
	public static String cleanPattern(String wikitext){		 
		wikitext = StringUtils.replaceEach(wikitext, 
//				new String[] { ":{|" , "<br/>", "<br />"}, 
//				new String[] { "{|" , "&lt;br/&gt;", "&lt;br /&gt;"}); //start table notation	
		new String[] { ":{|" }, 
		new String[] { "{|" }); //start table notation
	
		Matcher matcher = pattern.matcher(wikitext); // space for non-tag			
		wikitext = matcher.replaceAll("&lt; $1");			
		matcher.reset();
		
		matcher = stylePattern.matcher(wikitext); // escape for style containing tag
		StringBuffer sb = new StringBuffer();
        while(matcher.find()){
        	String replace = StringEscapeUtils.escapeHtml(matcher.group(1));
        	replace = Matcher.quoteReplacement(replace);
        	matcher.appendReplacement(sb,replace);
        }
        matcher.appendTail(sb);		        
	    wikitext=sb.toString();    
	    return wikitext;
	}
	
	public static String cleanTextStart(String trimmedStrLine) throws IOException{
		Matcher matcher = textPattern.matcher(trimmedStrLine);		
		return matcher.replaceFirst("")+"\n";		
	}
	
	public void validateXML(WikiPage wikiPage) {
		
		if (wikiPage == null){
			throw new IllegalArgumentException("WikiPage cannot be null.");
		}

		String t="";
		try{ //test XML validity
			 t = "<text>"+wikiPage.wikitext+"</text>";
			dp.parseXML(new ByteArrayInputStream(t.getBytes("utf-8")));				
		}
		catch (Exception e) {			
			wikiStatistics.addParsingErrors();
			wikiStatistics.errorPages.add("DOM "+wikiPage.getPageTitle());
			wikiPage.wikitext="";				
		}
		
		try { 
			wikiPage.pageStructure = tagSoupParser.generate(wikiPage.pageStructure, false);
		} 
		catch (Exception e) { 
			System.err.println("Outer Error: "+wikiPage.getPageTitle());
			wikiStatistics.addPageStructureErrors();
			e.printStackTrace(); 
		}		
	}
}

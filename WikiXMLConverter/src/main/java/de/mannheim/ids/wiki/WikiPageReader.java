package de.mannheim.ids.wiki;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.mannheim.ids.util.LanguageProperties;
import de.mannheim.ids.util.WikiStatistics;

/** This class reads a Wiki page, identify some page metadata, 
 *  such as title, namespace and id, and pass the page content 
 *  to a corresponding handler depends on the type of the Wiki 
 *  page: article or talk page. 
 * 
 * @author margaretha
 *
 */

public class WikiPageReader {

	private WikiPage wikiPage;	
	private LanguageProperties languageProperties;
	private WikiPageHandler wikiPageHandler;
	private WikiTalkHandler wikiTalkHandler;
	private WikiStatistics wikiStatistics;
		
	private Pattern titlePattern = Pattern.compile("<title>(.+)</title>");
	private Pattern nsPattern = Pattern.compile("<ns>(.+)</ns>");
	private Pattern idPattern = Pattern.compile("<id>(.+)</id>");
	
	public WikiPageReader(LanguageProperties languageProperties,WikiStatistics wikiStatistics) throws IOException {		
		this.languageProperties = languageProperties;
		this.wikiStatistics = wikiStatistics;
		
		this.wikiPageHandler = new WikiPageHandler(languageProperties.getLanguage(),wikiStatistics);
		this.wikiTalkHandler = new WikiTalkHandler(languageProperties.getLanguage(), 
				languageProperties.getUser(), languageProperties.getContribution(),wikiStatistics);
	}
	
	public void read(String inputFile, WikiXMLWriter wikiXMLWriter) throws IOException {
		
		FileInputStream fs = new FileInputStream(inputFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		
		Matcher matcher;
		String strLine, trimmedStrLine;
		boolean readFlag = false, isDiscussion=false, idFlag=true;
		
		while ((strLine = br.readLine()) != null)   {					
			trimmedStrLine = strLine.trim();
		
			// Start reading a page
			if (trimmedStrLine.startsWith("<page>")){
				wikiPage = new WikiPage();
				wikiPage.pageStructure = strLine+"\n";

				readFlag = true;
				idFlag=true;
				isDiscussion=false;				
			}
			// End reading a page
			else if (readFlag && trimmedStrLine.endsWith("</page>")){				
				wikiPage.pageStructure += strLine;
				wikiPageHandler.validateXML(wikiPage);
				wikiStatistics.countStatistics(isDiscussion, wikiPage);
				wikiXMLWriter.write(wikiPage, isDiscussion, setIndent(strLine));
				readFlag = false;
			}
			else if(readFlag && !trimmedStrLine.equals("</mediawiki>")){
				// Page title
				if (trimmedStrLine.startsWith("<title>") ){
					matcher = titlePattern.matcher(trimmedStrLine);
					if (matcher.find()){
						wikiPage.setPageTitle(matcher.group(1));
						wikiPage.pageStructure += strLine+"\n";
					}
				}
				// Page namespace
				else if (trimmedStrLine.startsWith("<ns>")){
					matcher = nsPattern.matcher(trimmedStrLine);
					if (matcher.find()){
						String ns = matcher.group(1);
						if (languageProperties.getNamespaces().contains(ns)){
							// Discussion namespace
							if (ns.equals("1")) isDiscussion = true;
							wikiPage.pageStructure += setIndent(strLine)+ trimmedStrLine+"\n";
						}
						else {
							readFlag = false; // Stop reading. Skip this page.
							wikiStatistics.addTotalMetapages();
						}
					}
				}
				// Page id
				else if (trimmedStrLine.startsWith("<id>") && idFlag) {
					matcher = idPattern.matcher(trimmedStrLine);
					if (matcher.find()){
						wikiPage.setPageId(matcher.group(1));					
						wikiPage.setPageIndex(isDiscussion,languageProperties.getTalk());
						wikiPage.pageStructure += strLine + "\n";
						idFlag=false;
					}
				}	
				// Redirect page
				else if (trimmedStrLine.startsWith("<redirect")){
					wikiPage.setRedirect(true);		
					wikiStatistics.countStatistics(isDiscussion, wikiPage);
					readFlag = false;
				}
				
				// Handle Discussion
				else if (isDiscussion){
					wikiTalkHandler.handleDiscussion(wikiPage,strLine, trimmedStrLine);
				}
				// Handle Page Content
				else {
					wikiPageHandler.handlePageContent(wikiPage, strLine, trimmedStrLine);
				}
			}
		}
		wikiTalkHandler.user.closeWriter();
		wikiTalkHandler.time.closeWriter();		
	}
	
	public String setIndent(String strLine){		
		return StringUtils.repeat(" ", strLine.indexOf("<"));				
	}
}

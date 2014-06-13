package de.mannheim.ids.wiki;

import java.io.IOException;

import de.mannheim.ids.util.Utilities;

/** Wikipage Class
 * 
 * @author margaretha
 *
 */
public class WikiPage {

	public String pageStructure;
	public String wikitext;
	private String pageTitle;
	private String pageIndex;
	private String pageId;	
	private boolean isEmpty, isRedirect;	
	
	static String[] indexList = {"A","B","C","D","E","F","G","H","I","J","K","L",
	    "M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
	    "0","1","2","3","4","5","6","7","8","9","Char"};	
	
	public WikiPage() {
		wikitext="";		
	}
	
	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(boolean isDiscussion, String talk) throws IOException {
		int start = talk.length() +1;
		if (isDiscussion){						
			pageIndex = Utilities.normalizeIndex(this.pageTitle.substring(start, start+1), indexList);
		}
		else {
			pageIndex = Utilities.normalizeIndex(this.pageTitle.substring(0,1), indexList);
		}
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}
	
}

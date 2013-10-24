package de.mannheim.ids.util;

import java.util.ArrayList;
import java.util.List;

import de.mannheim.ids.wiki.WikiPage;

/** Collect statistical information about wikipages and 
 *  the errors found in the conversion
 * 
 * @author margaretha
 *
 */
public class WikiStatistics {	
	
	private int swebleErrors;
	private int parsingErrors;
	private int pageStructureErrors;

	private int emptyArticles;
	private int emptyDiscussions;
	private int emptyParsedArticles;
	private int emptyParsedDiscussions;
	
	private int redirectArticles;
	private int redirectDiscussions;
	
	private int totalDiscussions;
	private int totalArticles;
	private int totalMetapages;
	private int totalPostings;
	
	public List<String> errorPages;

	public WikiStatistics() {
		this.swebleErrors=0;
		this.parsingErrors=0;
		this.pageStructureErrors=0;
		this.redirectArticles=0;
		this.redirectDiscussions=0;
		this.emptyArticles=0; 
		this.emptyDiscussions=0;
		this.emptyParsedDiscussions=0; 
		this.emptyParsedArticles=0;		
		this.totalMetapages=0;
		this.totalDiscussions=0;
		this.totalArticles=0;	
		this.totalPostings=0;
		this.errorPages = new ArrayList<String>();
	} 
	
	public int getSwebleErrors() {
		return swebleErrors;
	}

	public void addSwebleErrors() {
		this.swebleErrors ++;
	}

	public int getParsingErrors() {
		return parsingErrors;
	}

	public void addParsingErrors() {
		this.parsingErrors ++;
	}

	public int getPageStructureErrors() {
		return pageStructureErrors;
	}

	public void addPageStructureErrors() {
		this.pageStructureErrors ++;
	}

	public int getTotalDiscussions() {
		return totalDiscussions;
	}

	public void addTotalDiscussions() {
		this.totalDiscussions ++;
	}	
	
	public int getTotalPostings() {
		return totalPostings;
	}

	public void addTotalPostings() {
		this.totalPostings++;
	}

	public int getTotalArticles() {
		return totalArticles;
	}

	public void addTotalArticles() {
		this.totalArticles ++;
	}

	public int getEmptyArticles() {
		return emptyArticles;
	}

	public void addEmptyArticles() {
		this.emptyArticles ++;
	}

	public int getEmptyDiscussions() {
		return emptyDiscussions;
	}

	public void addEmptyDiscussions() {
		this.emptyDiscussions ++;
	}

	public int getEmptyParsedArticles() {
		return emptyParsedArticles;
	}

	public void addEmptyParsedArticles() {
		this.emptyParsedArticles ++;
	}

	public int getEmptyParsedDiscussions() {
		return emptyParsedDiscussions;
	}

	public void addEmptyParsedDiscussions() {
		this.emptyParsedDiscussions ++;
	}
	
	public int getRedirectArticles() {
		return redirectArticles;
	}

	public void addRedirectArticles() {
		this.redirectArticles++;
	}

	public int getRedirectDiscussions() {
		return redirectDiscussions;
	}

	public void addRedirectDiscussions() {
		this.redirectDiscussions++;
	}

	public int getTotalMetapages() {
		return totalMetapages;
	}

	public void addTotalMetapages() {
		this.totalMetapages ++;
	}
	
	public void countStatistics(boolean isDiscussion, WikiPage wikiPage) {
		if (isDiscussion){
			if (!wikiPage.wikitext.isEmpty()){ addTotalDiscussions(); }
			else if (wikiPage.isRedirect()){ addRedirectDiscussions(); }
			else if (wikiPage.isEmpty()){ addEmptyDiscussions(); }
			else { addEmptyParsedDiscussions(); }
		}
		else{
			if (!wikiPage.wikitext.isEmpty()){ addTotalArticles(); }
			else if (wikiPage.isRedirect()){ addRedirectArticles(); }
			else if (wikiPage.isEmpty()){  addEmptyArticles(); }
			else { addEmptyParsedArticles(); }
		}
	}
	
	public void printStatistics(){
		System.out.println("Total non-empty articles "+ this.getTotalArticles());
		System.out.println("Total non-empty discussions "+ this.getTotalDiscussions());
		System.out.println("Total postings "+ this.getTotalPostings());
		System.out.println("Total redirect articles "+ this.getRedirectArticles());
		System.out.println("Total redirect discussions "+ this.getRedirectDiscussions());		
		System.out.println("Total empty articles "+ this.getEmptyArticles());
		System.out.println("Total empty discussions "+ this.getEmptyDiscussions());
		System.out.println("Total empty parsed articles "+ this.getEmptyParsedArticles());
		System.out.println("Total empty parsed discussions "+ this.getEmptyParsedDiscussions());		
		//System.out.println("Total metapages "+ this.getTotalMetapages());
		System.out.println("Total Sweble exceptions "+ this.getSwebleErrors());
		System.out.println("Total XML parsing exceptions "+ this.getParsingErrors());
		System.out.println("Total page structure exceptions "+ this.getPageStructureErrors());
	}

	public List<String> getErrorPages() {
		return errorPages;
	}

	public void setErrorPages(List<String> errorPages) {
		this.errorPages = errorPages;
	}	
}

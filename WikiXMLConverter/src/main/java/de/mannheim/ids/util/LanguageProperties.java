package de.mannheim.ids.util;

import java.util.ArrayList;
import java.util.List;

/** Set the language properties of a wikidump
 * 
 * @author margaretha
 * @version 1.0 Aug 2014
 */
public class LanguageProperties {

	private List<Integer> namespaces = new ArrayList<Integer>();
	private String talk, language, user, contribution;
	
	/** Define language properties for an unsupported language.
	 * 
	 * 	@author margaretha
	 * */
	public LanguageProperties(String language, String talk, String user, 
			String contribution, List<Integer> namespaces){
		
		if (language==null || language.isEmpty()){
			throw new IllegalArgumentException("Language cannot be null or empty.");
		}
		if (talk==null || talk.isEmpty()){
			throw new IllegalArgumentException("Talk cannot be null or empty.");
		}
		if (user==null || user.isEmpty()){
			throw new IllegalArgumentException("User cannot be null or empty.");
		}
		if (contribution==null || contribution.isEmpty()){
			throw new IllegalArgumentException("Contribution cannot be null or empty.");
		}
		if (namespaces == null){
			throw new IllegalArgumentException("Namespaces cannot be null.");
		}
		
		this.language = language;
		this.talk = talk;
		this.user = user;
		this.contribution = contribution;
		this.namespaces = namespaces;	
	}
	
	/** Set the language of the wikidump
	 * 
	 * @param language
	 */	
	public LanguageProperties(String language, List<Integer> namespaces) {
		
		if (language==null || language.isEmpty()){
			throw new IllegalArgumentException("Language cannot be null or empty.");
		}
		if (namespaces == null){
			throw new IllegalArgumentException("Namespaces cannot be null.");
		}
		
		this.setLanguage(language.toLowerCase());
		setLanguageProperties(language);		
		setNamespaces(namespaces);
	}
	
	/** Define language dependent terms used in Wikipedia
	 * 	Supported languages are de (german), fr (french), hu (hungarian), 
	 * 	it (italian) pl (polish), and no (norwegian). Properties of other languages
	 *  must be set manually. Use {@link #LanguageProperties(String, String, String, 
	 *  String, List) LanguageProperties} constructor
	 *  
	 * @param language
	 */
	private void setLanguageProperties(String language){		
		if (language.equals("de")){		
			setTalk("Diskussion");
			setUser("Benutzer");
			setContribution("Spezial:Beiträge");			
		}
		else if (language.equals("fr")){
			setTalk("Discussion");
			setUser("Utilisateur");
			setContribution("Spécial:Contributions");
		}
		else if (language.equals("hu")){
			setTalk("Vita");
			setUser("Szerkesztő");
			setContribution("Speciális:Contributions");
		}
		else if (language.equals("it")){
			setTalk("Discussione");
			setUser("Utente");
			setContribution("Speciale:Contributi");
		}
		else if (language.equals("no")){
			setTalk("Diskusjon");
			setUser("Bruker");
			setContribution("Spesial:Contributions");
		}
		else if (language.equals("pl")){
			setTalk("Dyskusja");
			setUser("Wikipedysta");
			setContribution("Specjalna:Wkład");
		}
		else {
			throw new IllegalArgumentException("Language is not supported.");
		}
	}

	public String getTalk() {
		return talk;
	}

	public void setTalk(String talk) {
		this.talk = talk;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContribution() {
		return contribution;
	}

	public void setContribution(String contribution) {
		this.contribution = contribution;
	}

	public List<Integer> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<Integer> namespaces) {
		this.namespaces = namespaces;
	}
}

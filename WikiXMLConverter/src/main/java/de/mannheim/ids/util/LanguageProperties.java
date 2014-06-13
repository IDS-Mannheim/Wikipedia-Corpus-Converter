package de.mannheim.ids.util;

import java.util.ArrayList;
import java.util.List;

/** Set the language properties of a wikidump
 * 
 * @author margaretha
 * @version 1.0 Build Mar 2013
 */
public class LanguageProperties {

	private List<String> namespaces = new ArrayList<String>();
	private String talk, language, user, contribution;
	
	public LanguageProperties() {}

	/** Set the language of the wikidump
	 * 
	 *  If the language of the input wikidump is defined in the LanguageSetter 
	 * 	class, instantiate the language. Otherwise, create an empty instance 
	 * 	and set its properties.
	 * 
	 * @param language
	 */	
	public LanguageProperties(String language, List<String> namespaces) {		
		this.setLanguage(language.toLowerCase());
		setLanguageProperties(language);		
		setNamespaces(namespaces);	
	}
	
	/** Define language dependent terms used in Wikipedia
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

	public List<String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<String> namespaces) {
		this.namespaces = namespaces;
	}
}

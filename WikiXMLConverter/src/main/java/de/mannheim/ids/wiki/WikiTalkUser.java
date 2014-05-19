package de.mannheim.ids.wiki;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import de.mannheim.ids.util.Utilities;

/** Class implementation for handling posting authors.
 *  Generate a list of authours / users in XML.
 *  
 * @author margaretha
 *
 */

public class WikiTalkUser {

	private Map<String, String> user; // username, userid
	private OutputStreamWriter userWriter;
	private int counter;
	private String userUri;
	
	public WikiTalkUser(String language, String userUri) throws IOException {		
		user = new HashMap<String, String>();
		userWriter = Utilities.createWriter(language+"wiki-talk-user.xml");
		userWriter.append("<listPerson>\n");		
		counter=0;
		this.userUri=userUri;
		getTalkUser("unknown","",false);		
	}
	
	public String getTalkUser(String username, String speaker, boolean sigFlag) throws IOException {
		if (!user.containsKey(username)){
			String userId = generateUserId();
			user.put(username, userId);	
			createUser(username,userId,speaker,sigFlag);			
		}
		return user.get(username);		
	}	
		
	private String generateUserId() {
		String userId = "WU"+String.format("%08d", counter);
		counter++;
		return userId;
	}	
	
	private void createUser(String username,String userId, String speaker, boolean sigFlag) throws IOException{
		userWriter.append("   <person xml:id=\""+userId+"\">\n");
		userWriter.append("      <persName>"+username+"</persName>\n");
		
		if (sigFlag){
			userWriter.append("      <signatureContent>\n");
			userWriter.append("         <ref target=\""+userUri);
			userWriter.append(speaker.replaceAll("\\s", "_")+"\">");
			userWriter.append(username+"</ref>\n");
			userWriter.append("      </signatureContent>\n");
		}
		
		userWriter.append("   </person>\n");		
	}
	
	public void closeWriter() throws IOException{
		userWriter.append("</listPerson>");
		userWriter.close();
	}
	
}

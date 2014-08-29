package de.mannheim.ids.wiki;

import java.io.File;

public class I5Corpus {	
	private String dumpFilename;	
	//private String origfilename;
	private String lang;
	private String year;
	private String type;
	private String korpusSigle;
	private String corpusTitle;
	private String textType;
	private String encoding;
	
	public I5Corpus(String dumpFilename, String type, String encoding) {
		if (dumpFilename == null || dumpFilename.isEmpty()){
			throw new IllegalArgumentException("Wikidump filename cannot be " +
			"null or empty.");
		}
		if (type == null || type.isEmpty()){
			throw new IllegalArgumentException("type cannot be null or empty.");
		}
		if (encoding == null){
			encoding = "UTF-8"; // default encoding
		}
		else this.encoding = encoding.toUpperCase();
		
		this.type = type;
		File f = new File(dumpFilename);		
		this.dumpFilename = f.getName();
		this.lang = this.dumpFilename.substring(0,2);
		this.year = this.dumpFilename.substring(7,11);
		//this.origfilename = dumpFilename;
		
		setCorpusTitle();
		setKorpusSigle();
		setTextType();		
	}
	
	public String getDumpFilename() {
		return dumpFilename;
	}
	
	public void setDumpFilename(String dumpFilename) {
		this.dumpFilename = dumpFilename;
	}

/*	public String getOrigfilename() {
		return origfilename;
	}

	public void setOrigfilename(String origfilename) {
		this.origfilename = origfilename;
	}*/

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getKorpusSigle() {
		return korpusSigle;
	}
	
	public void setKorpusSigle() {		
		String lang = this.lang.substring(0,1).toUpperCase();
		String year = this.year.substring(2,4);
		if (type.equals("articles")) {
			korpusSigle = "WP"+lang+year;
		}
		korpusSigle = "WD"+lang+year;
	}
	
	public String getCorpusTitle() {
		return corpusTitle;
	}
	
	public void setCorpusTitle() {
		if (type.equals("articles")) {
			corpusTitle = "Wikipedia."+lang+" "+year+" Artikel";
		}
		corpusTitle = "Wikipedia."+lang+" "+year+" Diskussionen";
	}
	
	public String getTextType() {
		return textType;
	}
	
	public void setTextType() {
		if (type.equals("articles")) {
			textType =  "Enzyklopädie";
		}
		textType =  "Diskussionen zu Enzyklopädie-Artikeln";
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}

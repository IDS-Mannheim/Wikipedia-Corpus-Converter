package de.mannheim.ids.wiki;

public class I5Exception extends Exception{
	static final long serialVersionUID = 8102512892128262089L;
	
	public I5Exception() {
		super();
	}
	
	public I5Exception(String message){
		super(message);
	}
	
	public I5Exception(String message, Throwable cause) {
        super(message, cause);
    }
	
	public I5Exception(Throwable cause) {
		super(cause);
    }
}

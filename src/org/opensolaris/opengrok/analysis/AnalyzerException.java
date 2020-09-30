package org.opensolaris.opengrok.analysis;

import java.io.IOException;

public class AnalyzerException extends IOException {
	/**
	 * 
	 * exception thrown while analyzing the index
	 * enum indicates the type of exception thrown
	 */
	public enum ANALYZER_EXCEPTION_TYPE {
	    NON_EXISTING_GROUP,
	    EXTENSION_ALREADY_EXISTS,
	    NON_EXISTING_EXTENSION,
	    GROUP_ALREADY_EXISTS;
	}
	
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	private ANALYZER_EXCEPTION_TYPE type;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ANALYZER_EXCEPTION_TYPE getType() {
		return type;
	}

	public void setType(ANALYZER_EXCEPTION_TYPE type) {
		this.type = type;
	}

	public AnalyzerException(ANALYZER_EXCEPTION_TYPE type,String message) {
		      super(message);
		      this.message = message;
		      this.type = type;
	}

}

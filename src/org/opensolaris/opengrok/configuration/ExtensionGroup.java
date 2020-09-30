package org.opensolaris.opengrok.configuration;

import java.util.List;

public class ExtensionGroup {
	/**
	 *   POJO used for mapping custom File groups and
	 *   new extensions for analyzer Mapping
	 */
	
	/**
	 * Name of the extension group for example Grind
	 */
	String name;
	
	/**
	 * list of extensions for the extension group
	 * for example list {s,seq,plx} for group Grind
	 */
	List<String> extList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getExtList() {
		return extList;
	}

	public void setExtList(List<String> extList) {
		this.extList = extList;
	}

	public ExtensionGroup() {
		super();
	}	
	
	

}

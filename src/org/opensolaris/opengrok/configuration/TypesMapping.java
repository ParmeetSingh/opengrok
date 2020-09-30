package org.opensolaris.opengrok.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypesMapping {
	
	/**
	 */
	/**
	 * excludeGroups,customGroups,analyzerMapping used by analyzerGuru for UI configuration
	 * excludeGroups is for extension extension groups to be excluded in the UI
	 */
	List<String> excludeGroups;
	
	/**
	 * customGroups is additional extension groups to be added in the UI
	 */
	List<ExtensionGroup> customGroups;
	
	/**
	 * analyzerMapping is for adding additional extensions to existing extension groups.
	 */
	List<ExtensionGroup> analyzerMapping;
	
	/**
	 * fieldListSet is set used for validating fields in yaml configuration file for UI mapping.
	 */
	private static final String[] fieldList = {"excludeGroups","customGroups","analyzerMapping"};
	public static final Set<String> fieldListSet = new HashSet<>(Arrays.asList(fieldList));
	
	/**
	 * sample YAML file
	 * --- 
		analyzerMapping: 
		  - 
		    extList: 
		      - seq
		    name: perl
		customGroups: 
		  - 
		    extList: 
		      - java
		      - pl
		    name: grind
		excludeGroups:
		  - XMl
		  - Lisp
	 */
	
	public TypesMapping() {
		excludeGroups = new ArrayList<String>();
		customGroups = new ArrayList<ExtensionGroup>();
		analyzerMapping = new ArrayList<ExtensionGroup>();
	}

	public List<String> getExcludeGroups() {
		return excludeGroups;
	}

	public void setExcludeGroups(List<String> excludeGroups) {
		this.excludeGroups = excludeGroups;
	}

	public List<ExtensionGroup> getCustomGroups() {
		return customGroups;
	}

	public void setCustomGroups(List<ExtensionGroup> customGroups) {
		this.customGroups = customGroups;
	}

	public List<ExtensionGroup> getAnalyzerMapping() {
		return analyzerMapping;
	}

	public void setAnalyzerMapping(List<ExtensionGroup> analyzerMapping) {
		this.analyzerMapping = analyzerMapping;
	}
	
}

package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.utility.FileModifiedChecker;

public class AliasesFile extends File {
	private static final long serialVersionUID = -3607791607132062852L;
	private final FileModifiedChecker fileModifiedChecker;
	private List<AliasDefinition> aliasDefinitions;
	private List<String> groupNames;
	
	public AliasesFile(File parent, String child) {
		super(parent, child);
		fileModifiedChecker = new FileModifiedChecker(this);
	}
	
	public AliasDefinition getAlias(AliasName aliasName, String scenarioName) {
		AliasDefinition aliasDefinition = null;
		
		for(AliasDefinition nextAliasDefinition : aliasDefinitions()) {
			if(nextAliasDefinition.getScenario().equals(scenarioName) && nextAliasDefinition.getName().equals(aliasName.getName())) {
				aliasDefinition = nextAliasDefinition;
				break;
			}
		}
		
		return aliasDefinition;
	}
	
	public List<String> groupNames() {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return groupNames;
	}
	
	public List<AliasDefinition> aliasDefinitions() {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return aliasDefinitions;
	}
	
	private void reparseFile() {
		aliasDefinitions = new ArrayList<>();
		groupNames = new ArrayList<>();
		
		// TODO: bring aliasing code over from the 'bundlers' project
	}
}

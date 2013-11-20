package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.utility.FileModifiedChecker;

public class AliasDefinitionsFile extends File {
	private static final long serialVersionUID = 822434477840572747L;
	private final FileModifiedChecker fileModifiedChecker;
	private List<AliasDefinition> aliasDefinitions;
	
	public AliasDefinitionsFile(File parent, String child) {
		super(parent, child);
		fileModifiedChecker = new FileModifiedChecker(this);
	}
	
	public AliasDefinition getAlias(AliasName aliasName, String scenarioName, List<String> groupNames) {
		AliasDefinition aliasDefinition = null;
		
		for(AliasDefinition nextAliasDefinition : aliasDefinitions()) {
			String groupName = nextAliasDefinition.getGroup();
			
			if(nextAliasDefinition.getScenario().equals(scenarioName) && ((groupName == null) || groupNames.contains(groupName)) && nextAliasDefinition.getName().equals(aliasName.getName())) {
				aliasDefinition = nextAliasDefinition;
				break;
			}
		}
		
		return aliasDefinition;
	}
	
	public List<AliasDefinition> aliasDefinitions() {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return aliasDefinitions;
	}
	
	private void reparseFile() {
		aliasDefinitions = new ArrayList<>();
		
		// TODO: bring aliasing code over from the 'bundlers' project
	}
}

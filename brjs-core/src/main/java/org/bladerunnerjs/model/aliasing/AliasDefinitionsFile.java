package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AliasDefinitionsFile extends File {
	private static final long serialVersionUID = 822434477840572747L;
	
	public AliasDefinitionsFile(File parent, String child) {
		super(parent, child);
	}
	
	public AliasDefinition getAlias(AliasName aliasName, String scenarioName, List<String> groupNames) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<AliasDefinition> aliasDefinitions() {
		// TODO: bring aliasing code over from the 'bundlers' project
		return new ArrayList<>();
	}
}

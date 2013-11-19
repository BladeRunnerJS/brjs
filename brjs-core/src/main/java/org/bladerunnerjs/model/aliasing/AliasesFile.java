package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AliasesFile extends File {
	private static final long serialVersionUID = -3607791607132062852L;
	
	public AliasesFile(File parent, String child) {
		super(parent, child);
	}
	
	public List<AliasDefinition> aliasDefinitions() {
		// TODO: bring aliasing code over from the 'bundlers' project
		return new ArrayList<>();
	}
}

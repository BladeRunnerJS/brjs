package org.bladerunnerjs.model.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AliasDefinition;

public class AliasDefinitionsFile extends File {
	
	private static final long serialVersionUID = 822434477840572747L;

	public AliasDefinitionsFile(File parent, String child) {
		super(parent, child);
	}
	
	public List<AliasDefinition> aliasDefinitions() {
		// TODO: bring aliasing code over from the 'bundlers' project
		return new ArrayList<>();
	}
}

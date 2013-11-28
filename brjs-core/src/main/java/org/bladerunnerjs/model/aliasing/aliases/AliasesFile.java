package org.bladerunnerjs.model.aliasing.aliases;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bladerunnerjs.model.aliasing.AliasOverride;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;

public class AliasesFile {
	private final AliasesData data = new AliasesData();
	private final AliasesReader reader;
	private final AliasesWriter writer;
	private final File file;
	private final FileModifiedChecker fileModifiedChecker;
	
	public AliasesFile(File parent, String child) {
		file = new File(parent, child);
		fileModifiedChecker = new FileModifiedChecker(file);
		reader = new AliasesReader(data, file);
		writer = new AliasesWriter(data, file);
	}
	
	public File getUnderlyingFile() {
		return file;
	}
	
	public String scenarioName() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.scenario;
	}
	
	public void setScenarioName(String scenarioName) {
		data.scenario = scenarioName;
	}
	
	public List<String> groupNames() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) {
		data.groupNames = groupNames;
	}
	
	public List<AliasOverride> aliasOverrides() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.aliasOverrides;
	}
	
	public void addAlias(AliasOverride aliasOverride) {
		data.aliasOverrides.add(aliasOverride);
	}
	
	public AliasOverride getAlias(String aliasName) throws BundlerFileProcessingException {
		AliasOverride aliasOverride = null;
		
		for(AliasOverride nextAliasOverride : aliasOverrides()) {
			if(nextAliasOverride.getName().equals(aliasName)) {
				aliasOverride = nextAliasOverride;
				break;
			}
		}
		
		return aliasOverride;
	}
	
	public void write() throws IOException {
		writer.write();
	}
}

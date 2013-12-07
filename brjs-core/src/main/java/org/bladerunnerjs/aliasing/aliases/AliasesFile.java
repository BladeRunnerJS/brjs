package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.utility.FileModifiedChecker;

public class AliasesFile {
	private final AliasesData data = new AliasesData();
	private final AliasesReader reader;
	private final AliasesWriter writer;
	private final File file;
	private final FileModifiedChecker fileModifiedChecker;
	private BundlableNode bundlableNode;
	
	public AliasesFile(File parent, String child, BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
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
	
	public AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException {
		AliasOverride aliasOverride = getLocalAliasOverride(aliasName);
		AliasDefinition aliasDefinition = getAliasDefinition(aliasName);
		AliasOverride groupAliasOverride = getGroupAliasOverride(aliasName);
		
		if((aliasOverride == null) && (groupAliasOverride != null)) {
			aliasOverride = groupAliasOverride;
		}
		
		if((aliasDefinition == null) && (aliasOverride == null)) {
			throw new UnresolvableAliasException(this, aliasName);
		}
		
		if(aliasDefinition == null) {
			aliasDefinition = new AliasDefinition(aliasOverride.getName(), aliasOverride.getClassName(), null);
		}
		else if(aliasOverride != null) {
			aliasDefinition = new AliasDefinition(aliasOverride.getName(), aliasOverride.getClassName(), aliasDefinition.getInterfaceName());
		}
		
		return aliasDefinition;
	}
	
	public void write() throws IOException {
		writer.write();
	}
	
	private AliasOverride getLocalAliasOverride(String aliasName) throws BundlerFileProcessingException {
		AliasOverride aliasOverride = null;
		
		for(AliasOverride nextAliasOverride : aliasOverrides()) {
			if(nextAliasOverride.getName().equals(aliasName)) {
				aliasOverride = nextAliasOverride;
				break;
			}
		}
		
		return aliasOverride;
	}
	
	private AliasOverride getGroupAliasOverride(String aliasName) throws BundlerFileProcessingException, AmbiguousAliasException {
		AliasOverride aliasOverride = null;
		List<String> groupNames = groupNames();
		
		for(AliasDefinitionsFile aliasDefinitionsFile : bundlableNode.getAliasDefinitionFiles()) {
			AliasOverride nextAliasOverride = aliasDefinitionsFile.getGroupOverride(aliasName, groupNames);
			
			
			
			if(aliasOverride != null && nextAliasOverride != null) {
				throw new AmbiguousAliasException(getUnderlyingFile(), aliasName, groupNames);
			}
			
			if (nextAliasOverride != null)
			{
				aliasOverride = nextAliasOverride;
			}

		}
		
		return aliasOverride;
	}
	
	private AliasDefinition getAliasDefinition(String aliasName) throws BundlerFileProcessingException, AmbiguousAliasException {
		AliasDefinition aliasDefinition = null;
		String scenarioName = scenarioName();
		List<String> groupNames = groupNames();
		
		for(AliasDefinitionsFile aliasDefinitionsFile : bundlableNode.getAliasDefinitionFiles()) {
			AliasDefinition nextAliasDefinition = aliasDefinitionsFile.getAliasDefinition(aliasName, scenarioName, groupNames);

			if (aliasDefinition != null && nextAliasDefinition != null) {
				throw new AmbiguousAliasException(getUnderlyingFile(), aliasName, scenarioName);
			}
			
			if (nextAliasDefinition != null)
			{
				aliasDefinition = nextAliasDefinition;
			}
		}
		
		return aliasDefinition;
	}
}

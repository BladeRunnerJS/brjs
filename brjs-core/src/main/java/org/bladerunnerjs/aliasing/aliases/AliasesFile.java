package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.IncompleteAliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.FileModifiedChecker;

public class AliasesFile {
	private final AliasesData data = new AliasesData();
	private final AliasesReader reader;
	private final AliasesWriter writer;
	private final File file;
	private final FileModifiedChecker fileModifiedChecker;
	private BundlableNode bundlableNode;
	
	public AliasesFile(File parent, String child, BundlableNode bundlableNode) {
		try {
			this.bundlableNode = bundlableNode;
			file = new File(parent, child);
			fileModifiedChecker = new FileModifiedChecker(file);
			
			String defaultFileCharacterEncoding = bundlableNode.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			reader = new AliasesReader(data, file, defaultFileCharacterEncoding);
			writer = new AliasesWriter(data, file, defaultFileCharacterEncoding);
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public File getUnderlyingFile() {
		return file;
	}
	
	public String scenarioName() throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.scenario;
	}
	
	public void setScenarioName(String scenarioName) {
		data.scenario = scenarioName;
	}
	
	public List<String> groupNames() throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) {
		data.groupNames = groupNames;
	}
	
	public List<AliasOverride> aliasOverrides() throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.aliasOverrides;
	}
	
	public void addAlias(AliasOverride aliasOverride) {
		data.aliasOverrides.add(aliasOverride);
	}
	
	public AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, IncompleteAliasException, ContentFileProcessingException {
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
		
		if((aliasDefinition.getClassName() == null)) {
			throw new IncompleteAliasException(file, aliasDefinition.getName());
		}
		
		return aliasDefinition;
	}
	
	public boolean hasAlias(String aliasName) throws ContentFileProcessingException {
		boolean hasAlias = true;
		
		try {
			getAlias(aliasName);
		}
		catch (AliasException e) {
			hasAlias = false;
		}
		catch (ContentFileProcessingException e) {
			throw e;
		}
		
		return hasAlias;
	}
	
	public AliasDefinition getAliasDefinition(String aliasName) throws ContentFileProcessingException, AmbiguousAliasException {
		AliasDefinition aliasDefinition = null;
		String scenarioName = scenarioName();
		List<String> groupNames = groupNames();
		
		for(AliasDefinitionsFile aliasDefinitionsFile : bundlableNode.aliasDefinitionFiles()) {
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
	
	public void write() throws IOException {
		writer.write();
	}
	
	private AliasOverride getLocalAliasOverride(String aliasName) throws ContentFileProcessingException {
		AliasOverride aliasOverride = null;
		
		for(AliasOverride nextAliasOverride : aliasOverrides()) {
			if(nextAliasOverride.getName().equals(aliasName)) {
				aliasOverride = nextAliasOverride;
				break;
			}
		}
		
		return aliasOverride;
	}
	
	private AliasOverride getGroupAliasOverride(String aliasName) throws ContentFileProcessingException, AmbiguousAliasException {
		AliasOverride aliasOverride = null;
		List<String> groupNames = groupNames();
		
		for(AliasDefinitionsFile aliasDefinitionsFile : bundlableNode.aliasDefinitionFiles()) {
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
}

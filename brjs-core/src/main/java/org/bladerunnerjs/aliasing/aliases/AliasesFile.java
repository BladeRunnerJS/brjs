package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.FileModifiedChecker;

public class AliasesFile {
	public static final String BR_UNKNOWN_CLASS_NAME = "br.UnknownClass";
	
	private final AliasesData data = new AliasesData();
	private final File file;
	private final FileModifiedChecker fileModifiedChecker;
	private BundlableNode bundlableNode;

	private final String defaultFileCharacterEncoding;
	
	public AliasesFile(File parent, String child, BundlableNode bundlableNode) {
		try {
			this.bundlableNode = bundlableNode;
			file = new File(parent, child);
			fileModifiedChecker = new FileModifiedChecker(file);
			
			defaultFileCharacterEncoding = bundlableNode.root().bladerunnerConf().getDefaultFileCharacterEncoding();
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
			AliasesReader.read(data, file, defaultFileCharacterEncoding);
		}
		
		return data.scenario;
	}
	
	public void setScenarioName(String scenarioName) {
		data.scenario = scenarioName;
	}
	
	public List<String> groupNames() throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			AliasesReader.read(data, file, defaultFileCharacterEncoding);
		}
		
		return data.groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) {
		data.groupNames = groupNames;
	}
	
	public List<AliasOverride> aliasOverrides() throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			AliasesReader.read(data, file, defaultFileCharacterEncoding);
		}
		
		return data.aliasOverrides;
	}
	
	public void addAlias(AliasOverride aliasOverride) {
		data.aliasOverrides.add(aliasOverride);
	}
	
	public AliasDefinition getAlias(String aliasName) throws AliasException, ContentFileProcessingException {
		AliasOverride aliasOverride = getLocalAliasOverride(aliasName);
		AliasDefinition aliasDefinition = getAliasDefinition(aliasName);
		AliasOverride groupAliasOverride = getGroupAliasOverride(aliasName);
		
		if ((aliasOverride == null) && (groupAliasOverride != null)) {
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
			aliasDefinition = new AliasDefinition(aliasDefinition.getName(), BR_UNKNOWN_CLASS_NAME, aliasDefinition.getInterfaceName());
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
	
	public AliasDefinition getAliasDefinition(String aliasName) throws ContentFileProcessingException, AliasException {
		AliasDefinition aliasDefinition = null;
		String scenarioName = scenarioName();
		List<String> groupNames = groupNames();
		
		for(AliasDefinitionsFile aliasDefinitionsFile : bundlableNode.aliasDefinitionFiles()) {
			AliasDefinition nextAliasDefinition = aliasDefinitionsFile.getAliasDefinition(aliasName, scenarioName, groupNames);

			if (nextAliasDefinition != null)
			{    			
    			if (aliasDefinition != null && nextAliasDefinition != null) {
    				throw new AmbiguousAliasException(getUnderlyingFile(), aliasName, scenarioName);
    			}
			
				aliasDefinition = nextAliasDefinition;
			}
		}
		
		return aliasDefinition;
	}
	
	public void write() throws IOException {
		AliasesWriter.write(data, file, defaultFileCharacterEncoding);
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

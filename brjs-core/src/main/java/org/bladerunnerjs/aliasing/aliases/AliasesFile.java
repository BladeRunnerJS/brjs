package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

public class AliasesFile {
	public static final String BR_UNKNOWN_CLASS_NAME = "br.UnknownClass";
	
	private final File file;
	private final BundlableNode bundlableNode;
	private final AliasData aliasData;
	
	public AliasesFile(File parent, String child, BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		file = new File(parent, child);
		aliasData = new AliasData(bundlableNode.root(), file);
	}
	
	public File getUnderlyingFile() {
		return file;
	}
	
	public String scenarioName() throws ContentFileProcessingException {
		return aliasData.getScenario();
	}
	
	public void setScenarioName(String scenarioName) throws ContentFileProcessingException {
		aliasData.setScenario(scenarioName);
	}
	
	public List<String> groupNames() throws ContentFileProcessingException {
		return aliasData.getGroupNames();
	}
	
	public void setGroupNames(List<String> groupNames) throws ContentFileProcessingException {
		aliasData.setGroupNames(groupNames);
	}
	
	public List<AliasOverride> aliasOverrides() throws ContentFileProcessingException {
		return aliasData.getAliasOverrides();
	}
	
	public void addAlias(AliasOverride aliasOverride) throws ContentFileProcessingException {
		List<AliasOverride> aliasOverrides = aliasData.getAliasOverrides();
		aliasOverrides.add(aliasOverride);
		aliasData.setAliasOverrides(aliasOverrides);
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
	
	public void write() throws ContentFileProcessingException {
		aliasData.write();
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

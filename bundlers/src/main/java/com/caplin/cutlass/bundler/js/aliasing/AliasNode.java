package com.caplin.cutlass.bundler.js.aliasing;

import java.util.Map;

import org.bladerunnerjs.model.AliasContainer;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.exception.NamespaceException;
import com.caplin.cutlass.structure.ScopeLevel;

public class AliasNode implements AliasingNode {

	private String name;
	private String interfaceName;
	private Map<String, String> scenarios;
	private String errorMessage = "The alias name '%s' must be namespaced with the name of the %s, '%s'";
	private AliasContext context;
	private String className;
	
	
	public AliasNode(String name, String interfaceName, Map<String, String> scenarios, String className) {
		this.name = name;
		this.interfaceName = interfaceName;
		this.scenarios = scenarios;
		this.className = className;
	}

	@Override
	public void register() throws BundlerProcessingException, NamespaceException 
	{
		verifyAliasIsCorrectlyNamespaced();
		
		addAliasesToConatainer(context.getAliasRegistry());
	}
	
	public void addAliasesToConatainer (AliasContainer aliasContainer) throws BundlerProcessingException
	{
		for (String scenarioName : scenarios.keySet())
		{
			String className = scenarios.get(scenarioName);
			AliasDefinition aliasDefinition = new AliasDefinition( this.name, className, this.interfaceName );
			aliasContainer.addClassAlias(aliasDefinition, scenarioName);
		}
	}
		
	@Override
	public void use() throws BundlerProcessingException 
	{
		getInterfaceNameFromRegistry();
		AliasDefinition aliasDefinition = new AliasDefinition( this.name, className, this.interfaceName );
		context.getAliasRegistry().addClassAlias(aliasDefinition);
	}
	
	@Override
	public void setContext(AliasContext context)
	{
		this.context = context;
	}
	
	private void verifyAliasIsCorrectlyNamespaced() throws NamespaceException
	{
		ScopeLevel requestLevel = context.getRequestLevel();
		if( requestLevel == ScopeLevel.BLADESET_SCOPE )
		{
			verifyNamespace("bladeset");
		}
		else if( requestLevel == ScopeLevel.BLADE_SCOPE )
		{
			verifyNamespace("blade");
		}
	}
	
	private void verifyNamespace(String resourceNamespaceName ) throws NamespaceException
	{
		String resourceNamespace = context.getPackageNamespace();
		if( ! name.startsWith( resourceNamespace ) )
		{			
			String message = String.format( errorMessage, name, resourceNamespaceName, resourceNamespace );
			throw new NamespaceException( message );
		}
	}
	//TODO: Replace this with the new AliasRegistry method when it is shown to work.
	private void getInterfaceNameFromRegistry()
	{
		ScenarioAliases scenarioAliases = context.getAliasRegistry().getAliases();
		
		if( scenarioAliases != null ) {
			AliasDefinition aliasDefinitions = scenarioAliases.getAlias( name );
			
			if( aliasDefinitions != null ) {
				interfaceName = aliasDefinitions.getInterfaceName();
			}
		}
	}


}

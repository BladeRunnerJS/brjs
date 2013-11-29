package com.caplin.cutlass.bundler.js.aliasing;

import java.util.List;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.exception.NamespaceException;

public class GroupNode implements AliasingNode {

	private AliasContext context;
	private String groupName;
	private List<AliasNode> aliasNodes;
	
	public GroupNode(String groupName, List<AliasNode> aliasNodes) {
		this.groupName = groupName;
		this.aliasNodes = aliasNodes;
	}

	@Override
	public void register() throws BundlerProcessingException, NamespaceException 
	{
		AliasRegistry aliasRegistry = context.getAliasRegistry();
		
		GroupDefinition groupDefinition = new GroupDefinition(groupName);
		
		for (AliasNode aliasingNode : aliasNodes)
		{
			aliasingNode.addAliasesToConatainer(groupDefinition);
		}
		aliasRegistry.addGroup(groupDefinition);
	}

	@Override
	public void use() throws BundlerProcessingException 
	{
		context.getAliasRegistry().useGroup(groupName);
	}

	@Override
	public void setContext(AliasContext context) 
	{
		this.context = context;
	}

}

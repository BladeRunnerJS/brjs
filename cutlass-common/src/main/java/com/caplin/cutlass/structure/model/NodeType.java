package com.caplin.cutlass.structure.model;

import com.caplin.cutlass.structure.model.node.*;

public enum NodeType {
	
	ROOT (RootNode.class),
	SDK (SdkNode.class),
	LIB (UserLibNode.class),
	APPS_ROOT (AppsRootNode.class),
	APP (AppNode.class),
	THIRDPARTY_LIB (ThirdpartyLibNode.class),
	ASPECT (AspectNode.class),
	BLADESET (BladesetNode.class),
	BLADE (BladeNode.class),
	WORKBENCH (WorkbenchNode.class),
	TEST (TestNode.class),
	DATABASE (DatabaseNode.class),
	CONFIG (ConfigNode.class),
	TEST_RESULTS (TestResultsNode.class),
	TEMP (TempNode.class);
	
	public final Class<? extends Node> nodeClass;
	
	private NodeType(Class<? extends Node> nodeClass)
	{
		this.nodeClass = nodeClass;
	}
	
}
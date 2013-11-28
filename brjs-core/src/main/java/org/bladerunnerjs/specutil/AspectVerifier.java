package org.bladerunnerjs.specutil;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AspectVerifier extends NodeVerifier<Aspect> {
	private Aspect aspect;
	
	public AspectVerifier(SpecTest modelTest, Aspect aspect) {
		super(modelTest, aspect);
		this.aspect = aspect;
	}
	
	public void hasAlias(String aliasName, String classRef, String interfaceRef) throws Exception {
		AliasDefinition alias = aspect.aliasesFile().getAlias(aliasName);
		
		assertEquals("Class not as expected for alias '" + aliasName + "'", classRef, alias.getClassName());
		assertEquals("Interface not as expected for alias '" + aliasName + "'", interfaceRef, alias.getInterfaceName());
	}
	
	public void hasAlias(String aliasName, String classRef) throws Exception {
		hasAlias(aliasName, classRef, null);
	}
}

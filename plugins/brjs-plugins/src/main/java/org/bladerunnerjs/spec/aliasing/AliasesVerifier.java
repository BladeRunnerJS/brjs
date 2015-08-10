package org.bladerunnerjs.spec.aliasing;

import static org.junit.Assert.*;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.SpecTestVerifier;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility;


public class AliasesVerifier implements SpecTestVerifier
{

	private Aspect aspect;
	private VerifierChainer verifierChainer;

	public AliasesVerifier(SpecTest specTest, Aspect aspect) {
		this.aspect = aspect;
		verifierChainer = new VerifierChainer(specTest);
	}
	
	public VerifierChainer hasAlias(String aliasName, String classRef, String interfaceRef) throws Exception {
		AliasDefinition alias = AliasingUtility.aliasesFile(aspect).getAlias(aliasName);
		
		assertEquals("Class not as expected for alias '" + aliasName + "'", classRef, alias.getClassName());
		assertEquals("Interface not as expected for alias '" + aliasName + "'", interfaceRef, alias.getInterfaceName());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasAlias(String aliasName, String classRef) throws Exception {
		hasAlias(aliasName, classRef, null);
		
		return verifierChainer;
	}

}

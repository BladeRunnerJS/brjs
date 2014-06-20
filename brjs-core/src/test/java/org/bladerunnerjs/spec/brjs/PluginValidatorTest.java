package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.model.exception.IncorectContentPathPrefixException;
import org.bladerunnerjs.model.exception.InvalidContentPathException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Ignore;
import org.junit.Test;


public class PluginValidatorTest extends SpecTest
{

	@Test @Ignore
	public void pluginsMustHaveUrlsThatBeginWithTheirRequestPrefix() throws Exception
	{
		given(brjs).hasContentPlugins(new MockContentPluginWithIncorrectRequestPrefix())
			.and(brjs).hasBeenCreated()
			.and( brjs.app("app") ).hasBeenPopulated();
		then(exceptions).verifyException(IncorectContentPathPrefixException.class, 
				MockContentPluginWithIncorrectRequestPrefix.class.getSimpleName(), "some/url/path");
	}
	
	@Test @Ignore
	public void pluginsCannotHaveUrlsThatHaveAFileWhereAnotherUrlExpectsADirectory() throws Exception
	{
		given(brjs).hasContentPlugins(new MockContentPluginWithIncorrectContentPaths())
			.and(brjs).hasBeenCreated()
			.and( brjs.app("app") ).hasBeenPopulated();
		then(exceptions).verifyException(InvalidContentPathException.class, 
				MockContentPluginWithIncorrectRequestPrefix.class.getSimpleName());
	}
	
}

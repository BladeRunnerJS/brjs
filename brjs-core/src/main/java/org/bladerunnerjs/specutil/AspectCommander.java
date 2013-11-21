package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import java.io.StringWriter;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Mode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AspectCommander extends NodeCommander<Aspect> {
	private final Aspect aspect;
	public AspectCommander(SpecTest modelTest, Aspect aspect)
	{
		super(modelTest, aspect);
		this.aspect = aspect;
	}
	
	public CommanderChainer getBundledFiles() {
		fail("the model doesn't yet support bundling!");
		
		return commanderChainer;
	}

	public BundleInfoCommander getBundleInfo() throws Exception {
		
		return new BundleInfoCommander((aspect.getBundleSet()));
	}
	
	public void pageLoadedInDev(StringBuffer page, String locale) throws ConfigException {
		pageLoaded(page, locale, Mode.Dev);
	}

	public void pageLoadedInProd(StringBuffer page, String locale) throws ConfigException {
		pageLoaded(page, locale, Mode.Prod);
	}
	
	private void pageLoaded(StringBuffer page, String locale, Mode opMode) throws ConfigException {
		StringWriter writer = new StringWriter();	
		aspect.writeIndexPage(writer, opMode, locale);
		
		page.append(writer.toString());
	}
}

package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Mode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.IndexPageWriter;
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
	
	public void indexPageLoadedInDev(StringBuffer pageResponse, String locale) throws ConfigException, IOException, ModelOperationException {
		pageLoaded(pageResponse, locale, Mode.Dev);
	}

	public void pageLoadedInProd(StringBuffer pageResponse, String locale) throws ConfigException, IOException, ModelOperationException {
		pageLoaded(pageResponse, locale, Mode.Prod);
	}
	
	private void pageLoaded(StringBuffer pageResponse, String locale, Mode opMode) throws ConfigException, IOException, ModelOperationException {
		StringWriter writer = new StringWriter();	
		
		IndexPageWriter.write(FileUtils.readFileToString(aspect.file("index.html")), aspect.getBundleSet(), writer, opMode, locale);
		
		pageResponse.append(writer.toString());
	}
}

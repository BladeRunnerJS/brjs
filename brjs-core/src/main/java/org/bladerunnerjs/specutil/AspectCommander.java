package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.NoTagHandlerFoundException;
import org.bladerunnerjs.model.utility.TagPluginUtility;
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
	
	public void indexPageLoadedInDev(StringBuffer pageResponse, String locale) throws ConfigException, IOException, ModelOperationException, NoTagHandlerFoundException {
		pageLoaded(pageResponse, locale, RequestMode.Dev);
	}

	public void pageLoadedInProd(StringBuffer pageResponse, String locale) throws ConfigException, IOException, ModelOperationException, NoTagHandlerFoundException {
		pageLoaded(pageResponse, locale, RequestMode.Prod);
	}
	
	private void pageLoaded(StringBuffer pageResponse, String locale, RequestMode opMode) throws ConfigException, IOException, ModelOperationException, NoTagHandlerFoundException {
		StringWriter writer = new StringWriter();	
		
		TagPluginUtility.filterContent(FileUtils.readFileToString(aspect.file("index.html")), aspect.getBundleSet(), writer, opMode, locale);
		
		pageResponse.append(writer.toString());
	}
}

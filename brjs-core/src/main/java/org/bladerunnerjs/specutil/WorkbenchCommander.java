package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.NoTagHandlerFoundException;
import org.bladerunnerjs.model.utility.TagPluginUtility;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.dom4j.DocumentException;

public class WorkbenchCommander extends NodeCommander<Workbench> 
{
	private final Workbench workbench;
	
	public WorkbenchCommander(SpecTest modelTest, Workbench workbench) 
	{
		super(modelTest, workbench);
		this.workbench = workbench;
	}
	
	public CommanderChainer getBundledFiles() 
	{
		fail("the model doesn't yet support bundling!");
		return commanderChainer;
	}
	
	public BundleInfoCommander getBundleInfo() throws Exception 
	{
		return new BundleInfoCommander(workbench.getBundleSet());
	}

	public void pageLoaded(StringBuffer pageResponse, String locale) throws ConfigException, IOException, ModelOperationException, NoTagHandlerFoundException, DocumentException 
	{
		StringWriter writer = new StringWriter();	
		TagPluginUtility.filterContent(FileUtils.readFileToString(workbench.file("index.html")), workbench.getBundleSet(), writer, RequestMode.Dev, locale);
		pageResponse.append(writer.toString());
	}
}

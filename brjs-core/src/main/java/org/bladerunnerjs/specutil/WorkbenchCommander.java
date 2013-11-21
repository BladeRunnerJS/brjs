package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Mode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.IndexPageWriter;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;

public class WorkbenchCommander extends NodeCommander<Workbench> {
	@SuppressWarnings("unused")
	private final Workbench workbench;
	
	public WorkbenchCommander(SpecTest modelTest, Workbench workbench) {
		super(modelTest, workbench);
		this.workbench = workbench;
	}
	
	public CommanderChainer getBundledFiles() {
		fail("the model doesn't yet support bundling!");
		
		return commanderChainer;
	}
	
	public BundleInfoCommander getBundleInfo() throws Exception {
		
		return new BundleInfoCommander(workbench.getBundleSet());
	}

	public void pageLoaded(StringBuffer pageResponse, String locale) throws ConfigException, IOException, ModelOperationException {
		StringWriter writer = new StringWriter();	
		
		IndexPageWriter.write(FileUtils.readFileToString(workbench.file("index.html")), workbench.getBundleSet(), writer, Mode.Dev, locale);
		
		pageResponse.append(writer.toString());
	}
}

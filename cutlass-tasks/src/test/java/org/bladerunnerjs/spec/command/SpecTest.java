package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.testing.specutility.engine.ConsoleStoreWriter;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;

import com.caplin.cutlass.BRJSAccessor;

public class SpecTest extends org.bladerunnerjs.testing.specutility.engine.SpecTest {
	
	// This allows commands which don't use the model to use the spec test format
	@Override
	public BRJS createModel() throws InvalidSdkDirectoryException 
	{	
		return BRJSAccessor.initialize(new BRJS (testSdkDirectory, pluginLocator, new PessimisticFileModificationService(), new TestLoggerFactory(logging), new ConsoleStoreWriter(output)));
	}

}

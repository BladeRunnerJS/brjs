package org.bladerunnerjs.jstestdriver;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.utility.filemodification.OptimisticFileModificationService;
import org.bladerunnerjs.appserver.BRJSThreadSafeModelAccessor;
import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.logging.ConsoleLoggerConfigurator;
import org.bladerunnerjs.logging.LogConfiguration;
import org.slf4j.impl.StaticLoggerBinder;

import com.caplin.cutlass.BRJSAccessor;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

public class BRJSBundleHandler extends AbstractModule
{	
	public BRJSBundleHandler() throws IOException
	{
		BRJS brjs = null;
		try
		{
    		LogConfiguration logConfigurator = new ConsoleLoggerConfigurator(
    			StaticLoggerBinder.getSingleton().getLoggerFactory().getRootLogger());
    		
    		logConfigurator.ammendProfile(LogLevel.INFO)
    			.pkg("com.google.jstestdriver").logsAt(LogLevel.WARN)
    			.pkg("org.mortbay.log").logsAt(LogLevel.WARN)
    			.pkg("brjs.core").logsAt(LogLevel.WARN);
    		logConfigurator.setLogLevel(LogLevel.INFO);
    		
    		brjs = BRJSAccessor.initialize(new BRJS(new File(".").getCanonicalFile(), logConfigurator, new OptimisticFileModificationService()));
    		BRJSThreadSafeModelAccessor.initializeModel(brjs);
		}
		finally
		{
			if (brjs != null) { brjs.close(); };
		}
	}
	
	@Override
	protected void configure()
	{
		Multibinder.newSetBinder(binder(), ResourcePreProcessor.class).addBinding().to(BundlerHandler.class);
	}
}

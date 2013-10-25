package com.caplin.jstestdriver.plugin;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.ConsoleLoggerConfigurator;
import org.bladerunnerjs.core.log.LogConfiguration;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.logger.LogLevel;
import org.slf4j.impl.StaticLoggerBinder;

import com.caplin.cutlass.BRJSAccessor;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

public class CutlassBundleInjectorPlugin extends AbstractModule
{	
	public CutlassBundleInjectorPlugin() throws IOException
	{
		LogConfiguration logConfigurator = new ConsoleLoggerConfigurator(
			StaticLoggerBinder.getSingleton().getLoggerFactory().getRootLogger());
		
		logConfigurator.ammendProfile(LogLevel.INFO)
			.pkg("com.google.jstestdriver").logsAt(LogLevel.WARN)
			.pkg("org.mortbay.log").logsAt(LogLevel.WARN)
			.pkg("brjs.core").logsAt(LogLevel.WARN);
		logConfigurator.setLogLevel(LogLevel.INFO);
		
		BRJSAccessor.initialize(new BRJS(new File(".").getCanonicalFile(), logConfigurator));
	}
	
	@Override
	protected void configure()
	{
		Multibinder.newSetBinder(binder(), ResourcePreProcessor.class).addBinding().to(BundlerInjector.class);
	}
}

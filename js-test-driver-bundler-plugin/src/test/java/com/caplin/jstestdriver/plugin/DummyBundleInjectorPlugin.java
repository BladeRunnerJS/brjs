package com.caplin.jstestdriver.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

public class DummyBundleInjectorPlugin extends AbstractModule {

	public DummyBundleInjectorPlugin() {
		
	}
	
	@Override
	protected void configure() {
		
		Multibinder.newSetBinder(binder(), ResourcePreProcessor.class).addBinding().to(DummyBundleInjector.class);
		
	}
	
}

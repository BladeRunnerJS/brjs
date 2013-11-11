package org.bladerunnerjs.specutil;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BladeBuilder extends NodeBuilder<Blade> {
	private Blade blade;
	
	public BladeBuilder(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		this.blade = blade;
	}
	
	public BuilderChainer hasClass(String className) throws Exception {
		FileUtils.write(blade.src().file(className.replaceAll("\\.", "/") + ".js"), "");
		
		return builderChainer;
	}
	
	public BuilderChainer hasClasses(String... classNames) throws Exception {
		for(String className : classNames) {
			hasClass(className);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer classRefersTo(String sourceClass, String destClass) throws Exception {
		FileUtils.write(blade.src().file(sourceClass.replaceAll("\\.", "/") + ".js"), destClass);
		
		return builderChainer;
	}

	public BuilderChainer hasBeenPopulated() throws Exception {
		blade.populate();
		return builderChainer;
	}
}

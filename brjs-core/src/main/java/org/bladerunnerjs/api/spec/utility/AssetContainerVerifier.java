package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.model.AssetContainer;

import com.google.common.base.Joiner;

public class AssetContainerVerifier {
	private AssetContainer assetContainer;
	
	public AssetContainerVerifier(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
	}
	
	public void hasSourceModules(String... expectedSourceModules) throws Exception {
		
		Set<Asset> assets = assetContainer.assets();
		Set<SourceModule> actualSourceModules = new LinkedHashSet<SourceModule>();
		for(Asset asset : assets){
			if(asset instanceof SourceModule){
				actualSourceModules.add((SourceModule)asset);
			}
		}
		assertEquals("Source modules [" + renderSourceModules(actualSourceModules) + "] was expected to contain " + expectedSourceModules.length + " item(s).", expectedSourceModules.length, actualSourceModules.size());
		
		int i = 0;
		for(SourceModule actualSourceModule : actualSourceModules) {
			String expectedSourceModule = expectedSourceModules[i++];
			StringWriter sourceModuleContents = new StringWriter();
			
			assertEquals("Source module " + i + " differs from what's expected.", expectedSourceModule, actualSourceModule.getPrimaryRequirePath());
			try (Reader reader = actualSourceModule.getReader()) { IOUtils.copy(reader, sourceModuleContents); }
		}
	}
	
	private String renderSourceModules(Set<SourceModule> sourceModules) {
		List<String> sourceModulePaths = new ArrayList<>();
		
		for(SourceModule sourceModule : sourceModules) {
			sourceModulePaths.add(sourceModule.getPrimaryRequirePath());
		}
		
		return Joiner.on(", ").join(sourceModulePaths);
	}
	
}

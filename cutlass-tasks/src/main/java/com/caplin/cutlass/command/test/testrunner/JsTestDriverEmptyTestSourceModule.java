package com.caplin.cutlass.command.test.testrunner;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class JsTestDriverEmptyTestSourceModule implements SourceModule {
	private final SourceModule sourceModule;
	 
	public JsTestDriverEmptyTestSourceModule(SourceModule sourceModule) {
		this.sourceModule = sourceModule;
	}
	
	public Reader getReader() throws IOException {
		return new StringReader("");
	}
	
	public AssetLocation assetLocation() {
		return sourceModule.assetLocation();
	}
	
	public boolean isEncapsulatedModule() {
		return sourceModule.isEncapsulatedModule();
	}
	
	public File dir() {
		return sourceModule.dir();
	}
	
	public String getAssetName() {
		return sourceModule.getAssetName();
	}
	
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		return sourceModule.getOrderDependentSourceModules(bundlableNode);
	}
	
	public String getAssetPath() {
		return sourceModule.getAssetPath();
	}
	
	public List<String> getRequirePaths() {
		return sourceModule.getRequirePaths();
	}
	
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return sourceModule.getDependentAssets(bundlableNode);
	}
	
	public String getPrimaryRequirePath() {
		return sourceModule.getPrimaryRequirePath();
	}
	
	public List<String> getAliasNames() throws ModelOperationException {
		return sourceModule.getAliasNames();
	}
	
	public List<AssetLocation> assetLocations() {
		return sourceModule.assetLocations();
	}
}

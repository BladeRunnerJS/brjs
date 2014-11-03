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
	
	@Override
	public Reader getReader() throws IOException {
		return new StringReader("");
	}
	
	@Override
	public AssetLocation assetLocation() {
		return sourceModule.assetLocation();
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return sourceModule.isEncapsulatedModule();
	}
	
	@Override
	public boolean isGlobalisedModule() {
		return sourceModule.isEncapsulatedModule();
	}
	
	@Override
	public File dir() {
		return sourceModule.dir();
	}
	
	@Override
	public String getAssetName() {
		return sourceModule.getAssetName();
	}
	
	@Override
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode);
	}
	
	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return sourceModule.getPostExportDefineTimeDependentAssets(bundlableNode);
	}
	
	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return sourceModule.getUseTimeDependentAssets(bundlableNode);
	}
	
	@Override
	public String getAssetPath() {
		return sourceModule.getAssetPath();
	}
	
	@Override
	public List<String> getRequirePaths() {
		return sourceModule.getRequirePaths();
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return sourceModule.getDependentAssets(bundlableNode);
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return sourceModule.getPrimaryRequirePath();
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return sourceModule.getAliasNames();
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return sourceModule.assetLocations();
	}
}

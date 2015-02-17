package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;

public class JsTestDriverEmptyTestSourceModule implements SourceModule {
	private final SourceModule sourceModule;
	 
	public JsTestDriverEmptyTestSourceModule(SourceModule sourceModule) {
		this.sourceModule = sourceModule;
	}
	
	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
		sourceModule.addImplicitDependencies(implicitDependencies);
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new StringReader("");
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return sourceModule.isEncapsulatedModule();
	}
	
	@Override
	public boolean isGlobalisedModule() {
		return sourceModule.isEncapsulatedModule();
	}
	
	public MemoizedFile file() {
		return sourceModule.file();
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
	public AssetContainer assetContainer()
	{
		return sourceModule.assetContainer();
	}
}

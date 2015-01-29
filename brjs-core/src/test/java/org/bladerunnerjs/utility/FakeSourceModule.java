package org.bladerunnerjs.utility;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.BundlableNode;

public class FakeSourceModule implements SourceModule {
	private String identifier;
	private List<Asset> preExportDependencies = new ArrayList<>();
	private List<Asset> postExportDependencies = new ArrayList<>();

	public FakeSourceModule(String identifier) {
		this.identifier = identifier;
	}
	
	public void dependsOn(SourceModule dependency) {
		postExportDependencies.add(dependency);
	}
	
	public void preDependsOn(SourceModule dependency) {
		preExportDependencies.add(dependency);
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		List<Asset> dependentAssets = new ArrayList<>(preExportDependencies);
		dependentAssets.addAll(postExportDependencies);
		
		return dependentAssets;
	}

	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public Reader getReader() throws IOException {
		return new StringReader(identifier);
	}

	@Override
	public AssetLocation assetLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemoizedFile dir() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAssetName() {
		return identifier;
	}

	@Override
	public String getAssetPath() {
		return identifier;
	}

	@Override
	public List<String> getRequirePaths() {
		List<String> requirePaths = new ArrayList<>();
		requirePaths.add(identifier);
		
		return requirePaths;
	}

	@Override
	public String getPrimaryRequirePath() {
		return identifier;
	}

	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}

	@Override
	public boolean isGlobalisedModule() {
		return false;
	}

	@Override
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return preExportDependencies;
	}

	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return postExportDependencies;
	}

	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<AssetLocation> assetLocations() {
		return Collections.emptyList();
	}
}

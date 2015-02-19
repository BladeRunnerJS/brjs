package org.bladerunnerjs.plugin.plugins.require;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;

import com.Ostermiller.util.ConcatReader;

public class AliasDataSourceModule implements CommonJsSourceModule {
	private final AssetLocation assetLocation;
	private final BundlableNode bundlableNode;
	private final List<String> requirePaths = new ArrayList<>();

	public AliasDataSourceModule(AssetLocation assetLocation, BundlableNode bundlableNode) {
		this.assetLocation = assetLocation;
		this.bundlableNode = bundlableNode;
		requirePaths.add("alias!$data");
	}

	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public Reader getReader() throws IOException {
		return new ConcatReader(new Reader[] {
			new StringReader(String.format(COMMONJS_DEFINE_BLOCK_HEADER, getPrimaryRequirePath())),
			getUnalteredContentReader(),
			new StringReader(COMMONJS_DEFINE_BLOCK_FOOTER)
		});
	}
	
	@Override
	public Reader getUnalteredContentReader() throws IOException {
		try {
			return new StringReader("module.exports = " + AliasingSerializer.createJson(bundlableNode.getBundleSet()) + ";");
		}
		catch (ModelOperationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public AssetLocation assetLocation() {
		return assetLocation;
	}

	@Override
	public MemoizedFile dir() {
		return assetLocation.dir();
	}

	@Override
	public String getAssetName() {
		return getAssetPath();
	}

	@Override
	public String getAssetPath() {
		return getPrimaryRequirePath();
	}

	@Override
	public List<String> getRequirePaths() {
		return requirePaths;
	}

	@Override
	public String getPrimaryRequirePath() {
		return "alias!$data";
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
		return Collections.emptyList();
	}

	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
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

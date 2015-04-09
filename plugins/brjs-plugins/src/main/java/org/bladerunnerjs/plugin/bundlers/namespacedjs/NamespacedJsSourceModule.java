package org.bladerunnerjs.plugin.bundlers.namespacedjs;

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
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedFileAsset;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.TrieBasedDependenciesCalculator;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.RequirePathUtility;

import com.Ostermiller.util.ConcatReader;
import com.google.common.base.Joiner;

public class NamespacedJsSourceModule implements AugmentedContentSourceModule {
	
	private AssetLocation assetLocation;
	private MemoizedFile assetFile;
	private LinkedFileAsset linkedFileAsset;
	private List<String> requirePaths = new ArrayList<>();
	private SourceModulePatch patch;
	private TrieBasedDependenciesCalculator trieBasedUseTimeDependenciesCalculator;
	private TrieBasedDependenciesCalculator trieBasedPreExportDefineTimeDependenciesCalculator;
	private TrieBasedDependenciesCalculator trieBasedPostExportDefineTimeDependenciesCalculator;
	public static final String JS_STYLE = "namespaced-js";
	
	public NamespacedJsSourceModule(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this.assetLocation = assetLocation;
		this.assetFile = assetFile;
		this.linkedFileAsset =  new LinkedFileAsset(assetFile, assetLocation);
		
		String requirePath = assetLocation.requirePrefix() + "/" + assetLocation.dir().getRelativePath(assetFile).replaceAll("\\.js$", "");
		requirePaths.add(requirePath);

		patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getPrimaryRequirePath());
	}
	
	@Override
 	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		List<Asset> dependendAssets = new ArrayList<>();
		dependendAssets.addAll( getPreExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getPostExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getUseTimeDependentAssets(bundlableNode) );
		return dependendAssets;
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		List<String> aliases = new ArrayList<>(getPreExportDefineTimeDependencyCalculator().getAliases());
		aliases.addAll(getPostExportDefineTimeDependencyCalculator().getAliases());
		aliases.addAll(getUseTimeDependencyCalculator().getAliases());
		
		return aliases;
	}
	
	@Override
	public Reader getUnalteredContentReader() throws IOException {
		if (patch.patchAvailable()){
			return new ConcatReader( new Reader[] { linkedFileAsset.getReader(), patch.getReader() });
		} else {
			return linkedFileAsset.getReader();
		}
	}
	
	@Override
	public Reader getReader() throws IOException {
		try {
			List<String> requirePaths = getUseTimeDependencyCalculator().getRequirePaths(SourceModule.class);
			String requireAllInvocation = (requirePaths.size() == 0) ? "" : "\n" + calculateDependenciesRequireDefinition(requirePaths) + "\n";
			List<String> staticRequirePaths = getPreExportDefineTimeDependencyCalculator().getRequirePaths(SourceModule.class);
			String staticRequireAllInvocation = (staticRequirePaths.size() == 0) ? "" : " " + calculateDependenciesRequireDefinition(staticRequirePaths);
			String defineBlockHeader = CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_HEADER.replace("\n", "") + staticRequireAllInvocation + "\n";
			
			Reader[] readers = new Reader[] { 
				new StringReader( String.format(defineBlockHeader, getPrimaryRequirePath()) ), 
				getUnalteredContentReader(),
				new StringReader( "\n" ),
				new StringReader( "module.exports = " + getPrimaryRequirePath().replaceAll("/", ".") + ";" ),
				new StringReader( requireAllInvocation ),
				new StringReader(CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_FOOTER), 
			};
			return new ConcatReader( readers );
		}
		catch (ModelOperationException e) {
			throw new IOException("Unable to create the SourceModule reader", e);
		}
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return RequirePathUtility.getPrimaryRequirePath(this);
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}
	
	@Override
	public boolean isGlobalisedModule() {
		return true;
	}
	
	@Override
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		try {
			 return bundlableNode.getLinkedAssets(assetLocation, getPreExportDefineTimeDependencyCalculator().getRequirePaths());
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		try {
			List<Asset> assets = bundlableNode.getLinkedAssets(assetLocation, getPostExportDefineTimeDependencyCalculator().getRequirePaths());
			assets.addAll(bundlableNode.getLinkedAssets(assetLocation, getUseTimeDependencyCalculator().getRequirePaths()));
			
			return assets;
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		// Note: use-time NamespacedJs dependencies are promoted to post-export define-time since this makes our transpiler much easier to write,
		// and since this also enables our CommonJs singleton-pattern to correctly require all of the dependencies needed for any singletons.
		return Collections.emptyList();
	}
	
	private String calculateDependenciesRequireDefinition(List<String> requirePaths) throws ModelOperationException {
		return (requirePaths.isEmpty()) ? "" : "requireAll(require, ['" + Joiner.on("','").join(requirePaths) + "']);\n";
	}
	
	@Override
	public MemoizedFile dir()
	{
		return linkedFileAsset.dir();
	}
	
	@Override
	public String getAssetName() {
		return linkedFileAsset.getAssetName();
	}
	
	@Override
	public String getAssetPath() {
		return linkedFileAsset.getAssetPath();
	}
	
	@Override
	public AssetLocation assetLocation()
	{
		return assetLocation;
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return AssetLocationUtility.getAllDependentAssetLocations(assetLocation);
	}
	
	private TrieBasedDependenciesCalculator getPreExportDefineTimeDependencyCalculator() {
		if (trieBasedPreExportDefineTimeDependenciesCalculator == null) {
			trieBasedPreExportDefineTimeDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new NamespacedJsPreExportDefineTimeDependenciesReader.Factory(this), assetFile, patch.getPatchFile());
		}
		return trieBasedPreExportDefineTimeDependenciesCalculator;
	}
	
	private TrieBasedDependenciesCalculator getPostExportDefineTimeDependencyCalculator() {
		if (trieBasedPostExportDefineTimeDependenciesCalculator == null) {
			trieBasedPostExportDefineTimeDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new NamespacedJsPostExportDefineTimeDependenciesReader.Factory(this), assetFile, patch.getPatchFile());
		}
		return trieBasedPostExportDefineTimeDependenciesCalculator;
	}
	
	private TrieBasedDependenciesCalculator getUseTimeDependencyCalculator() {
		if (trieBasedUseTimeDependenciesCalculator == null) {
			trieBasedUseTimeDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new NamespacedJsUseTimeDependenciesReader.Factory(this), assetFile, patch.getPatchFile());
		}
		return trieBasedUseTimeDependenciesCalculator;
	}
	
	@Override
	public List<String> getRequirePaths() {
		return requirePaths;
	}
	
}

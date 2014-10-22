package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedFileAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.TrieBasedDependenciesCalculator;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.PrimaryRequirePathUtility;
import org.bladerunnerjs.utility.RelativePathUtility;

import com.Ostermiller.util.ConcatReader;
import com.google.common.base.Joiner;

public class NamespacedJsSourceModule implements AugmentedContentSourceModule {
	
	private AssetLocation assetLocation;
	private File assetFile;
	private LinkedFileAsset linkedFileAsset;
	private List<String> requirePaths = new ArrayList<>();
	private SourceModulePatch patch;
	private TrieBasedDependenciesCalculator trieBasedUseTimeDependenciesCalculator;
	private TrieBasedDependenciesCalculator trieBasedDefineTimeDependenciesCalculator;
	public static final String JS_STYLE = "namespaced-js";
	
	public NamespacedJsSourceModule(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this.assetLocation = assetLocation;
		this.assetFile = assetFile;
		this.linkedFileAsset =  new LinkedFileAsset(assetFile, assetLocation);
		
		String requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.root().getFileInfoAccessor(), assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
		requirePaths.add(requirePath);

		patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getPrimaryRequirePath());
	}
	
	@Override
 	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		List<Asset> dependendAssets = new ArrayList<>();
		dependendAssets.addAll( getPreExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getUseTimeDependentAssets(bundlableNode) );
		return dependendAssets;
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return getUseTimeDependencyCalculator().getAliases();
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
		return PrimaryRequirePathUtility.getPrimaryRequirePath(this);
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
		return Collections.emptyList();
	}
	
	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		try {
			return bundlableNode.getLinkedAssets(assetLocation, getUseTimeDependencyCalculator().getRequirePaths());
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	private String calculateDependenciesRequireDefinition(List<String> requirePaths) throws ModelOperationException {
		return (requirePaths.isEmpty()) ? "" : "requireAll(require, window, ['" + Joiner.on("','").join(requirePaths) + "']);\n";
	}
	
	@Override
	public File dir()
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
	
	
	private TrieBasedDependenciesCalculator getUseTimeDependencyCalculator() {
		if (trieBasedUseTimeDependenciesCalculator == null) {
			trieBasedUseTimeDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new NamespacaedJsUseTimeDependenciesReader.Factory(this), assetFile, patch.getPatchFile());
		}
		return trieBasedUseTimeDependenciesCalculator;
	}
	
	private TrieBasedDependenciesCalculator getPreExportDefineTimeDependencyCalculator() {
		if (trieBasedDefineTimeDependenciesCalculator == null) {
			trieBasedDefineTimeDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new NamespacedJsDefineTimeDependenciesReader.Factory(this), assetFile, patch.getPatchFile());
		}
		return trieBasedDefineTimeDependenciesCalculator;
	}
	
	@Override
	public List<String> getRequirePaths() {
		return requirePaths;
	}
	
}

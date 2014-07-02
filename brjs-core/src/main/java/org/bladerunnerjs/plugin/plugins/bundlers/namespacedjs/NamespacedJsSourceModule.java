package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.bladerunnerjs.utility.reader.factory.JsCommentAndCodeBlockStrippingReaderFactory;
import org.bladerunnerjs.utility.reader.factory.JsCommentStrippingReaderFactory;

import com.Ostermiller.util.ConcatReader;

public class NamespacedJsSourceModule implements AugmentedContentSourceModule {
	
	public static final String STATIC_DEPENDENCIES_BLOCK_START = "requireAll([";
	public static final String STATIC_DEPENDENCIES_BLOCK_END = "]);";
	
	private AssetLocation assetLocation;
	private File assetFile;
	private LinkedFileAsset linkedFileAsset;
	private List<String> requirePaths = new ArrayList<>();
	private SourceModulePatch patch;
	private TrieBasedDependenciesCalculator trieBasedDependenciesCalculator;
	private TrieBasedDependenciesCalculator trieBasedStaticDependenciesCalculator;
	
	public NamespacedJsSourceModule(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this.assetLocation = assetLocation;
		this.assetFile = assetFile;
		this.linkedFileAsset =  new LinkedFileAsset(assetFile, assetLocation);
		
		String requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.root(), assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
		requirePaths.add(requirePath);

		patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getPrimaryRequirePath());
	}
	
	@Override
 	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {		
		try {
			return bundlableNode.getLinkedAssets(assetLocation, getDependencyCalculator().getRequirePaths());
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return getDependencyCalculator().getAliases();
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
		String staticDependenciesRequireDefinition;
		try
		{
			staticDependenciesRequireDefinition = calculateStaticDependenciesRequireDefinition();
			staticDependenciesRequireDefinition = (staticDependenciesRequireDefinition.isEmpty()) ? "" : " "+staticDependenciesRequireDefinition;
		}
		catch (ModelOperationException e)
		{
			throw new IOException("Unable to create the SourceModule reader", e);
		}
		
		String defineBlockHeader = CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_HEADER.replace("\n", "") + staticDependenciesRequireDefinition+"\n";
		
		Reader[] readers = new Reader[] { 
				new StringReader( String.format(defineBlockHeader, getPrimaryRequirePath()) ), 
				getUnalteredContentReader(),
				new StringReader( "\n" ),
				new StringReader( "module.exports = " + getPrimaryRequirePath().replaceAll("/", ".") + ";" ),
				new StringReader(CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_FOOTER), 
		};
		return new ConcatReader( readers );
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
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		
		List<SourceModule> result = new ArrayList<SourceModule>();
		try {
			
			 List<Asset> assets = bundlableNode.getLinkedAssets(assetLocation, getStaticDependencyCalculator().getRequirePaths());
			 for(Asset asset : assets){
				 if(asset instanceof SourceModule)
					 result.add((SourceModule)asset);
			 }
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
		return result;
	}
	
	public String calculateStaticDependenciesRequireDefinition() throws ModelOperationException {
		List<String> staticDependencyRequirePaths = getStaticDependencyCalculator().getRequirePaths(SourceModule.class);
		if (staticDependencyRequirePaths.isEmpty()) {
			return "";
		}
		
		StringBuilder staticDependenciesRequireDefinition = new StringBuilder( STATIC_DEPENDENCIES_BLOCK_START );
		for (String staticDependencyRequirePath : staticDependencyRequirePaths) {
			staticDependenciesRequireDefinition.append( "'"+staticDependencyRequirePath+"'," );
		}
		staticDependenciesRequireDefinition.setLength( +staticDependenciesRequireDefinition.length() - 1 ); // remove the final ',' we added
		staticDependenciesRequireDefinition.append( STATIC_DEPENDENCIES_BLOCK_END );
		
		return staticDependenciesRequireDefinition.toString()+"\n";
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
	
	
	private TrieBasedDependenciesCalculator getDependencyCalculator() {
		if (trieBasedDependenciesCalculator == null) {
			trieBasedDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new JsCommentStrippingReaderFactory(this), assetFile, patch.getPatchFile());
		}
		return trieBasedDependenciesCalculator;
	}
	
	private TrieBasedDependenciesCalculator getStaticDependencyCalculator() {
		if (trieBasedStaticDependenciesCalculator == null) {
			trieBasedStaticDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new JsCommentAndCodeBlockStrippingReaderFactory(this), assetFile, patch.getPatchFile());
		}
		return trieBasedStaticDependenciesCalculator;
	}
	
	@Override
	public List<String> getRequirePaths() {
		return requirePaths;
	}
	
}

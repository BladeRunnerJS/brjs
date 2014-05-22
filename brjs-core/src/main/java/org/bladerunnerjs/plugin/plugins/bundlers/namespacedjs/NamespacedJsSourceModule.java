package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedFileAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.TrieBasedDependenciesCalculator;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.CommonJsSourceModule;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.SourceModuleResolver;
import org.bladerunnerjs.utility.reader.JsCommentAndCodeBlockStrippingReaderFactory;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReaderFactory;

import com.Ostermiller.util.ConcatReader;

public class NamespacedJsSourceModule implements AugmentedContentSourceModule {
	
	public static final String STATIC_DEPENDENCIES_BLOCK_START = "requireAll([";
	public static final String STATIC_DEPENDENCIES_BLOCK_END = "]);";
	
	private LinkedAsset linkedAsset;
	private AssetLocation assetLocation;
	private String requirePath;
	private SourceModulePatch patch;
	private TrieBasedDependenciesCalculator dependencyCalculator;
	private TrieBasedDependenciesCalculator staticDependencyCalculator;
	
	private MemoizedValue<List<AssetLocation>> assetLocationsList;
	private final Map<BundlableNode, SourceModuleResolver> sourceModuleResolvers = new HashMap<>();
	private final Map<BundlableNode, SourceModuleResolver> staticSourceModuleResolvers = new HashMap<>();
	
	public NamespacedJsSourceModule(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this.assetLocation = assetLocation;
		requirePath = assetLocation.requirePrefix() + "/" + RelativePathUtility.get(assetLocation.dir(), assetFile).replaceAll("\\.js$", "");
		linkedAsset = new LinkedFileAsset(assetFile, assetLocation);
		patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getRequirePath());
		dependencyCalculator = new TrieBasedDependenciesCalculator(this, new JsCommentStrippingReaderFactory(this), assetFile, patch.getPatchFile());
		staticDependencyCalculator = new TrieBasedDependenciesCalculator(this, new JsCommentAndCodeBlockStrippingReaderFactory(this), assetFile, patch.getPatchFile());
		assetLocationsList = new MemoizedValue<>("NamespacedJsSourceModule.assetLocations", assetLocation.root(), assetLocation.assetContainer().dir());
	}
	
	@Override
 	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		if(!sourceModuleResolvers.containsKey(bundlableNode)) {
			App app = assetLocation.assetContainer().app();
			
			sourceModuleResolvers.put(bundlableNode, new SourceModuleResolver(bundlableNode, assetLocation, requirePath, app.dir(), app.root().libsDir()));
		}
		SourceModuleResolver sourceModuleResolver = sourceModuleResolvers.get(bundlableNode);
		
		try {
			return sourceModuleResolver.getSourceModules(dependencyCalculator.getRequirePaths());
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return dependencyCalculator.getAliases();
	}
	
	@Override
	public Reader getUnalteredContentReader() throws IOException {
		return new ConcatReader( new Reader[] {
				linkedAsset.getReader(), 
				patch.getReader()
		});
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
		
		String defineBlockHeader = CommonJsSourceModule.NODEJS_DEFINE_BLOCK_HEADER.replace("\n", "") + staticDependenciesRequireDefinition+"\n";
		
		Reader[] readers = new Reader[] { 
				new StringReader( String.format(defineBlockHeader, getRequirePath()) ), 
				getUnalteredContentReader(),
				new StringReader( "\n" ),
				new StringReader( "module.exports = " + getRequirePath().replaceAll("/", ".") + ";" ),
				new StringReader(CommonJsSourceModule.NODEJS_DEFINE_BLOCK_FOOTER), 
		};
		return new ConcatReader( readers );
	}
	
	@Override
	public String getRequirePath() {
		return requirePath;
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		if(!staticSourceModuleResolvers.containsKey(bundlableNode)) {
			App app = assetLocation.assetContainer().app();
			
			staticSourceModuleResolvers.put(bundlableNode, new SourceModuleResolver(bundlableNode, assetLocation, requirePath, app.dir(), app.root().libsDir()));
		}
		SourceModuleResolver staticSourceModuleResolver = staticSourceModuleResolvers.get(bundlableNode);
		
		try {
			return staticSourceModuleResolver.getSourceModules(staticDependencyCalculator.getRequirePaths());
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	public String calculateStaticDependenciesRequireDefinition() throws ModelOperationException {
		List<String> staticDependencyRequirePaths = staticDependencyCalculator.getRequirePaths();
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
		return linkedAsset.dir();
	}
	
	@Override
	public String getAssetName() {
		return linkedAsset.getAssetName();
	}
	
	@Override
	public String getAssetPath() {
		return linkedAsset.getAssetPath();
	}
	
	@Override
	public AssetLocation assetLocation()
	{
		return assetLocation;
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return assetLocationsList.value(() -> {
			return AssetLocationUtility.getAllDependentAssetLocations(assetLocation);
		});
	}
	
}

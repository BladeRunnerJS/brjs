package org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty;

import java.util.ArrayList;
import java.util.List;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.UnhandledAssetContainerException;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class BRJSThirdpartyAssetPlugin extends AbstractAssetPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer) throws UnhandledAssetContainerException 
	{
		
		if((assetContainer instanceof JsLib) && (assetContainer.file("library.manifest").exists())) {
			List<AssetLocation> assetLocations = new ArrayList<>();
			assetLocations.add(new ThirdpartyAssetLocation(assetContainer.root(), assetContainer, assetContainer.dir()));
			return assetLocations;
		}
		
		throw new UnhandledAssetContainerException();
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation)
	{
		try
		{
    		List<SourceModule> sourceModules = new ArrayList<SourceModule>();
    		if (assetLocation.getAssetContainer() instanceof JsLib)
    		{
    			NonBladerunnerJsLibManifest manifest = new NonBladerunnerJsLibManifest(assetLocation);
    			if (manifest.fileExists())
    			{
    				BRJSThirdpartyBundlerSourceModule sourceModule = assetLocation.getAssetContainer().root().createAssetFile(BRJSThirdpartyBundlerSourceModule.class, assetLocation, assetLocation.dir());
    				sourceModule.initManifest(manifest);
    				sourceModules.add( sourceModule );
    			}
    		}
    		return sourceModules;
		}
		catch (ConfigException | AssetFileInstantationException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
	
	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation) {
		return new ArrayList<>();
	}
}

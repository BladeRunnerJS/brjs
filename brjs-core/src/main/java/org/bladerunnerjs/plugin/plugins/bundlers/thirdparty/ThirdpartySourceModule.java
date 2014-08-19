package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ThirdpartyLibManifest;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.PrimaryRequirePathUtility;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

import com.Ostermiller.util.ConcatReader;


public class ThirdpartySourceModule implements SourceModule
{

	private ThirdpartyAssetLocation assetLocation;
	private ThirdpartyLibManifest manifest;
	private String assetPath;
	private SourceModulePatch patch;
	private String defaultFileCharacterEncoding;
	
	public ThirdpartySourceModule(ThirdpartyAssetLocation assetLocation) {
		try {
			this.assetLocation = assetLocation;
			assetPath = RelativePathUtility.get(assetLocation.root(), assetLocation.assetContainer().app().dir(), assetLocation.dir());
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getPrimaryRequirePath());
			manifest = assetLocation.getManifest();
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Reader getReader() throws IOException
	{
		List<Reader> fileReaders = new ArrayList<>();
		List<Reader> jsFileReaders = new ArrayList<>();

		
		try {			
			for(File file : manifest.getJsFiles()) {
				jsFileReaders.add(new UnicodeReader(file, defaultFileCharacterEncoding));
				jsFileReaders.add(new StringReader("\n\n"));
			}
			
			String defineBlockHeader = String.format(CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_HEADER, getPrimaryRequirePath());
			String defineBlockFooter = CommonJsSourceModule.COMMONJS_DEFINE_BLOCK_FOOTER;
			
			if (manifest.getHasUmd())
			{
				fileReaders.add( new StringReader( defineBlockHeader ) );
				fileReaders.addAll(jsFileReaders);
				if (patch.patchAvailable()){
					fileReaders.add(patch.getReader());
				}
				fileReaders.add( new StringReader( defineBlockFooter ) );
			} else {				
				fileReaders.add( new StringReader( defineBlockHeader ) );
				fileReaders.addAll(jsFileReaders);
				if (patch.patchAvailable()){
					fileReaders.add(patch.getReader());
				}
    			fileReaders.add( new StringReader( "module.exports = " + manifest.getExports() ) );
    			fileReaders.add( new StringReader( defineBlockFooter ) );
			}
		}
		catch (ConfigException e)
		{
			throw new RuntimeException(e);
		}
		
		
		return new ConcatReader(fileReaders.toArray(new Reader[]{}));
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
	
	@Override
	public File dir()
	{
		return assetLocation.dir();
	}
	
	@Override
	public String getAssetName() {
		return "";
	}
	
	@Override
	public String getAssetPath() {
		return assetPath;
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException
	{
		Set<Asset> dependentLibs = new LinkedHashSet<Asset>();
		
		try 
		{
			for (String dependentLibName : manifest.getDepends())
			{
				JsLib dependentLib = bundlableNode.app().jsLib(dependentLibName);
				if (!dependentLib.dirExists())
				{
					throw new ConfigException(String.format("Library '%s' depends on the library '%s', which doesn't exist.", dir().getName(), dependentLibName)) ;
				}
				dependentLibs.addAll(dependentLib.linkedAssets());
			}
		}
		catch (ConfigException ex)
		{
			throw new ModelOperationException( ex );
		}
		
		return new ArrayList<Asset>( dependentLibs );
	}

	@Override
	public List<String> getAliasNames() throws ModelOperationException
	{
		return Collections.emptyList();
	}

	@Override
	public String getPrimaryRequirePath()
	{
		return PrimaryRequirePathUtility.getPrimaryRequirePath(this);
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return false;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException
	{
		List<SourceModule> result = new ArrayList<SourceModule>();
		for(Asset dependentAsset : getDependentAssets(bundlableNode)){
			if(dependentAsset instanceof SourceModule){
				result.add((SourceModule)dependentAsset);
			}
		}
		return result;
	}
	
	@Override
	public List<String> getRequirePaths() {
		List<String> requirePaths = new ArrayList<String>();
		requirePaths.add(assetLocation.dir().getName());
		
		return requirePaths;
	}
	
}

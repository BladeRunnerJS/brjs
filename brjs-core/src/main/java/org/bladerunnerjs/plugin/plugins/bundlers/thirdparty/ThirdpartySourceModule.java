package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.AssetLocationUtility;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ThirdpartyLibManifest;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.CommonJsSourceModule;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

import com.Ostermiller.util.ConcatReader;


public class ThirdpartySourceModule implements SourceModule
{

	private AssetLocation assetLocation;
	private ThirdpartyLibManifest manifest;
	private String assetPath;
	private SourceModulePatch patch;
	private String defaultFileCharacterEncoding;
	
	public ThirdpartySourceModule(AssetLocation assetLocation) {
		try {
			this.assetLocation = assetLocation;
			assetPath = RelativePathUtility.get(assetLocation.assetContainer().app().dir(), assetLocation.dir());
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			patch = SourceModulePatch.getPatchForRequirePath(assetLocation, getRequirePath());
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Reader getReader() throws IOException
	{
		List<Reader> fileReaders = new ArrayList<>();
		
		try {
			
			boolean hasPackageJson = assetLocation.assetContainer().file("package.json").isFile();
			boolean shouldDefineLibrary = hasPackageJson && !assetLocation.assetContainer().file(".no-define").isFile();
			
			
			String defineBlockHeader = String.format(CommonJsSourceModule.NODEJS_DEFINE_BLOCK_HEADER, getRequirePath());
			String defineBlockBody = "module.exports = " + manifest.getExports();
			String defineBlockFooter = CommonJsSourceModule.NODEJS_DEFINE_BLOCK_FOOTER;
			String globaliseModuleContent = manifest.getExports() + " = require('" + getRequirePath() + "');\n";
			
			//TODO: once we have proper node lib support remove this block and the 'else' block below
			if (shouldDefineLibrary)
			{
				fileReaders.add( new StringReader( defineBlockHeader ) );
			}
			
			for(File file : manifest.getJsFiles()) {
				fileReaders.add(new UnicodeReader(file, defaultFileCharacterEncoding));
				fileReaders.add(new StringReader("\n\n"));
			}
			
			if (!hasPackageJson)
			{
    			fileReaders.add( new StringReader( defineBlockHeader ) );
    			fileReaders.add( new StringReader( defineBlockBody ) );
    			fileReaders.add( new StringReader( defineBlockFooter ) );
			}
			else if (shouldDefineLibrary)
			{
				fileReaders.add( new StringReader( defineBlockFooter ) );
				
				String sanitizedExports = StringUtils.replaceChars(manifest.getExports(), " ", "");
				if (!sanitizedExports.contains("{}"))
				{
					fileReaders.add( new StringReader( globaliseModuleContent ) );
				}
			}
			
			fileReaders.add(patch.getReader());
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
	
	public void initManifest(ThirdpartyLibManifest manifest)
	{
		if (this.manifest == null)
		{
			this.manifest= manifest;
		}
	}

	@Override
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException
	{
		Set<SourceModule> dependentLibs = new LinkedHashSet<SourceModule>();
		
		try 
		{
			for (String dependentLibName : manifest.getDepends())
			{
				JsLib dependentLib = assetLocation.assetContainer().app().nonBladeRunnerLib(dependentLibName);
				if (!dependentLib.dirExists())
				{
					throw new ConfigException(String.format("Library '%s' depends on the library '%s', which doesn't exist.", dir().getName(), dependentLibName)) ;
				}
				dependentLibs.addAll(dependentLib.sourceModules());
			}
		}
		catch (ConfigException ex)
		{
			throw new ModelOperationException( ex );
		}
		
		return new ArrayList<SourceModule>( dependentLibs );
	}

	@Override
	public List<String> getAliasNames() throws ModelOperationException
	{
		return new ArrayList<String>();
	}

	@Override
	public String getRequirePath()
	{
		return assetLocation.dir().getName();
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		return false;
	}
	
	@Override
	public List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException
	{
		return getDependentSourceModules(bundlableNode);
	}
}

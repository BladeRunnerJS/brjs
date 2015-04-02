package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.model.DirectoryLinkedAsset;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.UnicodeReader;

import com.Ostermiller.util.ConcatReader;


public class ThirdpartySourceModule implements SourceModule, DirectoryLinkedAsset
{

	private ThirdpartyLibManifest manifest;
	private String assetPath;
	private SourceModulePatch patch;
	private String defaultFileCharacterEncoding;
	private AssetContainer assetContainer;
	private String primaryRequirePath;
	private List<Asset> implicitDependencies;
	
	public ThirdpartySourceModule(AssetContainer assetContainer, List<Asset> implicitDependencies) {
		try {
			this.assetContainer = assetContainer;
			assetPath = assetContainer.app().dir().getRelativePath(assetContainer.dir());
			defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			primaryRequirePath = calculateRequirePath(assetContainer);
			patch = SourceModulePatch.getPatchForRequirePath(assetContainer, primaryRequirePath);
			manifest = new ThirdpartyLibManifest(assetContainer);
			this.implicitDependencies = implicitDependencies;
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
		this.implicitDependencies.addAll(implicitDependencies);
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
			
			if (manifest.getCommonjsDefinition())
			{
				fileReaders.add( new StringReader( defineBlockHeader ) );
				fileReaders.addAll(jsFileReaders);
				if (patch.patchAvailable()){
					fileReaders.add(patch.getReader());
				}
				fileReaders.add( new StringReader( defineBlockFooter ) );
			} else {				
				fileReaders.addAll(jsFileReaders);
				if (patch.patchAvailable()){
					fileReaders.add(patch.getReader());
				}
				fileReaders.add( new StringReader( defineBlockHeader ) );
    			fileReaders.add( new StringReader( "module.exports = " + manifest.getExports() + ";" ) );
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
	public MemoizedFile file()
	{
		return assetContainer.dir();
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
		List<Asset> dependendAssets = new ArrayList<>();
		dependendAssets.addAll( getPreExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getPostExportDefineTimeDependentAssets(bundlableNode) );
		dependendAssets.addAll( getUseTimeDependentAssets(bundlableNode) );
		
		List<MemoizedFile> cssFiles;
		try {
			cssFiles = manifest.getCssFiles();
		} catch(ConfigException ex) {
			throw new ModelOperationException(ex);
		}
		
		for (MemoizedFile cssFile : cssFiles) {
			String cssFileRelativePath = StringUtils.substringBeforeLast(assetContainer.dir().getRelativePath(cssFile),".");
			String cssFileRequirePath = "css!"+assetContainer.requirePrefix()+"/"+cssFileRelativePath;
			
			Asset cssAsset = assetContainer.asset(cssFileRequirePath);
			if (cssAsset == null) {
				String appRelativePath = assetContainer.app().dir().getRelativePath(cssFile);
				throw new ModelOperationException("Unable to find CSS asset located at '"+appRelativePath+"' with the require path '"+cssFileRequirePath+"'.");
			}
			dependendAssets.add( cssAsset );
		}
		
		dependendAssets.addAll(implicitDependencies);
		
		return dependendAssets;
	}

	@Override
	public String getPrimaryRequirePath()
	{
		return primaryRequirePath;
	}
	
	public String getGlobalisedName() {
		return file().getName().replace("-", "_");
	}
	
	@Override
	public boolean isEncapsulatedModule() {
		try
		{
			return manifest.getCommonjsDefinition();
		}
		catch (ConfigException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public boolean isGlobalisedModule() {
		return !isEncapsulatedModule();
	}
	
	@Override
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException
	{
		Set<Asset> dependentLibs = new LinkedHashSet<Asset>();
		
		try 
		{
			for (String dependentLibName : manifest.getDepends())
			{
				JsLib dependentLib = bundlableNode.app().jsLib(dependentLibName);
				if (!dependentLib.dirExists())
				{
					throw new ConfigException(String.format("Library '%s' depends on the library '%s', which doesn't exist.", file().getName(), dependentLibName)) ;
				}
				dependentLibs.add(dependentLib.asset(dependentLib.requirePrefix()));
			}
		}
		catch (ConfigException ex)
		{
			throw new ModelOperationException( ex );
		}
		
		return new ArrayList<Asset>( dependentLibs );
	}
	
	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}
	
	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException
	{
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getRequirePaths() {
		return Arrays.asList(primaryRequirePath);
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}
	
	@Override
	public boolean isLogicalAsset()
	{
		return false;
	}
	
	public static String calculateRequirePath(AssetContainer assetContainer) {
		return assetContainer.dir().getName();
	}
	
}

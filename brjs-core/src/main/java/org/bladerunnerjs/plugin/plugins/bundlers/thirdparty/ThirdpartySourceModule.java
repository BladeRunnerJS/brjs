package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SourceModulePatch;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

import com.Ostermiller.util.ConcatReader;


public class ThirdpartySourceModule implements SourceModule
{

	private AssetLocation assetLocation;
	private File dir;
	private NonBladerunnerJsLibManifest manifest;
	private String assetPath;
	private SourceModulePatch patch;
	private String defaultInputEncoding;
	
	@Override
	public void initialize(AssetLocation assetLocation, File dir, String assetName)
	{
		this.assetLocation = assetLocation;
		this.dir = dir;
		assetPath = RelativePathUtility.get(assetLocation.getAssetContainer().getApp().dir(), dir);
		try
		{
			defaultInputEncoding = assetLocation.root().bladerunnerConf().getDefaultInputEncoding();
		}
		catch (ConfigException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException
	{
		List<Reader> readers = new ArrayList<Reader>();
		
		try
		{
			for (File file : manifest.getJsFiles())
			{
				readers.add( new BufferedReader(new UnicodeReader(file, defaultInputEncoding)) );
				readers.add( new StringReader("\n\n") );
			}
		}
		catch (ConfigException | IOException e)
		{
			throw new RuntimeException(e);
		}
		
		readers.add( patch.getReader() );
		
		return new ConcatReader( readers.toArray(new Reader[0]) );
	}

	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}
	
	@Override
	public File dir()
	{
		return dir;
	}
	
	@Override
	public String getAssetName() {
		return "";
	}
	
	@Override
	public String getAssetPath() {
		return assetPath;
	}
	
	public void initManifest(NonBladerunnerJsLibManifest manifest)
	{
		if (this.manifest == null)
		{
			this.manifest= manifest;
		}
	}

	@Override
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException
	{
		Set<SourceModule> dependentLibs = new HashSet<SourceModule>();
		
		try 
		{
			for (String dependentLibName : manifest.getDepends())
			{
				JsLib dependentLib = assetLocation.getAssetContainer().getApp().nonBladeRunnerLib(dependentLibName);
				if (!dependentLib.dirExists())
				{
					throw new ConfigException(String.format("Library '%s' depends on '%s', which doesn't exist.", getAssetName(), dependentLibName)) ;
				}
				dependentLibs.addAll(dependentLib.sourceModules());
			}
		}
		catch (ConfigException ex)
		{
			new ModelOperationException( ex );
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
		return dir.getName();
	}
	
	@Override
	public String getClassname() {
		return null;
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

	@Override
	public void addPatch(SourceModulePatch patch)
	{
		this.patch = patch;
	}
}

package org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileIterator;
import org.bladerunnerjs.utility.RelativePathUtility;


public class BRJSThirdpartySourceModule implements SourceModule
{

	private AssetLocation assetLocation;
	private File dir;
	private NonBladerunnerJsLibManifest manifest;
	private byte[] delimiterBytes;
	private FileIterator fileIterator;
	
	{
		try {
			delimiterBytes = "\n\n".getBytes("UTF-8");
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void initialize(AssetLocation assetLocation, File dir)
	{
		this.assetLocation = assetLocation;
		this.dir = dir;
		fileIterator = assetLocation.root().getFileIterator(dir);
	}
	
	@Override
	public Reader getReader() throws FileNotFoundException
	{
		Set<InputStream> fileFileInputStreams = new LinkedHashSet<InputStream>();
		try
		{
			for (File file : getFilesMatchingFilePaths(manifest.getJs()))
			{
				fileFileInputStreams.add( new FileInputStream(file) );
				fileFileInputStreams.add(new ByteArrayInputStream(delimiterBytes));
			}
		}
		catch (ConfigException ex)
		{
			throw new RuntimeException(ex);
		}
		
		InputStream sequenceReaders = new SequenceInputStream( Collections.enumeration(fileFileInputStreams) );
		return new InputStreamReader( sequenceReaders );
	}

	@Override
	public AssetLocation getAssetLocation()
	{
		return assetLocation;
	}

	@Override
	public File getUnderlyingFile()
	{
		return dir;
	}
	
	@Override
	public String getAssetName() {
		return dir.getName(); // TODO: this seems wrong
	}
	
	@Override
	public String getAssetPath() {
		return dir.getPath(); // TODO: this seems wrong
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
	
	private List<File> getFilesMatchingFilePaths(List<String> matchFilePaths)
	{
		List<File> filesMatching = new ArrayList<File>();
		List<File> files = fileIterator.nestedFiles();
		
		for (String pattern : matchFilePaths)
		{
			for (File f : files)
			{
				String relativePath = RelativePathUtility.get(dir, f);
				if ( Pattern.matches(pattern, relativePath) )
				{
					filesMatching.add(f);
				}
			}
		}
		return filesMatching;
	}
}

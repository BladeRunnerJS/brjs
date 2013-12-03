package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;


public class NonBladerunnerJsLibSourceFile implements SourceFile
{

	private AssetLocation assetLocation;
	private File dir;
	private NonBladerunnerJsLibManifest manifest;
	
	@Override
	public Reader getReader() throws FileNotFoundException
	{
		Set<InputStream> fileFileInputStreams = new LinkedHashSet<InputStream>();
		try
		{
			for (File file : getFilesMatchingFilePaths(manifest.getJs()))
			{
				fileFileInputStreams.add( new FileInputStream(file) );
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
	public void initializeUnderlyingObjects(AssetLocation assetLocation, File dir)
	{
		this.assetLocation = assetLocation;
		this.dir = dir;
	}
	
	public void initManifest(NonBladerunnerJsLibManifest manifest)
	{
		if (this.manifest == null)
		{
			this.manifest= manifest;
		}
	}

	@Override
	public List<SourceFile> getDependentSourceFiles() throws ModelOperationException
	{
		List<SourceFile> dependentLibs = new ArrayList<SourceFile>();
		
		try 
		{
    		for (String dependentLibName : manifest.getDepends())
    		{
    			JsLib dependentLib = assetLocation.getAssetContainer().getApp().nonBladeRunnerLib(dependentLibName);
    			if (!dependentLib.dirExists())
    			{
    				throw new ConfigException(String.format("Library '%s' depends on '%'s, which doesn't exists.")) ;
    			}
    			dependentLibs.addAll(dependentLib.sourceFiles());
    		}
		}
		catch (ConfigException ex)
		{
			new ModelOperationException( ex );
		}
		
		return dependentLibs;
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
	public List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException
	{
		return Arrays.asList();
	}

	
	
	
	private List<File> getFilesMatchingFilePaths(List<String> matchFilePaths)
	{
		List<File> filesMatching = new ArrayList<File>();
		Collection<File> foundFiles = FileUtils.listFiles(assetLocation.dir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File f : foundFiles)
		{
			for (String pattern : matchFilePaths)
			{
				if ( Pattern.matches(pattern, f.getPath()) )
				{
					filesMatching.add(f);
					break;
				}
			}
		}
		return filesMatching;
	}

}

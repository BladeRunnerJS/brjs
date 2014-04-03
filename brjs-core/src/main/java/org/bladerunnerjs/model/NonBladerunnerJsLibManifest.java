package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.FileIterator;
import org.bladerunnerjs.utility.RelativePathUtility;

public class NonBladerunnerJsLibManifest extends ConfFile<YamlNonBladerunnerLibManifest>
{
	private static final String LIBRARY_MANIFEST_FILENAME = "library.manifest";
	
	public static final String commaWithOptionalSpacesSeparator = "[\\s]*,[\\s]*";
	
	private final FileIterator fileIterator;
	private final File assetLocationDir;
	private final AssetLocation assetLocation;
	
	public NonBladerunnerJsLibManifest(AssetLocation assetLocation) throws ConfigException {
		super(assetLocation, YamlNonBladerunnerLibManifest.class, assetLocation.file(LIBRARY_MANIFEST_FILENAME));
		fileIterator = assetLocation.root().getFileIterator(assetLocation.dir());
		assetLocationDir = assetLocation.dir();
		this.assetLocation = assetLocation;
	}
	
	public List<String> getDepends() throws ConfigException
	{
		reloadConfIfChanged();
		return listify(conf.depends);
	}
	
	public List<File> getJsFiles() throws ConfigException {
		return getFilesForConfigPaths(getJs(), ".js");
	}
	
	public List<File> getCssFiles() throws ConfigException {
		return getFilesForConfigPaths(getCss(), ".css");
	}

	public String getExports() throws ConfigException
	{
		reloadConfIfChanged();
		return conf.exports;
	}
	
	
	private List<String> getJs() throws ConfigException
	{
		reloadConfIfChanged();
		return listify(conf.js); // TODO: see if we should also limit to only loading js in the root directory by default too
	}
	
	private List<String> getCss() throws ConfigException
	{
		reloadConfIfChanged();
		return listify(conf.css);
	}	
	
	private List<String> listify(String value)
	{
		if (value != null && !value.equals(""))
		{
			return Arrays.asList(value.split(commaWithOptionalSpacesSeparator));
		}
		return new ArrayList<String>();
	}
	
	private List<File> getFilesForConfigPaths(List<String> configPaths, String fileExtension) throws ConfigException
	{
		if (configPaths.isEmpty())
		{
			return findAllFilesWithExtension(fileExtension, false);
		}
		else if (configPaths.size() == 1)
		{
			String firstConfigPath = configPaths.get(0);
			if (firstConfigPath.equals("*"+fileExtension))
			{
				return findAllFilesWithExtension(fileExtension, false);
			}
			else if (firstConfigPath.equals("**/*"+fileExtension))
			{
				return findAllFilesWithExtension(fileExtension, true);
			}
		}
		return getFilesWithPaths(configPaths);
	}
	
	private List<File> getFilesWithPaths(List<String> filePaths) throws ConfigException
	{
		List<File> foundFiles = new ArrayList<File>();
		List<File> files = fileIterator.nestedFiles();
		
		for (String filePath : filePaths)
		{
			boolean foundMatchingFilePath = false;
			for (File f : files)
			{
				String relativePath = RelativePathUtility.get(assetLocationDir, f);
				if ( relativePath.equals(filePath) )
				{
					foundFiles.add(f);
					foundMatchingFilePath = true;
				}
			}
			if (!foundMatchingFilePath)
			{
				String relativeManifestPath = RelativePathUtility.get(assetLocation.assetContainer().root().dir(), assetLocation.file(LIBRARY_MANIFEST_FILENAME));
				throw new ConfigException("Unable to find the file '" + filePath + "' required in the manifest at '" + relativeManifestPath + "'.");
			}
		}
		return foundFiles;
	}
	
	private List<File> findAllFilesWithExtension(String extension, boolean includeNestedDirs)
	{
		List<File> foundFiles = new ArrayList<File>();
		List<File> files = (includeNestedDirs) ? fileIterator.nestedFiles() : fileIterator.files();
		
		for (File f : files)
		{
			if (f.getName().endsWith(extension))
			{
				foundFiles.add(f);
			}
		}
		return foundFiles;
	}
	
}

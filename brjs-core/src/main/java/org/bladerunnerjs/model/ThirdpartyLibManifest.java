package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.RelativePathUtility;

public class ThirdpartyLibManifest extends ConfFile<ThirdpartyLibYamlManifest>
{
	public static final String LIBRARY_MANIFEST_FILENAME = "thirdparty-lib.manifest";
	
	public static final String commaWithOptionalSpacesSeparator = "[\\s]*,[\\s]*";
	
	private final FileInfo fileInfo;
	private final File assetLocationDir;
	private final AssetLocation assetLocation;
	
	public ThirdpartyLibManifest(AssetLocation assetLocation) throws ConfigException {
		super(assetLocation, ThirdpartyLibYamlManifest.class, assetLocation.file(LIBRARY_MANIFEST_FILENAME));
		fileInfo = assetLocation.root().getFileInfo(assetLocation.dir());
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
		return  Collections.<String>emptyList();
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
		String assetLocationDirPath = assetLocationDir.getAbsolutePath();
		
		for (String filePath : filePaths)
		{
			String fullFilePath = assetLocationDirPath + File.separator + filePath;
			File file = new File(fullFilePath);
			
			if(file.exists()){
				foundFiles.add(file);
			}else{
				String relativeManifestPath = RelativePathUtility.get(assetLocation.root(), assetLocation.assetContainer().root().dir(), assetLocation.file(LIBRARY_MANIFEST_FILENAME));
				throw new ConfigException("Unable to find the file '" + filePath + "' required in the manifest at '" + relativeManifestPath + "'.");
			}
		}
		return foundFiles;
	}
	
	private List<File> findAllFilesWithExtension(String extension, boolean includeNestedDirs)
	{
		List<File> foundFiles = new ArrayList<File>();
		List<File> files = (includeNestedDirs) ? fileInfo.nestedFiles() : fileInfo.filesAndDirs();
		
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

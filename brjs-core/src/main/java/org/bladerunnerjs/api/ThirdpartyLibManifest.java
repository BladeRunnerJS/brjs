package org.bladerunnerjs.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.ThirdpartyLibYamlManifest;

public class ThirdpartyLibManifest extends ConfFile<ThirdpartyLibYamlManifest>
{
	public static final String LIBRARY_MANIFEST_FILENAME = "thirdparty-lib.manifest";
	
	public static final String commaWithOptionalSpacesSeparator = "[\\s]*,[\\s]*";
	
	private AssetContainer assetContainer;
	
	public ThirdpartyLibManifest(AssetContainer assetContainer) throws ConfigException {
		super(assetContainer, ThirdpartyLibYamlManifest.class, assetContainer.file(LIBRARY_MANIFEST_FILENAME));
		this.assetContainer = assetContainer;
	}
	
	public List<String> getDepends() throws ConfigException
	{
		return listify(getConf().depends);
	}
	
	public List<MemoizedFile> getJsFiles() throws ConfigException {
		return getFilesForConfigPaths(getJs(), ".js");
	}
	
	public List<MemoizedFile> getCssFiles() throws ConfigException {
		return getFilesForConfigPaths(getCss(), ".css");
	}

	public String getExports() throws ConfigException
	{
		return StringUtils.replaceChars(getConf().exports, " ", "");
	}
	
	public boolean getCommonjsDefinition() throws ConfigException
	{
		return getConf().commonjsDefinition;
	}
	
	public boolean exists() {
		return confFile.isFile();
	}
	
	
	private List<String> getJs() throws ConfigException
	{
		return listify(getConf().js); // TODO: see if we should also limit to only loading js in the root directory by default too
	}
	
	private List<String> getCss() throws ConfigException
	{
		return listify(getConf().css);
	}	
	
	private List<String> listify(String value)
	{
		if (value != null && !value.equals(""))
		{
			return Arrays.asList(value.split(commaWithOptionalSpacesSeparator));
		}
		return Collections.emptyList();
	}
	
	private List<MemoizedFile> getFilesForConfigPaths(List<String> configPaths, String fileExtension) throws ConfigException
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
	
	private List<MemoizedFile> getFilesWithPaths(List<String> filePaths) throws ConfigException
	{
		List<MemoizedFile> foundFiles = new ArrayList<>();
		String assetLocationDirPath = assetContainer.dir().getAbsolutePath();
		
		for (String filePath : filePaths)
		{
			String fullFilePath = assetLocationDirPath + File.separator + filePath;
			File file = new File(fullFilePath);
			
			if(file.exists()){
				foundFiles.add( assetContainer.root().getMemoizedFile(file) );
			}else{
				String relativeManifestPath = assetContainer.root().dir().getRelativePath(assetContainer.file(LIBRARY_MANIFEST_FILENAME));
				throw new ConfigException("Unable to find the file '" + filePath + "' required in the manifest at '" + relativeManifestPath + "'.");
			}
		}
		return foundFiles;
	}
	
	private List<MemoizedFile> findAllFilesWithExtension(String extension, boolean includeNestedDirs)
	{
		List<MemoizedFile> foundFiles = new ArrayList<>();
		List<MemoizedFile> files = (includeNestedDirs) ? assetContainer.dir().nestedFiles() : assetContainer.dir().filesAndDirs();
		
		for (File f : files)
		{
			if (f.getName().endsWith(extension))
			{
				foundFiles.add( assetContainer.root().getMemoizedFile(f) );
			}
		}
		return foundFiles;
	}
	
}

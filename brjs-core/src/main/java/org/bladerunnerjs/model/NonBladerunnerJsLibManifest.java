package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.FileIterator;
import org.bladerunnerjs.utility.RelativePathUtility;

public class NonBladerunnerJsLibManifest extends ConfFile<YamlNonBladerunnerLibManifest>
{
	public static final String commaWithOptionalSpacesSeparator = "[\\s]*,[\\s]*";
	
	private final FileIterator fileIterator;
	private final File assetLocationDir;
	
	public NonBladerunnerJsLibManifest(AssetLocation assetLocation) throws ConfigException {
		super(assetLocation, YamlNonBladerunnerLibManifest.class, assetLocation.file("library.manifest"));
		fileIterator = assetLocation.root().getFileIterator(assetLocation.dir());
		assetLocationDir = assetLocation.dir();
	}
	
	public List<String> getDepends() throws ConfigException
	{
		reloadConfIfChanged();
		return listify(conf.depends, null);
	}
	
	public List<String> getJs() throws ConfigException
	{
		reloadConfIfChanged();
		return listify(conf.js, ".*\\.js"); // TODO: see if we should also limit to only loading js in the root directory by default too
	}
	
	public List<File> getJsFiles() throws ConfigException {
		return getFilesMatchingFilePaths(getJs());
	}
	
	public List<String> getCss() throws ConfigException
	{
		reloadConfIfChanged();
		return listify(conf.css, "[^/]*\\.css");
	}
	
	public List<File> getCssFiles() throws ConfigException {
		return getFilesMatchingFilePaths(getCss());
	}
	
	private List<String> listify(String value, String nullValueFallback)
	{
		if (value != null && !value.equals(""))
		{
			return Arrays.asList(value.split(commaWithOptionalSpacesSeparator));
		}
		return (nullValueFallback != null) ? Arrays.asList(nullValueFallback) : new ArrayList<String>();
	}
	
	private List<File> getFilesMatchingFilePaths(List<String> matchFilePaths)
	{
		List<File> filesMatching = new ArrayList<File>();
		List<File> files = fileIterator.nestedFiles();
		
		for (String pattern : matchFilePaths)
		{
			for (File f : files)
			{
				String relativePath = RelativePathUtility.get(assetLocationDir, f);
				if ( Pattern.matches(pattern, relativePath) )
				{
					filesMatching.add(f);
				}
			}
		}
		return filesMatching;
	}
}

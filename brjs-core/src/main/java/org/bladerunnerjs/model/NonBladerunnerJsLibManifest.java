package org.bladerunnerjs.model;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.exception.ConfigException;

public class NonBladerunnerJsLibManifest extends ConfFile<YamlNonBladerunnerLibManifest>
{
	
	public static final String commaWithOptionalSpacesSeparator = "[\\s]*,[\\s]*";
	
	
	public NonBladerunnerJsLibManifest(AssetLocation assetLocation) throws ConfigException {
		super(assetLocation, YamlNonBladerunnerLibManifest.class, assetLocation.file("library.manifest"));
	}
	
	public List<String> getDepends() throws ConfigException
	{
		reloadConf();
		return Arrays.asList(conf.depends.split(commaWithOptionalSpacesSeparator));
	}
	
	public List<String> getJs() throws ConfigException
	{
		reloadConf();
		return Arrays.asList(conf.js.split(commaWithOptionalSpacesSeparator));
	}

	public List<String> getCss() throws ConfigException
	{
		reloadConf();
		return Arrays.asList(conf.css.split(commaWithOptionalSpacesSeparator));
	}
	
	
}

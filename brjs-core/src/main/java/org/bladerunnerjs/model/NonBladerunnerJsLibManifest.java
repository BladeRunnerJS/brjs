package org.bladerunnerjs.model;

import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.exception.ConfigException;

public class NonBladerunnerJsLibManifest extends ConfFile<YamlNonBladerunnerLibManifest>
{
	
	public static final String commaWithOptionalSpacesSeparator = "[\\s]*,[\\s]*";
	
	//TODO: change this back to just taking an AssetLocation once we've fixed the ThirdpartyBundlerPlugin todo
	public NonBladerunnerJsLibManifest(AssetLocation assetLocation) throws ConfigException {
		super(assetLocation, YamlNonBladerunnerLibManifest.class, assetLocation.file("library.manifest"));
	}
	
	public List<String> getDepends() throws ConfigException
	{
		reloadConf();
		return listify(conf.depends);
	}
	
	public List<String> getJs() throws ConfigException
	{
		reloadConf();
		return listify(conf.js);
	}
	

	public List<String> getCss() throws ConfigException
	{
		reloadConf();
		return listify(conf.css);
	}
	
	
	private List<String> listify(String value)
	{
		if (value != null)
		{
			return Arrays.asList(value.split(commaWithOptionalSpacesSeparator));
		}
		return Arrays.asList();
	}
}

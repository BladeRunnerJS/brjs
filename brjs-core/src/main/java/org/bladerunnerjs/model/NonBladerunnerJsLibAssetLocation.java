package org.bladerunnerjs.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public class NonBladerunnerJsLibAssetLocation extends SourceAssetLocation
{
	
	private NonBladerunnerJsLibManifest manifest;
	private NonBladerunnerJsLibSourceFile sourceFile;
	private AssetContainer assetContainer;
	private AliasDefinitionsFile aliasDefinitionsFile;
	
	public NonBladerunnerJsLibAssetLocation(RootNode rootNode, AssetContainer parent, File dir) throws ConfigException
	{
		super(rootNode, parent, dir);
		manifest = new NonBladerunnerJsLibManifest(this);
		assetContainer = parent;
		sourceFile = new NonBladerunnerJsLibSourceFile(this, dir, manifest);
		aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, dir(), "aliasDefinitions.xml");
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}

	@Override
	public AliasDefinitionsFile aliasDefinitionsFile()
	{
		return aliasDefinitionsFile;
	}

	@Override
	public List<LinkedAssetFile> seedResources()
	{
		return Arrays.asList();
	}

	@Override
	public List<LinkedAssetFile> seedResources(String fileExtension)
	{
		return Arrays.asList();
	}

	@Override
	public List<AssetFile> bundleResources(String fileExtension)
	{
		return Arrays.asList();
	}

	@Override
	public AssetContainer getAssetContainer()
	{
		return assetContainer;
	}

	@Override
	public List<AssetLocation> getAncestorAssetLocations()
	{
		return Arrays.asList();
	}

	public SourceFile getNonBladerunnerJsSourceFile()
	{
		return sourceFile;
	}
	
}

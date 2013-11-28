package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public class NonBladerunnerJsLib extends AbstractAssetContainer implements JsLib
{
	
	private NonBladerunnerJsLibAssetLocation sourceLocation;
	
	public NonBladerunnerJsLib(RootNode rootNode, Node parent, File dir) throws ConfigException
	{
		super(rootNode, dir);
		init(rootNode, parent, dir);
		sourceLocation = new NonBladerunnerJsLibAssetLocation(rootNode, this, dir);
	}
	
	public static NodeMap<NonBladerunnerJsLib> createSdkNonBladeRunnerLibNodeSet()
	{
		return new NodeMap<>(NonBladerunnerJsLib.class, "sdk/libs/javascript/thirdparty", null);
	}
	
	public static NodeMap<NonBladerunnerJsLib> createAppNonBladeRunnerLibNodeSet()
	{
		return new NodeMap<>(NonBladerunnerJsLib.class, "thirdparty-libraries", null);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}

	@Override
	public String namespace()
	{
		return getName();
	}

	@Override
	public String getName()
	{
		return dir().getName();
	}

	@Override
	public boolean isValidName()
	{
		return true;
	}

	@Override
	public void assertValidName() throws InvalidNameException
	{
	}

	@Override
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException
	{
	}
	
	@Override
	public SourceAssetLocation src() {
		return sourceLocation;
	}
	
	@Override
	public AssetLocation resources()
	{
		return null;
	}
	
	@Override
	public List<SourceFile> sourceFiles() {
		return Arrays.asList( sourceLocation.getNonBladerunnerJsSourceFile() );
	}
	
	
	@Override
	public List<AssetLocation> getAllAssetLocations() {
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.add(src());
		return assetLocations;
	}
	
}

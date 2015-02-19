package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.utility.RequirePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

/**
 * A linked asset file that refers to another AssetFile using a fully qualified name such as 'my.package.myClass'
 *
 */
public class LinkedFileAsset implements LinkedAsset {
	private App app;
	protected MemoizedFile assetFile;
	protected AssetLocation assetLocation;
	private String assetPath;
	protected String defaultFileCharacterEncoding;
	private TrieBasedDependenciesCalculator trieBasedDependenciesCalculator;
	
	public LinkedFileAsset(MemoizedFile assetFile, AssetLocation assetLocation) {
		try {
			this.assetLocation = assetLocation;
			app = assetLocation.assetContainer().app();
			this.assetFile = assetLocation.root().getMemoizedFile(assetFile);
			assetPath = app.dir().getRelativePath(assetFile);
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new UnicodeReader(assetFile, defaultFileCharacterEncoding);
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {		
		try {
			 return bundlableNode.getLinkedAssets(assetLocation, getDependencyCalculator().getRequirePaths());
		}
		catch (AmbiguousRequirePathException e) {			
			e.setSourceRequirePath(getAssetPath());
			throw new ModelOperationException(e);
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return getDependencyCalculator().getAliases();
	}
	
	@Override
	public MemoizedFile dir()
	{
		return assetFile.getParentFile();
	}
	
	@Override
	public String getAssetName() {
		return assetFile.getName();
	}
	
	@Override
	public String getAssetPath() {
		return assetPath;
	}
	
	@Override
	public AssetLocation assetLocation()
	{
		return assetLocation;
	}
	
	private TrieBasedDependenciesCalculator getDependencyCalculator() {
		if (trieBasedDependenciesCalculator == null) {
			trieBasedDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new LinkedFileAssetDependenciesReader.Factory(this), assetFile);
		}
		return trieBasedDependenciesCalculator;
	}

	@Override
	public List<String> getRequirePaths() {
		return Collections.emptyList();
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return RequirePathUtility.getPrimaryRequirePath(this);
	}
}

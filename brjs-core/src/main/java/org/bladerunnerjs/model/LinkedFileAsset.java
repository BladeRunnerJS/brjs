package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
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
	
	private MemoizedFile assetFile;
	private String assetPath;
	private String defaultFileCharacterEncoding;
	private TrieBasedDependenciesCalculator trieBasedDependenciesCalculator;
	private String primaryRequirePath;
	private AssetContainer assetContainer;
	
	public LinkedFileAsset(MemoizedFile assetFile, AssetContainer assetContainer, String requirePrefix) {
		try {
			this.assetContainer = assetContainer;
			this.assetFile = assetFile;
			assetPath = assetContainer.app().dir().getRelativePath(assetFile);
			primaryRequirePath = requirePrefix+"/"+StringUtils.substringBeforeLast(assetFile.getName(), ".");
			defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
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
		List<Asset> assetList;
		try {
			assetList = bundlableNode.assets(assetContainer, getDependencyCalculator().getRequirePaths());
		}
		catch (AmbiguousRequirePathException e) {			
			e.setSourceRequirePath(getAssetPath());
			throw new ModelOperationException(e);
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
		Set<String> dependencies = new HashSet<String>();
		List<String> aliases = new ArrayList<>();
		try {
			RequirePathUtility.addRequirePathsFromReader(getReader(), dependencies, aliases);
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
		List<String> dependenciesList = new ArrayList<String>(dependencies);
		try {
			assetList.addAll(bundlableNode.assets(assetContainer, dependenciesList));
		} catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
		return assetList;
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
	
	private TrieBasedDependenciesCalculator getDependencyCalculator() {
		if (trieBasedDependenciesCalculator == null) {
			trieBasedDependenciesCalculator = new TrieBasedDependenciesCalculator(assetContainer, this, new LinkedFileAssetDependenciesReader.Factory(this), assetFile);
		}
		return trieBasedDependenciesCalculator;
	}

	@Override
	public List<String> getRequirePaths() {
		return Arrays.asList(primaryRequirePath);
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return primaryRequirePath;
	}	

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}

}

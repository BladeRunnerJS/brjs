package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.utility.RequirePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

/**
 * A linked asset file that refers to another AssetFile using a fully qualified name such as 'my.package.myClass'
 *
 */
public class LinkedFileAsset implements LinkedAsset {
	
	private MemoizedFile assetFile;

	private String assetPath;
	protected String defaultFileCharacterEncoding;
	private TrieBasedDependenciesCalculator trieBasedDependenciesCalculator;
	private String primaryRequirePath;
	private AssetContainer assetContainer;
	private List<Asset> implicitDependencies;
	
	public LinkedFileAsset(MemoizedFile assetFile, AssetContainer assetContainer, String requirePrefix, List<Asset> implicitDependencies) {
		try {
			this.assetContainer = assetContainer;
			this.assetFile = assetFile;
			assetPath = assetContainer.app().dir().getRelativePath(assetFile);
			primaryRequirePath = calculateRequirePath(requirePrefix, assetFile);
			defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			this.implicitDependencies = implicitDependencies;
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
		this.implicitDependencies.addAll(implicitDependencies);
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new UnicodeReader(assetFile, defaultFileCharacterEncoding);
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {		
		List<Asset> assetList = new ArrayList<>();
		try {
			assetList = bundlableNode.assets(this, getDependencyCalculator().getRequirePaths());
		}
		catch (AmbiguousRequirePathException e) {			
			e.setSourceRequirePath(getAssetPath());
			throw new ModelOperationException(e);
		}
		catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
		Set<String> dependencies = new LinkedHashSet<String>();
		List<String> aliases = new ArrayList<>();
		try {
			RequirePathUtility.addRequirePathsFromReader(getReader(), dependencies, aliases);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		List<String> dependenciesList = new ArrayList<String>(dependencies);
		try {
			assetList.addAll(bundlableNode.assets(this, dependenciesList));
		} catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		assetList.addAll(implicitDependencies);
		
		return assetList;
	}
	
	@Override
	public MemoizedFile file()
	{
		return assetFile;
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

	public static String calculateRequirePath(String requirePrefix, MemoizedFile assetFile)
	{
		return requirePrefix+"/"+assetFile.requirePathName();
	}

}

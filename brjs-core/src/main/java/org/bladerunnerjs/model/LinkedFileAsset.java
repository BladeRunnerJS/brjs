package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReaderFactory;

/**
 * A linked asset file that refers to another AssetFile using a fully qualified name such as 'my.package.myClass'
 *
 */
public class LinkedFileAsset implements LinkedAsset {
	private App app;
	private File assetFile;
	private AssetLocation assetLocation;
	private String assetPath;
	private String defaultFileCharacterEncoding;
	private TrieBasedDependenciesCalculator trieBasedDependenciesCalculator;
	
	public LinkedFileAsset(File assetFile, AssetLocation assetLocation) {
		try {
			this.assetLocation = assetLocation;
			app = assetLocation.assetContainer().app();
			this.assetFile = assetFile;
			assetPath = RelativePathUtility.get(app.dir(), assetFile);
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
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {		
		try {
			return bundlableNode.getSourceModules(assetLocation, getDependencyCalculator().getRequirePaths());
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
	public File dir()
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
			trieBasedDependenciesCalculator = new TrieBasedDependenciesCalculator(this, new JsCommentStrippingReaderFactory(this), assetFile);
		}
		return trieBasedDependenciesCalculator;
	}
	
}

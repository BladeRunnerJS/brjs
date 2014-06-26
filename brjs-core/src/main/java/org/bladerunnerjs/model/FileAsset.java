package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.PrimaryRequirePathUtility;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

public class FileAsset implements Asset {
	private File file;
	private AssetLocation assetLocation;
	private String defaultFileCharacterEncoding;
	private String assetPath;
	
	public FileAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		try {
			this.file = assetFile;
			this.assetLocation = assetLocation;
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			assetPath = RelativePathUtility.get(assetLocation.assetContainer().app().dir(), file, assetLocation.root());
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new UnicodeReader(file, defaultFileCharacterEncoding);
	}
	
	@Override
	public AssetLocation assetLocation() {
		return assetLocation;
	}
	
	@Override
	public File dir()
	{
		return file.getParentFile();
	}
	
	@Override
	public String getAssetName() {
		return file.getName();
	}
	
	@Override
	public String getAssetPath() {
		return assetPath;
	}

	@Override
	public List<String> getRequirePaths() {
		return Collections.emptyList();
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return PrimaryRequirePathUtility.getPrimaryRequirePath(this);
	}
}

package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.RequirePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

public class FileAsset implements Asset {
	private MemoizedFile file;
	private AssetLocation assetLocation;
	private String defaultFileCharacterEncoding;
	private String assetPath;
	
	public FileAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		try {
			this.file = assetLocation.root().getMemoizedFile(assetFile);
			this.assetLocation = assetLocation;
			defaultFileCharacterEncoding = assetLocation.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			assetPath = assetLocation.assetContainer().app().dir().getRelativePath(file);
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
	public MemoizedFile dir()
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
		return RequirePathUtility.getPrimaryRequirePath(this);
	}
}

package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.UnicodeReader;

public class FileAsset implements Asset {
	private MemoizedFile file;
	private AssetLocation assetLocation;
	private String defaultFileCharacterEncoding;
	private String assetPath;
	private String primaryRequirePath;
	
	public FileAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException {
		this(assetFile, assetLocation.assetContainer(), "");
		this.assetLocation = assetLocation;
	}
	
	public FileAsset(MemoizedFile assetFile, AssetContainer assetContainer, String requirePrefix) {
		try {
			this.file = assetFile;
			defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			assetPath = assetContainer.app().dir().getRelativePath(file);
			primaryRequirePath = requirePrefix+"/"+StringUtils.substringBeforeLast(assetFile.getName(),".");
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
		return Arrays.asList(primaryRequirePath);
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return primaryRequirePath;
	}
}

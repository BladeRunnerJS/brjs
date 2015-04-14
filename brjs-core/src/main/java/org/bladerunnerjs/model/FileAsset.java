package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.UnicodeReader;

public class FileAsset implements Asset {
	private MemoizedFile file;
	private String defaultFileCharacterEncoding;
	private String assetPath;
	private String primaryRequirePath;
	private AssetContainer assetContainer;
	
	public FileAsset(MemoizedFile assetFile, AssetContainer assetContainer, String requirePrefix) {
		try {
			this.file = assetFile;
			this.assetContainer = assetContainer;
			defaultFileCharacterEncoding = assetContainer.root().bladerunnerConf().getDefaultFileCharacterEncoding();
			assetPath = assetContainer.app().dir().getRelativePath(file);
			primaryRequirePath = calculateRequirePath(requirePrefix, assetFile);
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
	public MemoizedFile file()
	{
		return file;
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

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}
	
	@Override
	public boolean isRequirable()
	{
		return true;
	}

	public static String calculateRequirePath(String requirePrefix, MemoizedFile assetFile)
	{
		return requirePrefix+"/"+assetFile.requirePathName();
	}
	
	@Override
	public boolean isScopeEnforced()
	{
		return true;
	}
	
}

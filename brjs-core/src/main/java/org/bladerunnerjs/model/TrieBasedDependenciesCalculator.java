package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.trie.Trie;
import org.bladerunnerjs.utility.trie.TrieFactory;

public class TrieBasedDependenciesCalculator
{
	private static final Pattern getServiceMatcherPattern = Pattern.compile("getService\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");
	
	private App app;
	private Asset asset;
	private final AssetReaderFactory readerFactory;
	private final TrieFactory trieFactory;
	
	private MemoizedValue<ComputedValue> computedValue;

	private AssetContainer assetContainer;
	
	public TrieBasedDependenciesCalculator(AssetContainer assetContainer, Asset asset, AssetReaderFactory readerFactory, MemoizedFile... readerFiles)
	{
		this.asset = asset;
		this.readerFactory = readerFactory;
		this.assetContainer = assetContainer;
		app = assetContainer.app();
		trieFactory = TrieFactory.getFactoryForAssetContainer(assetContainer);
		
		List<MemoizedFile> scopeFiles = new ArrayList<>();
		scopeFiles.addAll(Arrays.asList(readerFiles));
		scopeFiles.addAll(Arrays.asList(new MemoizedFile[] {assetContainer.root().file("js-patches"), BladerunnerConf.getConfigFilePath(assetContainer.root()), app.dir(), app.root().sdkJsLibsDir().dir()}));
		computedValue = new MemoizedValue<>(asset.getAssetPath()+" - TrieBasedDependenciesCalculator.computedValue", assetContainer.root(), scopeFiles.toArray(new File[scopeFiles.size()]));
	}
	
	public List<String> getRequirePaths() throws ModelOperationException
	{
		return getRequirePaths(Asset.class);
	}
	
	public List<String> getRequirePaths(Class<? extends Asset> assetClass) throws ModelOperationException
	{
		Map<String, Class<? extends Asset>> requirePathsMap = getComputedValue().requirePaths;
		List<String> requirePaths = new LinkedList<>();
		for (String requirePath : requirePathsMap.keySet()) {
			Class<? extends Asset> computedAssetClass = requirePathsMap.get(requirePath);
			if (assetClass.isAssignableFrom(computedAssetClass)) {
				requirePaths.add( requirePath );
			}
		}
		return requirePaths;
	}
	
	private ComputedValue getComputedValue() throws ModelOperationException {
		return computedValue.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws ModelOperationException {
				ComputedValue computedValue = new ComputedValue();
				
				try(Reader reader = readerFactory.createReader()) {
					Trie<Asset> trie = trieFactory.createTrie();
					
					StringWriter readerContents = new StringWriter();
					IOUtils.copy(reader, readerContents);
					String contents = readerContents.toString();
					
					List<Asset> trieMatches = trie.getMatches( new StringReader(contents));
					for (Asset match : trieMatches) {
						if(!asset.getAssetPath().equals(match.getAssetPath())) {
							computedValue.requirePaths.put(match.getPrimaryRequirePath(), match.getClass());
						}
					}
					
					if (serviceRegistryPresent()) {
    					Matcher m = getServiceMatcherPattern.matcher(contents);
    					while (m.find()) {
    						String serviceName = m.group(1);
    						computedValue.requirePaths.put("service!"+serviceName, SourceModule.class);
    					}
					}
				}
				catch (IOException ex)
				{
					throw new ModelOperationException(ex);
				}
				
				return computedValue;
			}
		});
 	}
	
	private boolean serviceRegistryPresent() {
		for (AssetContainer assetContainer : assetContainer.scopeAssetContainers()) {
			if (assetContainer.asset("br/ServiceRegistry") != null) {
				return true;
			}
		}
		return false;
	}
	
	private class ComputedValue {
		public Map<String, Class<? extends Asset>> requirePaths = new LinkedHashMap<>();
	}
}

package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.trie.AliasReference;
import org.bladerunnerjs.utility.trie.AssetReference;
import org.bladerunnerjs.utility.trie.LinkedAssetReference;
import org.bladerunnerjs.utility.trie.Trie;
import org.bladerunnerjs.utility.trie.TrieFactory;

public class TrieBasedDependenciesCalculator
{
	private App app;
	private AssetLocation assetLocation;
	private Asset asset;
	private final AssetReaderFactory readerFactory;
	private final TrieFactory trieFactory;
	
	private MemoizedValue<ComputedValue> computedValue;
	
	public TrieBasedDependenciesCalculator(Asset asset, AssetReaderFactory readerFactory, MemoizedFile... readerFiles)
	{
		this.asset = asset;
		this.readerFactory = readerFactory;
		assetLocation = asset.assetLocation();
		app = assetLocation.assetContainer().app();
		trieFactory = TrieFactory.getFactoryForAssetContainer(assetLocation.assetContainer());
		
		List<MemoizedFile> scopeFiles = new ArrayList<>();
		scopeFiles.addAll(Arrays.asList(readerFiles));
		scopeFiles.addAll(Arrays.asList(new MemoizedFile[] {assetLocation.root().file("js-patches"), BladerunnerConf.getConfigFilePath(assetLocation.root()), app.dir(), app.root().sdkJsLibsDir().dir()}));
		computedValue = new MemoizedValue<>(asset.getAssetPath()+" - TrieBasedDependenciesCalculator.computedValue", assetLocation.root(), scopeFiles.toArray(new File[scopeFiles.size()]));
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
	
	public List<String> getAliases() throws ModelOperationException
	{
		return getComputedValue().aliases;
	}
	
	private ComputedValue getComputedValue() throws ModelOperationException {
		return computedValue.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws ModelOperationException {
				ComputedValue computedValue = new ComputedValue();
				
				try(Reader reader = readerFactory.createReader()) {
					Trie<AssetReference> trie = trieFactory.createTrie();
					
					for(Object match : trie.getMatches(reader)) {
						if (match instanceof LinkedAssetReference){
							LinkedAssetReference reference = (LinkedAssetReference)match;
							if(!asset.getAssetPath().equals(reference.getAssetPath())) {
								computedValue.requirePaths.put(reference.getRequirePath(), reference.getAssetClass());
							}
						}
						else if (match instanceof AliasReference) {
							AliasReference aliasReference = (AliasReference) match;
							String alias = aliasReference.getName();
							if (alias.length() > 0)
							{
								computedValue.aliases.add(alias);							
							}
						}
						else {
							throw new RuntimeException("Unknown match type returned from Trie.");
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
	
	private class ComputedValue {
		public Map<String, Class<? extends Asset>> requirePaths = new LinkedHashMap<>();
		public List<String> aliases = new ArrayList<>();
	}
}

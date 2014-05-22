package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.memoization.Getter;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.trie.AliasReference;
import org.bladerunnerjs.utility.trie.AssetReference;
import org.bladerunnerjs.utility.trie.SourceModuleReference;
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
	
	public TrieBasedDependenciesCalculator(Asset asset, AssetReaderFactory readerFactory, File... readerFiles)
	{
		this.asset = asset;
		this.readerFactory = readerFactory;
		assetLocation = asset.assetLocation();
		app = assetLocation.assetContainer().app();
		trieFactory = TrieFactory.getFactoryForAssetContainer(assetLocation.assetContainer());
		
		List<File> scopeFiles = new ArrayList<>();
		scopeFiles.addAll(Arrays.asList(readerFiles));
		scopeFiles.addAll(Arrays.asList(new File[] {assetLocation.root().file("js-patches"), assetLocation.root().conf().file("bladerunner.conf"), app.dir(), app.root().libsDir()}));
		computedValue = new MemoizedValue<>("TrieBasedDependenciesCalculator.computedValue", assetLocation.root(), scopeFiles.toArray(new File[scopeFiles.size()]));
	}
	
	public List<String> getRequirePaths() throws ModelOperationException
	{
		return getComputedValue().requirePaths;
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
						if (match instanceof SourceModuleReference) {
							SourceModuleReference sourceModuleReference = (SourceModuleReference) match;
							
							if(!asset.getAssetPath().equals(sourceModuleReference.getAssetPath())) {
								computedValue.requirePaths.add(sourceModuleReference.getRequirePath());
							}
						}
						else if (match instanceof AliasReference){
							AliasReference aliasReference = (AliasReference) match;
							String alias = aliasReference.getName();
							if (alias.length() > 0)
							{
								computedValue.aliases.add(alias);							
							}
						}
						else
						{
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
		public List<String> requirePaths = new ArrayList<>();
		public List<String> aliases = new ArrayList<>();
	}
}

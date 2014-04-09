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
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.Trie;
import org.bladerunnerjs.utility.reader.ReaderFactory;

public class TrieBasedDependenciesCalculator
{
	private App app;
	private AssetLocation assetLocation;
	private Asset asset;
	private final ReaderFactory readerFactory;
	private final TrieFactory trieFactory;
	
	private MemoizedValue<ComputedValue> computedValue;
	
	public TrieBasedDependenciesCalculator(Asset asset, ReaderFactory readerFactory, File... readerFiles)
	{
		this.asset = asset;
		this.readerFactory = readerFactory;
		assetLocation = asset.assetLocation();
		app = assetLocation.assetContainer().app();
		trieFactory = TrieFactory.getFactoryForApp(app);
		
		List<File> scopeFiles = new ArrayList<>();
		scopeFiles.addAll(Arrays.asList(readerFiles));
		scopeFiles.addAll(Arrays.asList(new File[] {assetLocation.root().conf().file("bladerunner.conf"), app.dir(), app.root().libsDir()}));
		computedValue = new MemoizedValue<>("TrieBasedDependenciesCalculator.computedValue", assetLocation.root(), scopeFiles.toArray(new File[scopeFiles.size()]));
	}
	
	public List<SourceModule> getCalculatedDependentSourceModules() throws ModelOperationException
	{
		return getComputedValue().dependentSourceModules;
	}
	
	public List<String> getCalculataedAliases() throws ModelOperationException
	{
		return getComputedValue().aliases;
	}
	
	private ComputedValue getComputedValue() throws ModelOperationException {
		return computedValue.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws ModelOperationException {
				ComputedValue computedValue = new ComputedValue();
				
				try(Reader reader = readerFactory.createReader(asset.getReader())) {
					Trie<Object> trie = trieFactory.createTrie();
					
					for(Object match : trie.getMatches(reader)) {
						if (match instanceof SourceModuleReference) {
							SourceModuleReference sourceModuleReference = (SourceModuleReference) match;
							SourceModule sourceModule = assetLocation.sourceModule(sourceModuleReference.getRequirePath());
							
							if(sourceModule != asset) {
								computedValue.dependentSourceModules.add(sourceModule);
							}
						}
						else if (match instanceof AliasReference){
							AliasReference aliasReference = (AliasReference) match;
							String alias = aliasReference.getAlias();
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
				catch (IOException | RequirePathException ex)
				{
					throw new ModelOperationException(ex);
				}
				
				return computedValue;
			}
		});
 	}
	
	private class ComputedValue {
		public List<SourceModule> dependentSourceModules = new ArrayList<>();
		public List<String> aliases = new ArrayList<>();
	}
}

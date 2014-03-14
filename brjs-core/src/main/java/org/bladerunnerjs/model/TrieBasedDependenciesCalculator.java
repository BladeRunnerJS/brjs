package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.EmptyTrieKeyException;
import org.bladerunnerjs.utility.FileModifiedChecker;
import org.bladerunnerjs.utility.Trie;
import org.bladerunnerjs.utility.TrieKeyAlreadyExistsException;


public class TrieBasedDependenciesCalculator
{
	private App app;
	private NodeProperties appProperties;
	private AssetLocation assetLocation;
	private FileModifiedChecker fileModifiedChecker;
	
	private List<SourceModule> dependentSourceModules;
	private List<String> aliases;
	private Asset asset;
	
	public TrieBasedDependenciesCalculator(Asset asset)
	{
		this.asset = asset;
		assetLocation = asset.getAssetLocation();
		app = assetLocation.getAssetContainer().getApp();
		appProperties = app.nodeProperties("TrieBasedDependenciesAsset");
		File assetFile = new File( asset.getAssetPath() );
		fileModifiedChecker = new FileModifiedChecker(assetFile);
	}
	
	public List<SourceModule> getCalculatedDependentSourceModules() throws ModelOperationException
	{
		recalculateDependencies();
		return new ArrayList<SourceModule>( dependentSourceModules );
	}
	
	public List<String> getCalculataedAliases() throws ModelOperationException
	{
		recalculateDependencies();
		return aliases;
	}
	
	
	// private methods
	
	private void recalculateDependencies() throws ModelOperationException {
		boolean trieUpdated = updateTrie();
		
		if(fileModifiedChecker.fileModifiedSinceLastCheck() || trieUpdated || (dependentSourceModules == null)) {
			dependentSourceModules = new ArrayList<>();
			aliases = new ArrayList<>();
			
			try(Reader reader = asset.getReader()) {
				for(Object match : getTrie().getMatches(reader)) {
					if (match instanceof SourceModuleReference) {
						SourceModuleReference sourceModuleReference = (SourceModuleReference) match;
						String dependencyRequirePath = sourceModuleReference.getRequirePath();
						dependentSourceModules.add( assetLocation.getSourceModuleWithRequirePath(dependencyRequirePath) );
					}
					else if (match instanceof AliasReference){
						AliasReference aliasReference = (AliasReference) match;
						String alias = aliasReference.getAlias();
						if (alias.length() > 0)
						{
							aliases.add(alias);							
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
 		}
 	}
	
	private boolean updateTrie() throws ModelOperationException {
		long trieDependenciesLastModifiedTimestamp = getTrieDependenciesLastModifiedTimestamp();
		boolean trieUpdated = false;
		
		if(trieDependenciesLastModifiedTimestamp > getTrieTimestamp()) {
			setTrieTimestamp(trieDependenciesLastModifiedTimestamp);
			trieUpdated = true;
			setTrie(createTrie());
		}
		
		return trieUpdated;
	}
	
	private Trie<Object> createTrie() throws ModelOperationException {
		Trie<Object> trie = new Trie<Object>();
		
		for(AssetContainer assetContainer : assetLocation.getAssetContainer().getApp().getAllAssetContainers()) {
			try {
				if(assetContainer instanceof BundlableNode) {
					BundlableNode bundlableNode = (BundlableNode) assetContainer;
					
					for(AliasOverride aliasOverride : bundlableNode.aliasesFile().aliasOverrides()) {
						if(!trie.containsKey(aliasOverride.getName())) {
							addToTrie(trie, "'" + aliasOverride.getName() + "'", new AliasReference(aliasOverride.getName()));
							addToTrie(trie, "\"" + aliasOverride.getName() + "\"", new AliasReference(aliasOverride.getName()));
						}
					}
				}
				
				for(SourceModule sourceModule : assetContainer.sourceModules()) {
					if (!sourceModule.getAssetPath().equals(asset.getAssetPath())) {
						addToTrie(trie, sourceModule.getRequirePath(), new SourceModuleReference(sourceModule.getRequirePath()));
						if (sourceModule.getClassname() != null)
						{
							addToTrie(trie, sourceModule.getClassname(), new SourceModuleReference(sourceModule.getRequirePath()));
						}
					}
				}
				
				for(AssetLocation assetLocation : assetContainer.assetLocations()) {
					for(String aliasName : assetLocation.aliasDefinitionsFile().aliasNames()) {
						if(!trie.containsKey("'" + aliasName + "'")) {
							addToTrie(trie, "'" + aliasName + "'", new AliasReference(aliasName));
							addToTrie(trie, "\"" + aliasName + "\"", new AliasReference(aliasName));
						}
					}
				}
			}
			catch (EmptyTrieKeyException | ContentFileProcessingException ex) {
				throw new ModelOperationException(ex);
			}
		}
		
		return trie;
	}
	
	private void addToTrie(Trie<Object> trie, String key, Object value) throws EmptyTrieKeyException
	{
		try
		{
			trie.add(key, value);
		}
		catch (TrieKeyAlreadyExistsException ex)
		{
		}
	}
	
	private long getTrieDependenciesLastModifiedTimestamp() {
		long lastModified = 0;
		
		for(AssetContainer assetContainer : app.getAllAssetContainers()) {
			long assetContainerLastModified = assetContainer.lastModified();
			
			if(assetContainerLastModified > lastModified) {
				lastModified = assetContainerLastModified;
			}
		}
		
		return lastModified;
	}
	
	@SuppressWarnings("unchecked")
	private Trie<Object> getTrie() {
		return (Trie<Object>) appProperties.getTransientProperty("trie");
	}
	
	private void setTrie(Trie<Object> trie) {
		appProperties.setTransientProperty("trie", trie);
	}
	
	private long getTrieTimestamp() {
		Long trieTimestamp = (Long) appProperties.getTransientProperty("trie-timestamp");
		return (trieTimestamp == null) ? 0 : trieTimestamp;
	}
	
	private void setTrieTimestamp(long trieTimestamp) {
		appProperties.setTransientProperty("trie-timestamp", trieTimestamp);
	}

	private class AliasReference
	{
		private String alias;
		private AliasReference(String alias)
		{
			this.alias = alias;
		}
		private String getAlias()
		{
			return alias;
		}
	}
	
	private class SourceModuleReference
	{
		private String requirePath;
		private SourceModuleReference(String requirePath)
		{
			this.requirePath = requirePath;
		}
		private String getRequirePath()
		{
			return requirePath;
		}
	}
	
}

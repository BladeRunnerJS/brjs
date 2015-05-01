package org.bladerunnerjs.utility.trie;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.utility.trie.exception.EmptyTrieKeyException;
import org.bladerunnerjs.utility.trie.exception.TrieKeyAlreadyExistsException;

public class TrieFactory {
	private final MemoizedValue<Trie<AssetReference>> trie;
	private final AssetContainer assetContainer;
	
	private static final Pattern ALIAS_MATCHER_PATTERN = Pattern.compile("[\"'][\\S ]+[\"']|<\\S+[\\s/>]");
	private static final Pattern QUOTED_SOURCE_MODULE_MATCHER_PATTERN = Pattern.compile("[\"']\\S+[\"']");
	private static final Pattern SOURCE_MODULE_MATCHER_PATTERN = Pattern.compile(".*", Pattern.DOTALL);
	
	public static TrieFactory getFactoryForAssetContainer(AssetContainer assetContainer) {
		NodeProperties nodeProperties = assetContainer.nodeProperties("TrieFactory");
		
		if(nodeProperties.getTransientProperty("trieFactoryInstance") == null) {
			nodeProperties.setTransientProperty("trieFactoryInstance", new TrieFactory(assetContainer));
		}
		
		return (TrieFactory) nodeProperties.getTransientProperty("trieFactoryInstance");
	}
	
	private TrieFactory(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
		trie = new MemoizedValue<>(assetContainer.dir()+" - TrieFactory.trie", assetContainer);
	}
	
	public Trie<AssetReference> createTrie() throws ModelOperationException {
		return trie.value(new Getter<ModelOperationException>() {
			@Override
			public Object get() throws RuntimeException, ModelOperationException {
				Trie<AssetReference> trie = new Trie<AssetReference>( '/', new Character[]{'.', '/'} );
				
				for (AssetContainer scopeAssetContainer : assetContainer.scopeAssetContainers()) {
					try {						
						for(Asset asset : scopeAssetContainer.assets()) {
							if (!asset.isRequirable()) {
								continue;
							}
							
							List<String> requirePaths = asset.getRequirePaths();
							
							for(String requirePath : requirePaths) {								
								if (requirePath.contains("/")) {
									addToTrie(trie, requirePath, new LinkedAssetReference(asset), SOURCE_MODULE_MATCHER_PATTERN);
								} else {
									// the asset is one that can only be referred to via a string
									addToTrie(trie, requirePath, new LinkedAssetReference(asset), QUOTED_SOURCE_MODULE_MATCHER_PATTERN);
								}
								
								boolean requirePathStartsWithAlias = requirePath.startsWith("alias!");
								String requirePathAfterAlias = StringUtils.substringAfter(requirePath, "alias!");
								if (requirePathStartsWithAlias) {
									addToTrie(trie, requirePathAfterAlias, new LinkedAssetReference(asset), ALIAS_MATCHER_PATTERN);										
								}
							}
						}
					}
					catch (EmptyTrieKeyException ex) {
						throw new ModelOperationException(ex);
					}
				}
				
				return trie;
			}
		});
	}
	
	private void addToTrie(Trie<AssetReference> trie, String key, AssetReference value, Pattern matchPattern) throws EmptyTrieKeyException {
		if (!trie.containsKey(key)) {
			try
			{
				trie.add(key, value, matchPattern);
			}
			catch (TrieKeyAlreadyExistsException e)
			{
				// wrap this in a RuntimeException since its unexpected, let the other exceptions bubble up
				throw new RuntimeException(e);
			}
		}
	}
}

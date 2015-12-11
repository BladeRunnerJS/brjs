package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bladerunnerjs.api.Asset;

// this class implements all methods of Map<String,AT> but doesn't implement Map so we can enforce the correct argument type in methods like 'containsKey'
public class AssetMap<AT extends Asset>
{

	Map<String,AT> internalMap = new LinkedHashMap<>();
	
	public AssetMap() {
	}
	
	public AssetMap(AssetMap<AT> assets) {
		putAll(assets);
	}
	
	public int size()
	{
		return internalMap.size();
	}
	
	public boolean isEmpty()
	{
		return internalMap.isEmpty();
	}
	
	public boolean containsKey(String requirePath)
	{
		return internalMap.containsKey(requirePath);
	}
	
	public boolean containsKey(AT asset)
	{
		return containsKey(asset.getPrimaryRequirePath());
	}
	
	public boolean containsValue(AT value)
	{
		return internalMap.containsValue(value);
	}

	public AT get(String key)
	{
		return internalMap.get(key);
	}
	
	public boolean put(String key, AT value)
	{
//		return (internalMap.putIfAbsent(key, value) == null);
		if (containsKey(key)) {
			return false;
		}
		internalMap.put(key, value);
		return true;
	}
	
	public boolean put(AT asset)
	{
		return put(asset.getPrimaryRequirePath(), asset);
	}
	
	public void putFirst(AssetMap<AT> assetMap)
	{
		Map<String,AT> newInternalMap = new LinkedHashMap<>();
		newInternalMap.putAll(assetMap.internalMap);
		newInternalMap.putAll(internalMap);
		internalMap = newInternalMap;
	}
	
	public AT remove(String key)
	{
		return internalMap.remove(key);
	}
	
	public AT remove(AT asset)
	{
		return remove(asset.getPrimaryRequirePath());
	}
	
	public void putAll(Map<? extends String, ? extends AT> m)
	{
		internalMap.putAll(m);
	}
	
	public void putAll(AssetMap<AT> assetMap)
	{
		internalMap.putAll(assetMap.internalMap);
	}
	
	public void clear()
	{
		internalMap.clear();
	}
	
	public Set<String> keySet()
	{
		return internalMap.keySet();
	}
	
	public List<AT> values()
	{
		return new ArrayList<>(internalMap.values());
	}

	public Set<Entry<String, AT>> entrySet()
	{
		return internalMap.entrySet();
	}

}

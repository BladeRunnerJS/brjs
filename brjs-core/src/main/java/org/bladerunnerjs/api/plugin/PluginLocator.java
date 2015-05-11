package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.BRJS;

/**
 * An interface for the retrieval of various types of plugins e.g. {@link TagHandlerPlugin}, {@link CommandPlugin}.
 */

public interface PluginLocator
{
	
	/**
	 * The method creates all available plugins, no matter the type, for the specified {@link BRJS} object.
	 * 
	 * @param brjs a BRJS object for which all available plugins will be created
	 */
	// if more plugin types are added defaults should be added to BladerunnerConf
	void createPlugins(BRJS brjs);
	
	/**
	 * The method retrieves all {@link CommandPlugin}s for the {@link BRJS} object inputed through the createPlugins function.
	 * 
	 * @return all available CommandPlugin objects
	 */
	List<CommandPlugin> getCommandPlugins();
	
	/**
	 * The method retrieves all {@link ModelObserver}s for the {@link BRJS} object inputed through the createPlugins function.
	 * 
	 * @return all available ModelObserver objects
	 */
	List<ModelObserverPlugin> getModelObserverPlugins();
	
	/**
	 * The method retrieves all {@link ContentPlugin}s for the {@link BRJS} object inputed through the createPlugins function.
	 * 
	 * @return all available ContentPlugin objects
	 */
	List<ContentPlugin> getContentPlugins();
	
	/**
	 * The method retrieves all {@link TagHandlerPlugin}s for the {@link BRJS} object inputed through the createPlugins function.
	 * 
	 * @return all available TagHandlerPlugin objects
	 */
	List<TagHandlerPlugin> getTagHandlerPlugins();
	
	/**
	 * The method retrieves all {@link AssetPlugin}s for the {@link BRJS} object inputed through the createPlugins function.
	 * 
	 * @return all available AssetPlugin objects
	 */
	List<AssetPlugin> assetPlugins();
	
	/**
	 * The method retrieves all {@link MinifierPlugin}s for the {@link BRJS} object inputed through the createPlugins function.
	 * 
	 * @return all available MinifierPlugin objects
	 */
	List<MinifierPlugin> getMinifierPlugins();
	
	/**
	 * The method retrieves all {@link RequirePlugin}s for the {@link BRJS} object inputed through the createPlugins function.
	 * 
	 * @return all available RequirePlugin objects
	 */
	List<RequirePlugin> getRequirePlugins();
}

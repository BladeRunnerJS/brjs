package org.bladerunnerjs.core.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.BRJS;



public class PluginLoader
{
	// TODO: these messages (and likely, this classes functionality) aren't currently covered within our spec tests
	public class Messages {
		public static final String ERROR_CREATING_OBJECT_LOG_MSG =
			"There was an error loading plugins, some plugins have not been loaded. The exception was: %s";
		public static final String CANNOT_CREATE_INSTANCE_LOG_MSG =
			"Error while creating the plugin %s, the class will not be loaded. Make sure there is a constructor for the class that accepts 0 arguments.";
	}
	
	public static <P extends Plugin> List<P> createPluginsOfType(BRJS brjs, Class<P> pluginInterface)
	{
		return createPluginsOfType(brjs, pluginInterface, null);
	}
	
	public static <P extends Plugin, VPP extends P> List<P> createPluginsOfType(BRJS brjs, Class<P> pluginInterface, Class<VPP> virtualProxyClass)
	{
		Logger logger = brjs.logger(LoggerType.CORE, BRJSPluginLocator.class);
		List<P> objectList = new ArrayList<P>();
		
		try
		{
			ServiceLoader<P> loader = ServiceLoader.load(pluginInterface);
			Iterator<P> objectIterator = loader.iterator();
			
			while (objectIterator.hasNext())
			{
				P object = objectIterator.next();
				
				if(virtualProxyClass != null) {
					object = virtualProxyClass.getConstructor(pluginInterface).newInstance(object);
				}
				
				object.setBRJS(brjs);
				
				objectList.add(object);
			}
		}
		catch (ServiceConfigurationError serviceError)
		{
			Throwable cause = serviceError.getCause();
			
			if (cause != null && cause.getClass() == InstantiationException.class)
			{
				logger.error(Messages.CANNOT_CREATE_INSTANCE_LOG_MSG, cause.getMessage());				
			} else 
			{
				logger.error(Messages.ERROR_CREATING_OBJECT_LOG_MSG, serviceError);
			}
		}
		catch(NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			throw new RuntimeException(e);
		}
		
		return objectList;
	}
}

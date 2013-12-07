package org.bladerunnerjs.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;



public class TypedPluginCreator<I>
{
	public class Messages {
//		TODO: this message is not covered within our spec tests
		public static final String ERROR_CREATING_OBJECT_LOG_MSG =
			"There was an error loading plugins, some plugins have not been loaded. The exception was: %s";
		public static final String CANNOT_CREATE_INSTANCE_LOG_MSG =
			"Error while creating the plugin %s, the class will not be loaded. Make sure there is a constructor for the class that accepts 0 arguments.";
	}
	
	List<I> getSubTypesOfClass(BRJS brjs, Class<I> iFace)
	{
		List<I> objectList = new ArrayList<I>();

		Logger logger = brjs.logger(LoggerType.CORE, BRJSPluginLocator.class);

		ServiceLoader<I> loader = ServiceLoader.load(iFace);
		try
		{
			Iterator<I> classes = loader.iterator();
			while (classes.hasNext())
			{
				I clazz = classes.next();
				objectList.add(clazz);
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

		return objectList;
	}
}

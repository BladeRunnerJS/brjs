package com.caplin.cutlass.structure.model.path;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caplin.cutlass.util.FileUtility;

// TODO: experimental class
public class PathAccessor
{
	private static Map<String, AbstractPath> paths = new HashMap<String, AbstractPath>();
	
	@SuppressWarnings("unchecked")
	public static <P extends AbstractPath> List<P> getPaths(File path, String acceptSuffix, Class<P> pathClass)
	{
		List<P> childPaths = new ArrayList<P>();
		
		if(path.exists())
		{
			for(File childDir : FileUtility.sortFileArray(path.listFiles()))
			{
				if(childDir.isDirectory() && childDir.getName().endsWith(acceptSuffix))
				{
					try
					{
						String absoluteChildPath = childDir.getAbsolutePath();
						P childPath = null;
						
						if(paths.containsKey(absoluteChildPath))
						{
							childPath = (P) paths.get(absoluteChildPath);
						}
						else
						{
							childPath = pathClass.getConstructor(File.class).newInstance(childDir);
							registerPath(absoluteChildPath, childPath);
						}
						
						childPaths.add(childPath);
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		return childPaths;
	}
	
	private static synchronized void registerPath(String absoluteChildPath, AbstractPath path)
	{
		// TODO: the idea of this method is store references to all paths that also happen to exist
		//	   this can then be used by the locateAncestorPath() methods to quickly find the right paths
		paths.put(absoluteChildPath, path);
	}
}

package org.bladerunnerjs.model.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.NodeAlreadyRegisteredException;

public class NodeCreator
{
	@SuppressWarnings("unchecked")
	protected static <N extends Node> Node createNode(RootNode rootNode, Node parent, MemoizedFile file, String name, Class<N> nodeClass)
	{
		Node node = null;
		
		try
		{
			boolean constructorFound = false;
			
			for(Constructor<?> constructor : orderNodeConstructors(nodeClass.getDeclaredConstructors()))
			{
				Type[] constructorParams = constructor.getGenericParameterTypes();
				
				if(isBasicNodeConstructor(constructorParams))
				{
					constructorFound = true;
					node = (N) constructor.newInstance(file);
				}
				else if(isNodeConstructor(constructorParams))
				{
					constructorFound = true;
					node = (N) constructor.newInstance(rootNode, parent, file);
				}
				else if(isNamedNodeConstructor(constructorParams))
				{
					constructorFound = true;
					node = (N) constructor.newInstance(rootNode, parent, file, name);
				}
				
				if(constructorFound) {
					break;
				}
			}
			
			if(!constructorFound)
			{
				throw new RuntimeException("No valid Node constructor found for '" + nodeClass.getName() + "'");
			}
			rootNode.registerNode(node);
		}
		catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NodeAlreadyRegisteredException e)
		{
			throw new RuntimeException(e);
		}
		
		return node;
	}
	
	private static List<Constructor<?>> orderNodeConstructors(Constructor<?>[] declaredConstructors)
	{
		List<Constructor<?>> sortedConstructors = Arrays.asList(declaredConstructors);
		
		Collections.sort(sortedConstructors, new Comparator<Constructor<?>>() {
			@Override
			public int compare(Constructor<?> c1, Constructor<?> c2) {
				return c2.getGenericParameterTypes().length - c1.getGenericParameterTypes().length;
			}
		});
		
		return sortedConstructors;
	}
	
	private static boolean isBasicNodeConstructor(Type[] constructorParams)
	{
		return constructorParams.length == 1;
	}
	
	private static boolean isNodeConstructor(Type[] constructorParams)
	{
		return constructorParams.length == 3;
	}
	
	private static boolean isNamedNodeConstructor(Type[] constructorParams)
	{
		return constructorParams.length == 4;
	}
}
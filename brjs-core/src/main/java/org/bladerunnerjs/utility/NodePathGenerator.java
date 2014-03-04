package org.bladerunnerjs.utility;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;

import com.google.common.base.Joiner;

public class NodePathGenerator {
	public static String generatePath(Node node)
	{
		List<String> path = new ArrayList<>();
		
		while(node.parentNode() != null)
		{
			if(node instanceof NamedNode)
			{
				NamedNode namedNode = (NamedNode) node;
				String accessorMethod = getAccessorMethod(namedNode, namedNode.getClass());
				
				path.add(0, accessorMethod + "/" + namedNode.getName());
			}
			else
			{
				String accessorMethod = getAccessorMethod(node, node.getClass());
				
				path.add(0, accessorMethod);
			}
			
			node = node.parentNode();
		}
		
		return Joiner.on("/").join(path);
	}
	
	private static String getAccessorMethod(Node node, Class<?> returnType)
	{
		Node parentNode = node.parentNode();
		String methodName = null;
		
		for(Method parentMethod : sortMethods(parentNode.getClass().getDeclaredMethods()))
		{
			Type methodReturnType = parentMethod.getGenericReturnType();
			Class<?> containedNodeClass = null;
			
			if(methodReturnType instanceof Class)
			{
				containedNodeClass = (Class<?>) methodReturnType;
			}
			else if(methodReturnType instanceof ParameterizedType)
			{
				ParameterizedType parameterizedReturnType = (ParameterizedType) methodReturnType;
				
				if(parameterizedReturnType.getRawType() instanceof List)
				{
					containedNodeClass = (Class<?>) parameterizedReturnType.getActualTypeArguments()[0];
				}
			}
			
			if(containedNodeClass == returnType)
			{
				methodName = parentMethod.getName();
				break;
			}
		}
		
		return methodName;
	}
	
	private static List<Method> sortMethods(Method[] methods)
	{
		List<Method> methodsList = new ArrayList<>();
		
		for(Method method : methods)
		{
			methodsList.add(method);
		}
		
		Collections.sort(methodsList, new Comparator<Method>(){
			@Override
			public int compare(Method method1, Method method2)
			{
				return method1.getName().compareTo(method2.getName());
			}
		});
		
		return methodsList;
	}
}
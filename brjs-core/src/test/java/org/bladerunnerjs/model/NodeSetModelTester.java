package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJSNode;

import static org.junit.Assert.*;

public class NodeSetModelTester<PN extends BRJSNode, CN extends BRJSNode>
{
	private final PN parentNode;
	private final Class<PN> parentNodeClass;
	private final Class<CN> childNodeClass;
	private final String childrenMethodName;
	private final String childMethodName;
	private final Map<String, File> childPaths =  new LinkedHashMap<>();
	
	public NodeSetModelTester(PN parentNode, Class<PN> parentNodeClass, Class<CN> childNodeClass, String childrenMethodName, String childMethodName)
	{
		this.parentNode = parentNode;
		this.parentNodeClass = parentNodeClass;
		this.childNodeClass = childNodeClass;
		this.childrenMethodName = childrenMethodName;
		this.childMethodName = childMethodName;
	}
	
	public NodeSetModelTester<PN, CN> addChild(String childName, String childPath)
	{
		try
		{
			childPaths.put(childName, new File(parentNode.dir(), childPath).getCanonicalFile());
		}
		catch (IOException e)
		{
			new RuntimeException(e);
		}
		
		return this;
	}
	
	public void assertModelIsOK()
	{
		try
		{
			NodeModelTester.verifyBottomUpLocation(parentNode, childNodeClass, childPaths.values());
			verifyTopDownAllItems(parentNode);
			if(childMethodName != null)
			{
				verifyTopDownNamedItems(parentNode);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void verifyTopDownAllItems(PN parentNode) throws Exception
	{
		Method getAllItemsMethod = parentNodeClass.getMethod(childrenMethodName);
		@SuppressWarnings("unchecked")
		List<CN> childrenNodes = (List<CN>) getAllItemsMethod.invoke(parentNode);
		int nextChild = 0;
		
		for(Map.Entry<String, File> entry : childPaths.entrySet())
		{
			File entryPath = entry.getValue();
			CN childNode = childrenNodes.get(nextChild++);
			
			assertEquals(entryPath.getAbsolutePath(), childNode.dir().getAbsolutePath());
		}
		
		assertEquals("list lengths differ", childPaths.size(), childrenNodes.size());
	}
	
	private void verifyTopDownNamedItems(PN parentNode) throws Exception
	{
		Method getNamedItemMethod = parentNodeClass.getMethod(childMethodName, new Class<?>[] {String.class});
		
		for(Map.Entry<String, File> entry : childPaths.entrySet())
		{
			String entryName = entry.getKey();
			File entryPath = entry.getValue();
			@SuppressWarnings("unchecked")
			CN childNode = (CN) getNamedItemMethod.invoke(parentNode, entryName);
			
			assertEquals(entryPath.getAbsolutePath(), childNode.dir().getAbsolutePath());
		}
	}
}

package org.bladerunnerjs.model;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.BRJSNode;

import static org.junit.Assert.*;

public class NodeSetModelTester<PN extends BRJSNode, CN extends BRJSNode>
{
	private final PN parentNode;
	private final Class<PN> parentNodeClass;
	private final Class<CN> childNodeClass;
	private final String childrenMethodName;
	private final String childMethodName;
	private final Map<String, MemoizedFile> childPaths =  new LinkedHashMap<>();
	
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
		childPaths.put(childName, parentNode.dir().file(childPath));
		
		return this;
	}
	
	public void assertModelIsOK()
	{
		try
		{
			if(childMethodName != null)
			{
				verifyTopDownNamedItems(parentNode);
			}
			NodeModelTester.verifyBottomUpLocation(parentNode, childNodeClass, childPaths.values());
			verifyTopDownAllItems(parentNode);
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
		
		assertEquals("list lengths differ", childPaths.size(), childrenNodes.size());

		for(Map.Entry<String, MemoizedFile> entry : childPaths.entrySet())
		{
			File entryPath = entry.getValue();
			CN childNode = childrenNodes.get(nextChild++);
			
			assertEquals(entryPath.getAbsolutePath(), childNode.dir().getPath());
		}
		
	}
	
	private void verifyTopDownNamedItems(PN parentNode) throws Exception
	{
		Method getNamedItemMethod = parentNodeClass.getMethod(childMethodName, new Class<?>[] {String.class});
		
		for(Map.Entry<String, MemoizedFile> entry : childPaths.entrySet())
		{
			String entryName = entry.getKey();
			File entryPath = entry.getValue();
			@SuppressWarnings("unchecked")
			CN childNode = (CN) getNamedItemMethod.invoke(parentNode, entryName);
			
			assertEquals(entryPath.getAbsolutePath(), childNode.dir().getPath());
		}
	}
}

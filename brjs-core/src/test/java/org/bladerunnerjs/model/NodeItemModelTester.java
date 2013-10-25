package org.bladerunnerjs.model;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJSNode;

import static org.junit.Assert.*;

public class NodeItemModelTester<PN extends BRJSNode, CN extends BRJSNode>
{
	private final PN parentNode;
	private final Class<PN> parentNodeClass;
	private final Class<CN> childNodeClass;
	private final String itemMethodName;
	private final File childPath;
	private final List<File> childPaths = new ArrayList<>();
	
	public NodeItemModelTester(PN parentNode, Class<PN> parentNodeClass, Class<CN> childNodeClass, String itemMethodName, String childPath)
	{
		this.parentNode = parentNode;
		this.parentNodeClass = parentNodeClass;
		this.childNodeClass = childNodeClass;
		this.itemMethodName = itemMethodName;
		this.childPath = new File(parentNode.dir(), childPath);
		
		childPaths.add(this.childPath);
	}
	
	public void assertModelIsOK()
	{
		try
		{
			NodeModelTester.verifyBottomUpLocation(parentNode, childNodeClass, childPaths);
			verifyTopDownItem(parentNode);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void verifyTopDownItem(PN parentNode) throws Exception
	{
		Method getItemMethod = parentNodeClass.getMethod(itemMethodName);
		@SuppressWarnings("unchecked")
		CN childNode = (CN) getItemMethod.invoke(parentNode);
		
		assertEquals(childPath.getAbsolutePath(), childNode.dir().getAbsolutePath());
	}
}

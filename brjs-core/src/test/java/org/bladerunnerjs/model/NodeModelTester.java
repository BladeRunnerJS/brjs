package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;

import org.bladerunnerjs.model.BRJSNode;

public class NodeModelTester
{
	public static <PN extends BRJSNode, CN extends BRJSNode> void verifyBottomUpLocation(PN parentNode, Class<CN> childNodeClass, Collection<File> possibleNodeLocations) throws Exception
	{
		CN locatedNode = null;
		
		assertTrue("you must provide possible node locations", possibleNodeLocations.size() > 0);
		
		for(File possibleNodeLocation : possibleNodeLocations)
		{
			locatedNode = parentNode.root().locateAncestorNodeOfClass(possibleNodeLocation, childNodeClass);
			
			assertNotNull("no node of class " + childNodeClass + " found for location " + possibleNodeLocation, locatedNode);
			assertNotNull("no '" + childNodeClass.getSimpleName() + "' node located for '" + possibleNodeLocation.getAbsolutePath() + "'", locatedNode.dir());
			assertEquals("located node was found at given directory, but .dir() returns different location", possibleNodeLocation.getAbsolutePath(), locatedNode.dir().getAbsolutePath());
			
			
			if (locatedNode instanceof JsLib && !possibleNodeLocation.getAbsolutePath().contains("/apps/"))
			{
				assertSame(parentNode.root(), locatedNode.parentNode());
			}
			else
			{
				assertSame(parentNode, locatedNode.parentNode());
			}
		}
	}
}

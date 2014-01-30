package org.bladerunnerjs.utility;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginAccessorTest extends SpecTest
{

	private TagHandlerPlugin highPriorityTagHandler;
	private TagHandlerPlugin defaultPriorityTagHandler;
	private TagHandlerPlugin lowPriorityTagHandler;

	@Before
	public void initTestObjects() throws Exception
	{
		highPriorityTagHandler = mock(TagHandlerPlugin.class);
		Mockito.when(highPriorityTagHandler.priority()).thenReturn(100);
		Mockito.when(highPriorityTagHandler.toString()).thenReturn("highPriorityTagHandler");
		
		defaultPriorityTagHandler = mock(TagHandlerPlugin.class);
		Mockito.when(defaultPriorityTagHandler.priority()).thenReturn(0);
		Mockito.when(defaultPriorityTagHandler.toString()).thenReturn("defaultPriorityTagHandler");
		
		lowPriorityTagHandler = mock(TagHandlerPlugin.class);
		Mockito.when(lowPriorityTagHandler.priority()).thenReturn(0);
		Mockito.when(lowPriorityTagHandler.toString()).thenReturn("lowPriorityTagHandler");
		
		given(brjs).hasTagPlugins(defaultPriorityTagHandler, lowPriorityTagHandler, highPriorityTagHandler)
			.and(brjs).hasBeenCreated();
	}
	
	//TODO: this test can probably be deleted once it's passing since it will be tested by other ordering tests
	@Test
	public void pluginsHaveTheCorrectOrder()
	{
		assertEquals(3, brjs.plugins().tagHandlers().size());
		assertEquals( highPriorityTagHandler.priority(), brjs.plugins().tagHandlers().get(0).priority() );
		assertEquals( defaultPriorityTagHandler.priority(), brjs.plugins().tagHandlers().get(1).priority() );
		assertEquals( lowPriorityTagHandler.priority(), brjs.plugins().tagHandlers().get(2).priority() );
	}
	
	
	
}

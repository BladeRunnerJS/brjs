package com.caplin.cutlass.filter.tokenfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JndiTokenFinderTest
{

	private JndiTokenFinder tokenFinder;
	private Context mockContext;

	@Before
	public void setup()
	{
		mockContext = mock(Context.class);
		tokenFinder = new JndiTokenFinder(mockContext);
	}

	@After
	public void tearDown()
	{
		verifyNoMoreInteractions(mockContext);
	}

	@Test
	public void testNullIsReturnedForEmptyToken() throws Exception
	{
		assertNull(tokenFinder.findTokenValue(""));
	}

	@Test
	public void testNullIsReturnedForNullToken() throws Exception
	{
		assertNull(tokenFinder.findTokenValue(null));
	}

	@Test
	public void testTokenIsReturnedFromContextLookup() throws Exception
	{
		when(mockContext.lookup("java:comp/env/TEST.TOKEN")).thenReturn("token value");

		assertEquals("token value", tokenFinder.findTokenValue("TEST.TOKEN"));
		verify(mockContext, times(1)).lookup("java:comp/env/TEST.TOKEN");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNullIsReturnedIfLookupThrowsException() throws Exception
	{
		when(mockContext.lookup("java:comp/env/NON.EXISTANT.TOKEN")).thenThrow(NamingException.class);

		assertNull("token value", tokenFinder.findTokenValue("NON.EXISTANT.TOKEN"));
		verify(mockContext, times(1)).lookup("java:comp/env/NON.EXISTANT.TOKEN");
	}

	@Test
	public void testNullIsReturnedIfLookupReturnsNull() throws Exception
	{
		when(mockContext.lookup("java:comp/env/NON.EXISTANT.TOKEN")).thenReturn(null);

		assertNull("token value", tokenFinder.findTokenValue("NON.EXISTANT.TOKEN"));
		verify(mockContext, times(1)).lookup("java:comp/env/NON.EXISTANT.TOKEN");
	}

	@Test
	public void testEmptyStringIsReturnedIfLookupReturnsAnEmptyString() throws Exception
	{
		when(mockContext.lookup("java:comp/env/EMPTY.TOKEN")).thenReturn("");

		assertEquals("", tokenFinder.findTokenValue("EMPTY.TOKEN"));
		verify(mockContext, times(1)).lookup("java:comp/env/EMPTY.TOKEN");
	}

}

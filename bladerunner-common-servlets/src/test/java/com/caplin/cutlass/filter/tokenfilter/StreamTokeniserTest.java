package com.caplin.cutlass.filter.tokenfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class StreamTokeniserTest
{

	private StreamTokeniser tokeniser;
	private JndiTokenFinder mockTokenFinder;

	@Before
	public void setup()
	{
		tokeniser = new StreamTokeniser();
		mockTokenFinder = mock(JndiTokenFinder.class);
	}

	@After
	public void tearDown()
	{
		verifyNoMoreInteractions(mockTokenFinder);
	}

	@Test
	public void testAppVersionIsAutomaticallyGeneratedIfItDoesntExist() throws Exception
	{
		String inputString = "app version is @"+StreamTokeniser.APP_VERSION_TOKEN+"@";
		String expectedOutputRegex = "app version is /v_[\\d]{14}/";
		when(mockTokenFinder.findTokenValue(StreamTokeniser.APP_VERSION_TOKEN)).thenReturn(null);

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertTrue(actualOutput.matches(expectedOutputRegex));
		verify(mockTokenFinder, times(1)).findTokenValue(StreamTokeniser.APP_VERSION_TOKEN);
	}
	
	@Test
	public void testAppVersionIsDifferentOnCallsAtDifferentTimesToTokeniser() throws Exception
	{
		String inputString = "app version is @"+StreamTokeniser.APP_VERSION_TOKEN+"@";
		String expectedOutputRegex = "app version is /v_[\\d]{14}/";
		when(mockTokenFinder.findTokenValue(StreamTokeniser.APP_VERSION_TOKEN)).thenReturn(null);

		String actualOutput1 = doFilter(inputString, mockTokenFinder);
		Thread.sleep(1000);
		String actualOutput2 = doFilter(inputString, mockTokenFinder);
		assertTrue(actualOutput1.matches(expectedOutputRegex));
		assertTrue(actualOutput2.matches(expectedOutputRegex));
		assertFalse(actualOutput1.equals(actualOutput2));
		verify(mockTokenFinder, times(2)).findTokenValue(StreamTokeniser.APP_VERSION_TOKEN);
	}
	
	@Test
	public void testAppVersionFromTokenFilterIsUsedIfItExists() throws Exception
	{
		String inputString = "app version is @"+StreamTokeniser.APP_VERSION_TOKEN+"@";
		String expectedOutput = "app version is /v1234/";
		when(mockTokenFinder.findTokenValue(StreamTokeniser.APP_VERSION_TOKEN)).thenReturn("v1234");

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
		verify(mockTokenFinder, times(1)).findTokenValue(StreamTokeniser.APP_VERSION_TOKEN);
	}
	
	@Test
	public void testAppVersionAlwaysEndWithSlash() throws Exception
	{
		String inputString = "app version is @"+StreamTokeniser.APP_VERSION_TOKEN+"@";
		
		assertTrue( doFilter(inputString, mockTokenFinder).matches("app version is /v_[\\d]{14}/") );
		
		when(mockTokenFinder.findTokenValue(StreamTokeniser.APP_VERSION_TOKEN)).thenReturn("v1234");
		assertTrue( doFilter(inputString, mockTokenFinder).equals("app version is /v1234/") );
		
		Mockito.reset(mockTokenFinder);
	}
	
	@Test
	public void testABasicTokenIsReplaced() throws Exception
	{
		String inputString = "hello @TOKEN.WORLD@!";
		String expectedOutput = "hello world!";
		when(mockTokenFinder.findTokenValue("TOKEN.WORLD")).thenReturn("world");

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
		verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.WORLD");
	}

	@Test
	public void testMoreThanOneTokenCanBeReplaced() throws Exception
	{
		String inputString = "@TOKEN.HELLO@ @TOKEN.WORLD@!";
		String expectedOutput = "hello world!";
		when(mockTokenFinder.findTokenValue("TOKEN.HELLO")).thenReturn("hello");
		when(mockTokenFinder.findTokenValue("TOKEN.WORLD")).thenReturn("world");

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
		verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.HELLO");
		verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.WORLD");
	}

	@Test
	public void testTokensDontNeedSpacesBetweenThem() throws Exception
	{
		String inputString = "@TOKEN.HEL@@TOKEN.LO@";
		String expectedOutput = "hello";
		when(mockTokenFinder.findTokenValue("TOKEN.HEL")).thenReturn("hel");
		when(mockTokenFinder.findTokenValue("TOKEN.LO")).thenReturn("lo");

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
		verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.HEL");
		verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.LO");
	}

	@Test
	public void testTokenReplacementsCanContainSpaces() throws Exception
	{
		String inputString = "@TOKEN.HELLO.WORLD@";
		String expectedOutput = "hello world!";
		when(mockTokenFinder.findTokenValue("TOKEN.HELLO.WORLD")).thenReturn("hello world!");

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
		verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.HELLO.WORLD");
	}

	@Test
	public void testTokenWithLowercaseCharactersAreUnchanged() throws Exception
	{
		String inputString = "@hello.world@";
		String expectedOutput = inputString;

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	public void testTokenWithSpacesAreUnchanged() throws Exception
	{
		String inputString = "@HELLO WORLD@";
		String expectedOutput = inputString;

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	public void testTokenStartCharactersCanPrecedeAValidToken() throws Exception
	{
		String inputString = "@@HELLO.WORLD@@";
		String expectedOutput = inputString;

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	public void testTokensCanBeReplacedWithAnEmptyString() throws Exception
	{
		String inputString = "123@TOKEN.EMPTY.STRING@45";
		String expectedOutput = "12345";
		when(mockTokenFinder.findTokenValue("TOKEN.EMPTY.STRING")).thenReturn("");

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
		verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.EMPTY.STRING");
	}

	@Test
	public void testTokensCannotSpanSeveralLines() throws Exception
	{
		String inputString = "123@MY\n.TOKEN@456";
		String expectedOutput = inputString;

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	public void testInputStringCanContainASingleLiteralToken() throws Exception
	{
		String inputString = "1234@test.caplin.com";
		String expectedOutput = inputString;

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
		verify(mockTokenFinder, never()).findTokenValue(anyString());
	}

	@Test
	public void testEmptyTokensAreNotReplaced() throws Exception
	{
		String inputString = "hello world @@";
		String expectedOutput = inputString;

		String actualOutput = doFilter(inputString, mockTokenFinder);
		assertEquals(expectedOutput, actualOutput);
	}

	@Test
	public void testExceptionIsThrownIfTokenNotFound() throws Exception
	{
		String inputString = "@TOKEN.HELLO@ @TOKEN.WORLD@!";
		when(mockTokenFinder.findTokenValue("TOKEN.HELLO")).thenReturn("hello");
		when(mockTokenFinder.findTokenValue("TOKEN.WORLD")).thenReturn(null);

		try
		{
			doFilter(inputString, mockTokenFinder);
			// if we reach this point then an exception has not been thrown - fail!
			fail("Expected exception to be thrown.");
		}
		catch (Exception ex)
		{
			System.err.println(ex);
			verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.HELLO");
			verify(mockTokenFinder, times(1)).findTokenValue("TOKEN.WORLD");
		}
	}

	private String doFilter(String inputString, JndiTokenFinder tokenFinder) throws IOException
	{
		StringReader inputReader = new StringReader(inputString);
		return tokeniser.replaceTokens(inputReader, tokenFinder, "").toString();
	}
}

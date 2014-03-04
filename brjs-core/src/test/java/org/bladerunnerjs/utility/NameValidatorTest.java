package org.bladerunnerjs.utility;

import static org.mockito.Mockito.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.name.UnableToAutomaticallyGenerateAppRequirePrefixException;
import org.bladerunnerjs.utility.NameValidator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class NameValidatorTest
{
	
	@Rule
	public ExpectedException exception = ExpectedException.none();


	/* lower case alphabet tests */
	
	@Test
	public void testContainsLowerCaseAlphabetCharactersWithLowerCase()
	{
		String[] args = {"lowercase"};
		assertTrue(NameValidator.legacyIsValidPackageName(args));
	}

	@Test
	public void testContainsLowerCaseAndNumbers()
	{
		String[] args = {"lowercase12345"};
		assertTrue(NameValidator.legacyIsValidPackageName(args));
	}
	
	@Test
	public void testStartsWithANumber()
	{
		String[] args = {"1lowercase"};
		assertFalse(NameValidator.legacyIsValidPackageName(args));
	}
	
	@Test
	public void testContainsLowerCaseAlphabetCharactersWithUpperCase()
	{
		String[] args = {"UPPERCASE"};
		assertFalse(NameValidator.legacyIsValidPackageName(args));
	}
	
	@Test
	public void testContainsLowerCaseAlphabetCharactersWithCamelCase()
	{
		String[] args = {"upperCase"};
		assertFalse(NameValidator.legacyIsValidPackageName(args));
	}
	
	@Test
	public void testContainsLowerCaseAlphabetCharactersWithNumber()
	{
		String[] args = {"containsnumber1"};
		assertTrue(NameValidator.legacyIsValidPackageName(args));
	}
	
	@Test
	public void testStartsWithNumber()
	{
		String[] args = {"9isanumber"};
		assertFalse(NameValidator.legacyIsValidPackageName(args));
	}
	
	@Test
	public void testContainsLowerCaseAlphabetCharactersWithLowerCaseSpecialCharacters()
	{
		String[] args = {"��redit"};
		assertFalse(NameValidator.legacyIsValidPackageName(args));
	}
	
	@Test
	public void testEmptyStringPackageName()
	{
		String[] args = {""};
		assertFalse(NameValidator.legacyIsValidPackageName(args));
	}
	
	/* alphanumeric, dash and underscore tests */
	
	@Test
	public void testContainsAlphaNumericDashAndUnderscoreWithLowerCase()
	{
		assertTrue(NameValidator.isValidDirectoryName("lowercase"));
	}
	
	@Test
	public void testContainsAlphaNumericDashAndUnderscoreWithUpperCase()
	{
		assertTrue(NameValidator.isValidDirectoryName("UPPERCASE"));
	}
	
	@Test
	public void testContainsAlphaNumericDashAndUnderscoreWithNumbers()
	{
		assertTrue(NameValidator.isValidDirectoryName("132123"));
	}
	
	@Test
	public void testContainsAlphaNumericDashAndUnderscoreWithDashAndUnderscore()
	{
		assertTrue(NameValidator.isValidDirectoryName("-_"));
	}
	
	@Test
	public void testContainsAlphaNumericDashAndUnderscoreWithSlash()
	{
		assertFalse(NameValidator.isValidDirectoryName("/"));
	}
	
	@Test
	public void testContainsAlphaNumericDashAndUnderscoreWithMixedCharacters()
	{
		assertTrue(NameValidator.isValidDirectoryName("abc-123_ABC"));
	}
	
	@Test
	public void testContainsAlphaNumericDashAndUnderscoreWithSpecialCharacters()
	{
		assertFalse(NameValidator.isValidDirectoryName("��redit"));
	}

	@Test
	public void testReservedNamespace()
	{
		assertFalse("1a", NameValidator.isValidRootPackageName("caplin"));
		assertTrue("1b", NameValidator.isValidRootPackageName("appns"));
	}
	
	@Test
	public void testJavascriptKeywords()
	{
		assertFalse("caplin", NameValidator.isValidPackageName("break"));
		assertFalse("default", NameValidator.isValidPackageName("default"));
		assertFalse("namespace", NameValidator.isValidPackageName("namespace"));
		assertFalse("interface", NameValidator.isValidPackageName("interface"));
		assertFalse("debugger", NameValidator.isValidPackageName("debugger"));
	}
	
	@Test
	public void testgetReservedNamespaceList()
	{
		String[] messageLines = NameValidator.getReservedNamespaces().split("\n");
		assertEquals(messageLines[0], "Reserved namespace(s): 'caplin', 'caplinx'");
		assertTrue(messageLines[2], messageLines[2].startsWith("Banned Namespaces/JavaScript keywords: 'abstract', 'as', 'boolean', 'break'"));
	}
	
	@Test
	public void testGenerateAppNamespace() throws UnableToAutomaticallyGenerateAppRequirePrefixException
	{
		App app = mock(App.class);
		when(app.getName()).thenReturn("my-App");
		assertEquals("myapp", NameValidator.generateRequirePrefixFromApp(app));
	}
	
	@Test
	public void testExceptionThrownIfNamespaceCantBeAutomaticallyDetirmined() throws UnableToAutomaticallyGenerateAppRequirePrefixException
	{
		App app = mock(App.class);
		when(app.getName()).thenReturn("my�App");
		exception.expectMessage( startsWith("Unable to automatically calculate app namespace") );
		NameValidator.generateRequirePrefixFromApp(app);
	}
}

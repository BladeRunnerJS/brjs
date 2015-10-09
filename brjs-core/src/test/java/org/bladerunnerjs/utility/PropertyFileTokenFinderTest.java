package org.bladerunnerjs.utility;

import org.bladerunnerjs.appserver.util.TokenFinder;
import org.bladerunnerjs.appserver.util.TokenReplacementException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PropertyFileTokenFinderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private TokenFinder tokenFinder;
    private File environmentFilesRoot;

    @Before
    public void setup() throws IOException {
        environmentFilesRoot = FileUtils.createTemporaryDirectory(this.getClass(), "environments");
    }

    @Test
    public void defaultEnvironmentFileIsUsedForPropertiesIfNoOtherIsSpecified() throws Exception {
        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot);
        createDefaultEnvironmentProperty("foo", "bar");
        assertEquals("bar", tokenFinder.findTokenValue("foo"));
    }

    @Test
    public void environmentFileIsUsedIfSpecified() throws Exception {
        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot, "env1");
        createDefaultEnvironmentProperty("foo", "bar");
        createEnvironmentProperty("env1", "foo", "notbar");
        assertEquals("notbar", tokenFinder.findTokenValue("foo"));
    }

    @Test
    public void correctEnvironmentFileIsUsedIfOthersExist() throws Exception {
        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot, "env1");
        createDefaultEnvironmentProperty("foo", "bar");
        createEnvironmentProperty("env1", "foo", "notbar");
        createEnvironmentProperty("env2", "foo", "this");
        createEnvironmentProperty("dev", "foo", "that");
        assertEquals("notbar", tokenFinder.findTokenValue("foo"));
    }

    @Test
    public void multipleOrderedEnvironmentsCanBeUsed() throws Exception {
        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot, "env1", "env2", "env4");
        createDefaultEnvironmentProperty("foo", "bar");
        createEnvironmentProperty("env1", "foo", "bar1");
        createEnvironmentProperty("env2", "foo", "bar2");
        createEnvironmentProperty("env2", "foo", "bar3");
        createEnvironmentProperty("env4", "foo", "bar4");
        assertEquals("bar4", tokenFinder.findTokenValue("foo"));
    }

    @Test
    public void defaultEnvironmentIsIgnoredIfItDoesntExist() throws Exception {
        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot, "env1");
        createEnvironmentProperty("env1", "foo", "notbar");
        assertEquals("notbar", tokenFinder.findTokenValue("foo"));
    }

    @Test
    public void propertyValuesCanBeEmpty() throws Exception {
        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot);
        createDefaultEnvironmentProperty("foo", "");
        assertEquals("", tokenFinder.findTokenValue("foo"));
    }

    @Test
    public void ifThePropertyDoesntExistAnExceptionIsThrown() throws Exception {
        exception.expect(TokenReplacementException.class);
        exception.expectMessage("The token finder 'PropertyFileTokenFinder' could not find a replacement for the token 'foo'.");

        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot);
        createDefaultEnvironmentProperty("anotherToken", "");
        tokenFinder.findTokenValue("foo");
    }

    @Test
    public void ifThePropertyFileDoesntExistAnExceptionIsThrown() throws Exception {
        exception.expect(TokenReplacementException.class);
        exception.expectMessage("The token finder 'PropertyFileTokenFinder' could not find a replacement for the token 'foo'.");

        tokenFinder = new PropertyFileTokenFinder(environmentFilesRoot);
        tokenFinder.findTokenValue("foo");
    }



    private void createEnvironmentProperty(String environment, String key, String value) throws IOException {
        File environmentFile = new File(environmentFilesRoot, environment+".properties");
        org.apache.commons.io.FileUtils.write(environmentFile, key + "=" + value + "\n");
    }

    private void createDefaultEnvironmentProperty(String key, String value) throws IOException {
        createEnvironmentProperty("default", key, value);
    }

}

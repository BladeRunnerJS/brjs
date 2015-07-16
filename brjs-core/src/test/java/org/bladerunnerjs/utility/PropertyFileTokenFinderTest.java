package org.bladerunnerjs.utility;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.appserver.util.TokenFinder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PropertyFileTokenFinderTest {

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


    private void createEnvironmentProperty(String environment, String key, String value) throws IOException {
        File environmentFile = new File(environmentFilesRoot, environment+".properties");
        org.apache.commons.io.FileUtils.write(environmentFile, key + "=" + value + "\n");
    }

    private void createDefaultEnvironmentProperty(String key, String value) throws IOException {
        createEnvironmentProperty("default", key, value);
    }

}

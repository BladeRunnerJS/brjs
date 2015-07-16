package org.bladerunnerjs.utility;

import org.bladerunnerjs.appserver.util.NoTokenFoundException;
import org.bladerunnerjs.appserver.util.TokenFinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertyFileTokenFinder implements TokenFinder {

    public static final String DEFAULT_ENVIRONMENT = "default";

    private final List<String> environments;
    private final File environmentFilesRoot;

    public PropertyFileTokenFinder(File environmentFilesRoot) {
        this(environmentFilesRoot, null);
    }

    public PropertyFileTokenFinder(File environmentFilesRoot, String environment) {
        this.environmentFilesRoot = environmentFilesRoot;
        environments = (environment != null && environment.length() > 0) ? Arrays.asList(DEFAULT_ENVIRONMENT, environment) : Arrays.asList(DEFAULT_ENVIRONMENT);
    }

    @Override
    public String findTokenValue(String tokenName) throws NoTokenFoundException {
        String value = null;
        for (String environment : environments) {
            String environmentValue = null;
            try {
                environmentValue = getValueFromEnvironmentProperties(environment, tokenName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (environmentValue != null) {
                value = environmentValue;
            }
        }
        if (value == null) {
            throw new NoTokenFoundException(tokenName, this.getClass());
        }
        return value;
    }

    private String getValueFromEnvironmentProperties(String environment, String key) throws IOException {
        Properties props = new Properties();
        props.load( new FileReader(getPropertiesFileForEnvironment(environment)) );
        return (String) props.get(key);
    }

    private File getPropertiesFileForEnvironment(String environment) {
        return new File(environmentFilesRoot, environment+".properties");
    }

}

package org.bladerunnerjs.utility;

import org.bladerunnerjs.appserver.util.TokenReplacementException;
import org.bladerunnerjs.appserver.util.TokenFinder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertyFileTokenFinder implements TokenFinder {

    public static final String DEFAULT_ENVIRONMENT = "default";

    private final List<String> environments = new ArrayList<>();
    private final File environmentFilesRoot;

    public PropertyFileTokenFinder(File environmentFilesRoot) {
        this(environmentFilesRoot, new String[0]);
    }

    public PropertyFileTokenFinder(File environmentFilesRoot, String... environments) {
        this.environmentFilesRoot = environmentFilesRoot;
        this.environments.add(DEFAULT_ENVIRONMENT);
        this.environments.addAll( Arrays.asList(environments) );
    }

    @Override
    public String findTokenValue(String tokenName) throws TokenReplacementException {
        String value = null;
        for (String environment : environments) {
            String environmentValue = environmentValue = getValueFromEnvironmentProperties(environment, tokenName);
            if (environmentValue != null) {
                value = environmentValue;
            }
        }
        if (value == null) {
            throw new TokenReplacementException(tokenName, this.getClass());
        }
        return value;
    }

    private String getValueFromEnvironmentProperties(String environment, String key) throws TokenReplacementException {
        Properties props = new Properties();
        try {
            File environmentFile = getPropertiesFileForEnvironment(environment);
            if (!environmentFile.isFile()) {
                return null;
            }
            props.load( new FileReader(environmentFile) );
        } catch (IOException ex) {
            throw new TokenReplacementException(key, this.getClass(), ex);
        }
        return (String) props.get(key);
    }

    private File getPropertiesFileForEnvironment(String environment) {
        return new File(environmentFilesRoot, environment+".properties");
    }

}

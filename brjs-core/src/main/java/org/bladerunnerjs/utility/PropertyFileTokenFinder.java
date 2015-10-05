package org.bladerunnerjs.utility;

import org.bladerunnerjs.appserver.util.TokenReplacementException;
import org.bladerunnerjs.appserver.util.TokenFinder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PropertyFileTokenFinder implements TokenFinder {

    public static final String DEFAULT_ENVIRONMENT = "default";

    private final Map<String,Properties> environmentProperties = new LinkedHashMap<>();
    private final Set<String> environmentsToLoad = new LinkedHashSet<>();

	private File environmentFilesRoot;

    public PropertyFileTokenFinder(File environmentFilesRoot) {
        this(environmentFilesRoot, new String[0]);
    }

    /**
     * Construct a PropertyFileTokenFinder with the given environments file root and environments to load.
     * The environment last in the list is considered to have highest priority, so if environment n (where n is the last in the list) 
     * has a property it will be used otherwise n-1 will be checked for a property and so on. 
     * 
     * @param environmentFilesRoot
     * @param environments
     */
    public PropertyFileTokenFinder(File environmentFilesRoot, String... environments) {
    	this.environmentFilesRoot = environmentFilesRoot;
    	
    	List<String> environmentsList = new ArrayList<>( Arrays.asList(environments) );
    	Collections.reverse(environmentsList); /* reverse the order so we can return the first valid environment we find */
    	
    	environmentsToLoad.addAll(environmentsList);
    	if (!environmentsToLoad.contains(DEFAULT_ENVIRONMENT)) {
    		environmentsToLoad.add(DEFAULT_ENVIRONMENT);
    	}
    }

    @Override
    public String findTokenValue(String tokenName) throws TokenReplacementException {
    	try {
    		if (environmentProperties.isEmpty()) {
    			loadAllEnvironmentProperties();
    		}
    	} catch (IOException ex) {
    		throw new TokenReplacementException(tokenName, this.getClass(), ex);
    	}
    	
        for (String environment : environmentProperties.keySet()) {
            String environmentValue = environmentProperties.get(environment).getProperty(tokenName);
            if (environmentValue != null) {
                return environmentValue;
            }
        }
    	throw new TokenReplacementException(tokenName, this.getClass());
    }
    
    private void loadAllEnvironmentProperties() throws IOException {
    	for (String environment : environmentsToLoad) {
        	environmentProperties.put(environment, getPropertiesForEnvironment(environmentFilesRoot, environment));
        }
    }

    private Properties getPropertiesForEnvironment(File environmentFilesRoot, String environment) throws IOException {
    	Properties props = new Properties();
        File environmentFile = getPropertiesFileForEnvironment(environmentFilesRoot, environment);
        if (environmentFile.isFile()) {
            props.load( new FileReader(environmentFile) );
        }
        return props;
    }
    
    private File getPropertiesFileForEnvironment(File environmentFilesRoot, String environment) {
        return new File(environmentFilesRoot, environment+".properties");
    }

}

package org.bladerunnerjs.api;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;

/**
 * An Asset is any file that lives within an App. Assets are responsible for transforming their input into a readable stream
 * (e.g. compiling CoffeeScript to JavaScript). They also provide string names (decoupled from their location on disk)
 * by which they can be referenced. This interface is used by classes that implement methods such as reading the file, 
 * calculating the require path of the Asset, or obtaining the MemoizedFile version of an Asset for monitoring it for changes.
 */

public interface Asset {
	
	/**
	 * The method returns a {@link Reader} object for obtaining the content of the Asset.
	 * 
	 * @return a Reader object for reading the current Asset's character stream
	 * @thors IOException if the Asset may not be read
	 */
	Reader getReader() throws IOException;
	
	/**
	 * The method returns a {@link MemoizedFile} object that provides similar methods to File and wraps a File object.
	 * Several of the methods' return values are 'memoized' and only regenerated if properties on the underlying file on disk change. 
	 * Changes are detected by using {@link MemoizedValue} and the {@link FileModificationRegistry}.
	 * 
	 * @return a MemoizedFile object for obtaining the Asset within the BRJS structure and monitoring it for changes.
	 */
	MemoizedFile file();
	
	/**
	 * The method returns the name of the Asset file.
	 * 
	 * @return a String object representing the name of the Asset file.
	 */
	String getAssetName();
	
	/**
	 * The method returns the path to the Asset relative to the {@link App} that comprises it.
	 * 
	 * @return a String object representing the path to the Asset file relative to the App that comprises it.
	 */
	String getAssetPath();
	
	/**
	 * The method returns the available require paths to the Asset. An Asset may have one or more valid require paths, as desired.
	 * This list will include the primary require path for the Asset.
	 * 
	 * @return a List of Strings object representing the available require paths to the Asset.
	 */
	List<String> getRequirePaths();
	
	/**
	 * The method returns the primary require path to the Asset, which is calculated based on the require prefix of its {@link AssetContainer} 
	 * and the Asset name.
	 * 
	 * @return a String object representing the primary require path to the Asset.
	 */
	String getPrimaryRequirePath();
	
	/**
	 * The method returns the AssetContainer in which the Asset is contained. An AssetContainer may be e.g. an {@link Aspect}, a {@link Workbench},
	 * a {@link TestPack}, a {@link Blade}, a {@link Bladeset} or a library.
	 * 
	 * @return an AssetContainer object that contains the Asset.
	 */
	AssetContainer assetContainer();
	
	/**
	 * In order to encourage modularity, BRJS does not allow lower level entities to depend on higher level entities. For example,
	 * a blade may not require an asset from the default aspect. This behaviour is called 'scope enforced' and may be set on a per 
	 * {@link AssetContainer} basis. The method returns true if the scope is enforced, meaning that warnings about such out of scope require paths
	 * will be logged, or false, if these will be ignored and retrieved.  
	 * 
	 * @return a boolean object stating whether eventual invalid require path warnings will be logged.
	 */
	boolean isScopeEnforced();
	
	/**
	 * BRJS distinguishes between Assets that may be required and those they may not. Those that may not include Assets on the
	 * root directory level or directories. The method returns true if the current Asset may be required and false otherwise.
	 * 
	 * @return a boolean object stating whether the Asset may be required by another one.
	 */
	boolean isRequirable();
}

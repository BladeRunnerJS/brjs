package org.bladerunnerjs.utility;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * This filter accepts <code>File</code>s that are directories.
 * <p>
 * For example, here is how to print out a list of the 
 * current directory's subdirectories:
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list( DirectoryFileFilter.INSTANCE );
 * for ( int i = 0; i &lt; files.length; i++ ) {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 * @since 1.0
 * @version $Id: DirectoryFileFilter.java 1304052 2012-03-22 20:55:29Z ggregory $
 *
 * @see FileFilterUtils#directoryFileFilter()
 */
public class FastDirectoryFileFilter extends AbstractFileFilter {

	
    /**
     * Singleton instance of directory filter.
     * @since 1.3
     */
    public static final IOFileFilter DIRECTORY = new FastDirectoryFileFilter();
    /**
     * Singleton instance of directory filter.
     * Please use the identical DirectoryFileFilter.DIRECTORY constant.
     * The new name is more JDK 1.5 friendly as it doesn't clash with other
     * values when using static imports.
     */
    public static final IOFileFilter INSTANCE = DIRECTORY;

    public static final  HashMap<File, Boolean> filePropertiesCache = new HashMap<File, Boolean>();
    public static final  HashMap<File, String> fileCanonicalCache = new HashMap<File, String>();
    
    /**
     * Checks to see if the file is a directory.
     *
     * @param file  the File to check
     * @return true if the file is a directory
     */
    @Override
    public boolean accept(File file) {
        return isDirectory(file);
    }
    
    public static boolean isDirectory(File file)
	{
 //   	AdhocTimer.enter("isDirectory", false);
 //   	boolean result =  file.isDirectory();
		boolean result = false;
		Boolean isDirectory = filePropertiesCache.get(file);
		if(isDirectory == null){
			result = file.isDirectory();
			filePropertiesCache.put(file, new Boolean(result));
		}else{
			result = isDirectory.booleanValue();
		}
		
//		AdhocTimer.exit("isDirectory", false);
		return result;
	}
    
    public static String getCanonicalPath(File file) throws IOException
   	{
//    	AdhocTimer.enter("getCanonicalPath", false);
 //   	String result = file.getCanonicalPath();;
   		String result = fileCanonicalCache.get(file);
   		if(result == null){
   			result = file.getCanonicalPath();
   			fileCanonicalCache.put(file, result);
   		}
//   		AdhocTimer.enter("getCanonicalPath", false);
   		return result;
   	}
    
    
    
    

}

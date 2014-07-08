package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.util.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FileInfo;

public class RelativePathUtility {
	
	public static String get(BRJS brjs, File basePath, File childPath) {
		String baseCanonicalPath,  childCanonicalPath;
		
		try {
			FileInfo baseFileInfo = brjs.getFileInfo(basePath);
			FileInfo childFileInfo = brjs.getFileInfo(childPath);
			baseCanonicalPath = baseFileInfo.canonicalPath();
			childCanonicalPath = childFileInfo.canonicalPath();
		} catch (NullPointerException ex) {
			//TODO: fix the chicken/egg issue that causes this NPE if RelaltivePath is used during the file modification service constructor
			baseCanonicalPath = getCanonicalPath(basePath);
			childCanonicalPath = getCanonicalPath(childPath);			
		}
		
		return getRelativePath( baseCanonicalPath, childCanonicalPath );
	}
	
	private static String getCanonicalPath(File path)
	{
		try {
			return path.getCanonicalPath();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	
	/**
     * Calculates the relative path between two files.
     * <p>
     * Implementation note: This function may throw an IOException if an I/O error occurs
     * because its use of the canonical pathname may require filesystem queries.
     * </p>
     *
     * @param fromFileCanonicalPath the <code>File</code> to calculate the path from
     * @param toFileCanonicalPath the <code>File</code> to calculate the path to
     * @return the relative path between the files
     * @throws Exception for undocumented reasons
     *
     */
	private static String getRelativePath(String fromFileCanonicalPath, String toFileCanonicalPath) {

        // build the path stack info to compare
        String[] fromPathStack = getPathStack(fromFileCanonicalPath);
        String[] toPathStack = getPathStack(toFileCanonicalPath);

        if (0 < toPathStack.length && 0 < fromPathStack.length) {
            if (!fromPathStack[0].equals(toPathStack[0])) {
                // not the same device (would be "" on Linux/Unix)

                return getPath(Arrays.asList(toPathStack));
            }
        } else {
            // no comparison possible
            return getPath(Arrays.asList(toPathStack));
        }

        int minLength = Math.min(fromPathStack.length, toPathStack.length);
        int same = 1; // Used outside the for loop

        // get index of parts which are equal
        for (;
             same < minLength && fromPathStack[same].equals(toPathStack[same]);
             same++) {
            // Do nothing
        }

        List<String> relativePathStack = new ArrayList<String>();

        // if "from" part is longer, fill it up with ".."
        // to reach path which is equal to both paths
        for (int i = same; i < fromPathStack.length; i++) {
            relativePathStack.add("..");
        }

        // fill it up path with parts which were not equal
        for (int i = same; i < toPathStack.length; i++) {
            relativePathStack.add(toPathStack[i]);
        }

        return getPath(relativePathStack);
    }

    /**
     * Gets all names of the path as an array of <code>String</code>s.
     *
     * @param path to get names from
     * @return <code>String</code>s, never <code>null</code>
     *
     * @since Ant 1.7
     */
	private static String[] getPathStack(String path) {
    	
    	//this is slightly faster that String.split()
        Object[] tokens = StringUtils.split(path, File.separatorChar).toArray();
        String[] rv = new String[tokens.length];
        System.arraycopy(tokens, 0, rv, 0, tokens.length);
        return rv;
    }

    /**
     * Gets path from a <code>List</code> of <code>String</code>s.
     *
     * @param pathStack <code>List</code> of <code>String</code>s to be concatenated as a path.
     * @return <code>String</code>, never <code>null</code>
     */
	private static String getPath(List<String> pathStack) {
        // can safely use '/' because Windows understands '/' as separator
        return getPath(pathStack, '/');
    }
    /**
     * Gets path from a <code>List</code> of <code>String</code>s.
     *
     * @param pathStack <code>List</code> of <code>String</code>s to be concated as a path.
     * @param separatorChar <code>char</code> to be used as separator between names in path
     * @return <code>String</code>, never <code>null</code>
     */
	private static String getPath(final List<String> pathStack, final char separatorChar) {
        final StringBuffer buffer = new StringBuffer();

        final Iterator<String> iter = pathStack.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
        }
        while (iter.hasNext()) {
            buffer.append(separatorChar);
            buffer.append(iter.next());
        }
        return buffer.toString();
    }
	
}

package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;


public interface AugmentedContentSourceModule extends SourceModule
{
	/**
	 * Get a reader that represents the content for this SourceModule without any extra content or changes that might be made 
	 * in the Reader returned by getReader().
	 * 
	 * @return the reader
	 */
	Reader getUnalteredContentReader() throws IOException;
}

package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;

//TODO: we must be able to think of a better name for this...
public interface AssetContentAlteringSourceModule extends SourceModule
{
	/**
	 * Get the base reader without any wrapping/content alteration
	 * @return
	 * @throws IOException 
	 */
	public Reader getBaseReader() throws IOException;
}

package org.bladerunnerjs.api.plugin;

import org.bladerunnerjs.utility.ContentPathParser;

public interface RoutableContentPlugin extends ContentPlugin {
	/**
	 * Returns the content-path parser that will be used to parse all requests for this plug-in.
	 * 
	 * <p>Although the parsing mechanism used by content plug-ins could be an internal implementation detail within each class, by exposing it we
	 * make it possible to create requests for external content plug-ins that are less brittle to change.</p>
	 * 
	 * @return The {@link ContentPathParser} used by this {@link ContentPlugin} to generate content paths
	 */
	ContentPathParser getContentPathParser();
}

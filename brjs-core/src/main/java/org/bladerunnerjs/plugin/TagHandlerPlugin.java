package org.bladerunnerjs.plugin;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.utility.ContentPathParser;

/**
 * Tag handler plug-ins allow BladeRunnerJS tags within index pages (e.g. <code>&lt;@bundle.js/@&gt;</code>) to be replaced with arbitrary content.
 * 
 * <p>Tag handlers are often used to cause the browser to make requests to a {@link ContentPlugin}, using the
 * {@link ContentPlugin#getContentPathParser} and {@link ContentPathParser#createRequest} methods to help generate valid content paths. Similar to
 * {@link ContentPlugin} instances, tag-handler plug-ins should be deterministic, in that they generate the same content each time they are run.</p>
 * 
 * <p>Although tag-handler plug-ins are designed to be usable both dynamically, and as part of a build (for flat-file export), a provision for generating different content in
 * development and production is provided, for cases where things need to be done in a more efficient manner in production, and in a more dev-friendly manner in
 * development. Developers that choose to exploit this facility should take care to ensure there is no chance of bugs being found in production, that can't also be observed
 * in development; a core tenet of BladeRunnerJS.</p>
 * 
 * <p>The following methods are <i>identifier-methods</i>, and may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()}
 * has been invoked:</p>
 * 
 * <ul>
 *   <li>{@link #getTagName}</li>
 *   <li>{@link #getGroupName}</li>
 * </ul>
 */
public interface TagHandlerPlugin extends Plugin {
	/**
	 * Returns the name of the tag this tag-handler provides.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 */
	String getTagName();
	
	/**
	 * Returns the group name of tag-handler plug-in, which allow composite content plug-ins that compose a number of related plug-ins.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 * 
	 * @return the group name if one exists, or <code>null</code> otherwise.
	 */
	String getGroupName();
	
	/**
	 * Writes out the generated content for the given tag, optimized for development.
	 */
	void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException;
	
	/**
	 * Writes out the generated content for the given tag, optimized for production.
	 */
	void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException;
}

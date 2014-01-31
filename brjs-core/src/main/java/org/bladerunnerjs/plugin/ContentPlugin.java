package org.bladerunnerjs.plugin;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.utility.ContentPathParser;

/**
 * Content plug-ins allow generated content to be returned by the application server in response to requests from the web browser. Where content
 * plug-ins differ from similar mechanisms employed on most other application servers is that they are required to support flat-file export, and
 * so must therefore have the following properties:</p>
 * 
 * <dl>
 *   <dt>Determinism</dt>
 *   <dd>The same response must always be returned given the same request and the same set of files on disk.</dd>
 *   
 *   <dt>Discoverability</dt>
 *   <dd>The complete list of valid content paths must be provided by content plug-ins on demand (using {@link #getValidDevContentPaths getValidDevContentPaths()} &
 *   {@link #getValidProdContentPaths getValidProdContentPaths()}), such that any requests from the browser that are not included in this list are considered to be an
 *   error.</dd>
 * </dl>
 * 
 * <p>Although content-plugins are designed to be usable both dynamically, and as part of a build (for flat-file export), a provision for generating different content in
 * development and production is provided, for cases where things need to be done in a more efficient manner in production, and in a more dev-friendly manner in
 * development. Developers that choose to exploit this facility should take care to ensure there is no chance of bugs being found in production, that can't also be observed
 * in development; a core tenet of BladeRunnerJS.</p>
 * 
 * <p>The following methods are <i>identifier-methods</i>, and may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has been invoked:</p>
 * 
 * <ul>
 *   <li>{@link #getRequestPrefix}</li>
 *   <li>{@link #getGroupName}</li>
 * </ul>
 */
public interface ContentPlugin extends Plugin {
	/**
	 * Returns the prefix that all requests for this content plug-in will begin with.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 */
	String getRequestPrefix();
	
	/**
	 * Returns the group name of content plug-in, which allow composite content plug-ins that compose a number of related plug-ins.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 * 
	 * @return the group name if one exists, or <code>null</code> otherwise.
	 */
	String getGroupName();
	
	/**
	 * Returns the content-path parser that will be used to parse all requests for this plug-in.
	 * 
	 * <p>Although the parsing mechanism used by content plug-ins could be an internal implementation detail within each class, by exposing it we
	 * make it possible to create requests for external content plug-ins that are less brittle to change.</p>
	 */
	ContentPathParser getContentPathParser();
	
	/**
	 * Write content for the given request.
	 * 
	 * @param contentPath The parsed content path created using the content path parser available from {@link #getContentPathParser}.
	 * @param bundleSet The bundle-set for the bundlable node to which this request is related to.
	 * @param os The output stream the content will be written to.
	 * 
	 * @throws BundlerProcessingException if a problem is encountered.
	 */
	void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException;
	
	/**
	 * Returns the list of valid content paths, when in development, for the given bundle-set and locale.
	 * 
	 * @param bundleSet The bundle-set for which content paths must be generated.
	 * @param locales The locale for which content paths must be generated.
	 * 
	 * @throws BundlerProcessingException if a problem is encountered.
	 */
	List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws BundlerProcessingException;
	
	/**
	 * Returns the list of valid content paths, when in production, for the given bundle-set and locale.
	 * 
	 * @param bundleSet The bundle-set for which content paths must be generated.
	 * @param locales The locale for which content paths must be generated.
	 * 
	 * @throws BundlerProcessingException if a problem is encountered.
	 */
	List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws BundlerProcessingException;
}

package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;

/**
 * <p>
 * Content plug-ins allow generated content to be returned by the application server in response to requests from the web browser. Where content
 * plug-ins differ from similar mechanisms employed on most other application servers is that they are required to support flat-file export, and
 * so must therefore have the following properties:</p>
 * 
 * <dl>
 *   <dt>Determinism</dt>
 *   <dd>The same response must always be returned given the same request and the same set of files on disk.</dd>
 *   
 *   <dt>Discoverability</dt>
 *   <dd>The complete list of valid content paths must be provided by content plug-ins on demand (using {@link #getValidContentPaths getValidDevContentPaths()}), 
 *   such that any requests from the browser that are not included in this list are considered to be an
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
 * </ul>
 * 
 * @see CompositeContentPlugin
 */
public interface ContentPlugin extends OrderedPlugin {
	/**
	 * Returns the prefix that all requests for this content plug-in will begin with.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 */
	String getRequestPrefix();
	
	/**
	 * Get a reader for the content generated for the given request.
	 * 
	 * @param contentPath The content path the request is being made for.
	 * @param bundleSet The bundle-set for the bundlable node to which this request is related to.
	 * @param contentAccessor The output stream the content will be written to.
	 * @param version TODO
	 * @throws ContentProcessingException if a problem is encountered.
	 */
	ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException;
	
	/**
	 * Returns the list of valid content paths for the given bundle-set and locale.
	 * 
	 * @param bundleSet The bundle-set for which content paths must be generated.
	 * @param requestMode An enum representing the 'mode' for this request - either dev or prod.
	 * @param locales The locale for which content paths must be generated.
	 * 
	 * @throws ContentProcessingException if a problem is encountered.
	 */
	List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException;
	List<String> getUsedContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException;
	
}

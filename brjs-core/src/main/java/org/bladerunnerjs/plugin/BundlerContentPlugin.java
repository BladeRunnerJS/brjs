package org.bladerunnerjs.plugin;

/**
 * Content plug-ins that are also bundlers should implement <code>BundlerContentPlugin</code> rather than {@link ContentPlugin}.
 * 
 * <p>In addition to the methods defined within {@link ContentPlugin}, bundler content plug-ins have an additional {@link #getMimeType} method
 * that allows them to be logically grouped with other bundler content plug-ins of the same type. This grouping is performed by composite
 * content plug-ins, that forward requests on to the relevant bundler content plug-ins in the group. This arrangement provides
 * the benefit that the browser can make single logical request for all JavaScript, for example, and will receive all applicable content,
 * regardless of how many JavaScript bundler content plug-ins are in use by an app. Additionally, composite content plug-ins also allow a
 * unified approach to minification, or the production of unified multi-level source maps.</p>
 * 
 * <p>The following methods are <i>identifier-methods</i>, and may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has been invoked:</p>
 * 
 * <ul>
 *   <li>{@link #getRequestPrefix}</li>
 *   <li>{@link #getMimeType}</li>
 * </ul>
 */
public interface BundlerContentPlugin extends ContentPlugin {
	/**
	 * Returns the mime-type of the bundler content plug-in.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 */
	String getMimeType();
}

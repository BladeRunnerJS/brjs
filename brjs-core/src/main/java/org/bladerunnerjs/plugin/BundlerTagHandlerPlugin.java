package org.bladerunnerjs.plugin;

/**
 * Tag-handler plug-ins that are also bundlers should implement <code>BundlerTagHandlerPlugin</code> rather than {@link TagHandlerPlugin}.
 * 
 * <p>In addition to the methods defined within {@link TagHandlerPlugin}, bundler tag-handler plug-ins have an additional {@link #getMimeType} method
 * that allows them to be logically grouped with other bundler tag-handler plug-ins of the same type. This grouping is performed by composite
 * tag-handler plug-ins, that forward requests on to the relevant bundler tag-handler plug-ins in the group. This arrangement provides
 * the benefit that the developer can use a single logical tag to include all JavaScript, for example, and will receive all applicable content,
 * regardless of how many JavaScript bundler tag-handler plug-ins are in use by an app.</p>
 * 
 * <p>The following methods are <i>identifier-methods</i>, and may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has been invoked:</p>
 * 
 * <ul>
 *   <li>{@link #getTagName}</li>
 *   <li>{@link #getMimeType}</li>
 * </ul>
 */
public interface BundlerTagHandlerPlugin extends TagHandlerPlugin {
	/**
	 * Returns the mime-type of the bundler tag-handler plug-in.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 */
	String getMimeType();
}

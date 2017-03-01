package org.bladerunnerjs.api.plugin;

/**
 * <p>
 * Some content plug-ins are part of composites that may be used to wrap their content within a larger bundle. The <code>CompositeContentPlugin</code>
 * interface should be used by all content plug-ins that are part of a larger composite plug-in, where the output of these plug-ins will not appear within
 * the final bundle created during the build step.</p>
 * 
 * <p>The following methods are <i>identifier-methods</i>, and may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has been invoked:</p>
 * 
 * <ul>
 *   <li>{@link #getCompositeGroupName}</li>
 * </ul>
 */
public interface CompositeContentPlugin extends RoutableContentPlugin {
	/**
	 * Returns the group name of content plug-in, which allow composite content plug-ins that compose a number of related plug-ins.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 * 
	 * @return the group name if one exists, or <code>null</code> otherwise.
	 */
	String getCompositeGroupName();
}

package org.bladerunnerjs.api.plugin;

import org.bladerunnerjs.api.BRJS;


/**
 * <code>Plugin</code> is the base interface for all plug-ins within BladeRunnerJS. Plug-ins are discovered automatically by BladeRunnerJS (using the
 * <a href="http://docs.oracle.com/javase/tutorial/sound/SPI-intro.html">SPI Mechanism</a> introduced in Java 6), and are constructed using a zero-arg
 * constructor. The {@link #setBRJS setBRJS()} method is used in lieu of a parameterized constructor, and is invoked before any non-<i>identifier methods</i>
 * (see below).
 * 
 * <p>Because plug-ins form part of the model, yet use the model to initialize themselves, an untenable circular dependency exists. To overcome this
 * problem, each concrete plug-in instance is wrapped inside a <a href="http://en.wikipedia.org/wiki/Lazy_loading#Virtual_proxy">virtual proxy</a> that
 * delays plug-in initialization until somebody attempts to actually use the plug-in. Since code interested in interacting with a subset of the plug-ins
 * will often need to query all of them to locate the ones it needs, certain <i>identifier-methods</i> are proxied through before the object's
 * {@link #setBRJS setBRJS()} method has been invoked, which plug-in authors must be aware of.</p>
 * 
 * <p>Another consequence of wrapping all plug-ins in a virtual proxy is that the <code>instanceof</code> operator and the <code>Object.getClass()</code>
 * method do not work as expected. The {@link #instanceOf instanceOf()} and {@link #getPluginClass} methods are provided to overcome these deficiencies.</p>
 */
public interface Plugin
{
	/**
	 * Invoked after construction, and before any non-<i>identifier methods</i> have been invoked, so plug-ins have a reference to the BladeRunnerJS model.
	 * 
	 * @param brjs The reference to the BladeRunnerJS model.
	 */
	public void setBRJS(BRJS brjs);
	
	/**
	 * Invoked during the invocation of {@link BRJS#close()}, offering plug-ins a chance to release any resources they may be holding on to.
	 */
	public void close();
	
	/**
	 * An alternative to Java's <code>instanceof</code> operator that is needed because BladeRunnerJS wraps plug-ins within <a href="https://en.wikipedia.org/wiki/Lazy_loading#Virtual_proxy">virtual proxy</a>
	 * wrappers, causing the native <code>instanceof</code> operator to become ineffective.
	 * 
	 * <p>Plug-ins are not expected to implement this method themselves, and should instead extend one of the {@link org.bladerunnerjs.api.plugin.base.AbstractPlugin}
	 * sub-classes (e.g. {@link org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin}).</p>
	 * 
	 * @param pluginInterface The interface which this object may, or may not be, an instance of.
	 * @return <code>true</code>, if this object is an instance of the given interface, and <code>false</code> otherwise.
	 */
	<P extends Plugin> boolean instanceOf(Class<P> pluginInterface);
	
	/**
	 * An alternative to Java's class casting mechanism that is needed because BladeRunnerJS wraps plug-ins within <a href="https://en.wikipedia.org/wiki/Lazy_loading#Virtual_proxy">virtual proxy</a>
	 * wrappers, preventing class casting from working.
	 * 
	 * <p>Plug-ins are not expected to implement this method themselves, and should instead extend one of the {@link org.bladerunnerjs.api.plugin.base.AbstractPlugin}
	 * sub-classes (e.g. {@link org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin}).</p>
	 * 
	 * @param pluginInterface The interface which this object may, or may not be, an instance of.
	 */
	<P extends Plugin> P castTo(Class<P> pluginInterface);
	
	/**
	 * An alternative to Java's <code>Object.getClass()</code> method that is needed because BladeRunnerJS wraps plug-ins within <a href="https://en.wikipedia.org/wiki/Lazy_loading#Virtual_proxy">virtual proxy</a>
	 * wrappers, causing the native <code>Object.getClass()</code> method to become ineffective.
	 * 
	 * <p>Plug-ins are not expected to implement this method themselves, and should instead extend one of the {@link org.bladerunnerjs.api.plugin.base.AbstractPlugin}
	 * sub-classes (e.g. {@link org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin}).</p>
	 * 
	 * @return A reference to the class of the underlying plug-in.
	 */
	Class<?> getPluginClass();
}

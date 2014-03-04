package org.bladerunnerjs.plugin;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.plugin.proxy.VirtualProxyPlugin;


/**
 * Model observer plug-ins observe events within the model using the {@link AbstractNode#addObserver} method, and act appropriately to the events
 * received.
 * 
 * <p>The <code>ModelObserverPlugin</code> class doesn't add any methods over what is already provided by {@link Plugin}, and unlike all other
 * plug-in types, is wrapped within a {@link VirtualProxyPlugin} class that immediately invokes {@link Plugin#setBRJS Plugin.setBRJS()} on the underlying plug-in,
 * rather than doing it lazily, guaranteeing that all generated events will be witnessed.</p>
 * 
 * <p>Model observer plug-ins have their {@link Plugin#setBRJS} method invoked before any of the other plug-in types have been made available on
 * the model, so as to prevent model observers from inadvertently causing other plug-in types to start initializing, which would lead to events
 * being missed by other model observer plug-ins that have yet to be initialized.
 */
public interface ModelObserverPlugin extends Plugin
{
}

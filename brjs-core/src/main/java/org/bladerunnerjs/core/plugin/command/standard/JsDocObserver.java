package org.bladerunnerjs.core.plugin.command.standard;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.observer.AbstractModelObserverPlugin;
import org.bladerunnerjs.core.plugin.observer.JsDocNodeObserver;
import org.bladerunnerjs.model.BRJS;

public class JsDocObserver extends AbstractModelObserverPlugin implements ModelObserverPlugin {
	@Override
	public void setBRJS(BRJS brjs) {	
		brjs.addObserver( new JsDocNodeObserver(brjs) );
	}
}

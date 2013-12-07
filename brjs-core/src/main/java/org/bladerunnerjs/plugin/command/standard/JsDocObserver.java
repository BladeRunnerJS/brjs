package org.bladerunnerjs.plugin.command.standard;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.observer.AbstractModelObserverPlugin;
import org.bladerunnerjs.plugin.observer.JsDocNodeObserver;

public class JsDocObserver extends AbstractModelObserverPlugin implements ModelObserverPlugin {
	@Override
	public void setBRJS(BRJS brjs) {	
		brjs.addObserver( new JsDocNodeObserver(brjs) );
	}
}

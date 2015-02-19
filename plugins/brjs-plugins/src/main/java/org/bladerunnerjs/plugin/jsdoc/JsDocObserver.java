package org.bladerunnerjs.plugin.jsdoc;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractModelObserverPlugin;

public class JsDocObserver extends AbstractModelObserverPlugin implements ModelObserverPlugin {
	@Override
	public void setBRJS(BRJS brjs) {	
		brjs.addObserver( new JsDocNodeObserver(brjs) );
	}
}

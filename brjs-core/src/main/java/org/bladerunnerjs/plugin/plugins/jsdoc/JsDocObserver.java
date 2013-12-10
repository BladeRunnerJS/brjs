package org.bladerunnerjs.plugin.plugins.jsdoc;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.base.AbstractModelObserverPlugin;

public class JsDocObserver extends AbstractModelObserverPlugin implements ModelObserverPlugin {
	@Override
	public void setBRJS(BRJS brjs) {	
		brjs.addObserver( new JsDocNodeObserver(brjs) );
	}
}

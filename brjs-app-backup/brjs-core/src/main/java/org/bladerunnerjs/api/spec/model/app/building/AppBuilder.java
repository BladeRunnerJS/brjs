package org.bladerunnerjs.api.spec.model.app.building;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;

/**
 * Used to specify the actions taken when a static or a war app is built.
 */

public interface AppBuilder
{
	abstract void build(App app, MemoizedFile target) throws ModelOperationException;	
}

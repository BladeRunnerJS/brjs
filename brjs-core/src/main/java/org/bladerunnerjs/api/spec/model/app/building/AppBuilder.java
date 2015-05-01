package org.bladerunnerjs.api.spec.model.app.building;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;


public interface AppBuilder
{
	abstract void build(App app, MemoizedFile target) throws ModelOperationException;	
}

package org.bladerunnerjs.model.app.building;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.exception.ModelOperationException;


public interface AppBuilder
{
	abstract void build(App app, MemoizedFile target) throws ModelOperationException;	
}

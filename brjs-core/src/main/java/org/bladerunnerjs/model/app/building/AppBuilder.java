package org.bladerunnerjs.model.app.building;

import java.io.File;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ModelOperationException;


public interface AppBuilder
{
	abstract void build(App app, File target) throws ModelOperationException;	
}

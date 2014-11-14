package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.memoization.MemoizedFile;

public interface NodeLocator
{
	File getNodeDir(MemoizedFile sourceDir);
}

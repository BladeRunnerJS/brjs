package org.bladerunnerjs.model.engine;

import java.util.List;

import org.bladerunnerjs.model.Theme;


public interface ThemeableNode
{
	List<Theme> themes();	
	Theme theme(String themeName);
}

package org.bladerunnerjs.model;


public interface PatchableSourceModule extends SourceModule
{
	void addPatch(SourceModulePatch patch);
}

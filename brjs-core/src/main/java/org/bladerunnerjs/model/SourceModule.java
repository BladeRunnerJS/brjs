package org.bladerunnerjs.model;

/**
 * A source file, typically JavaScript (.js) files that live in a 'src' directory.
 *
 */
public interface SourceModule extends SourceAsset {
	void addPatch(SourceModulePatch patch);
}

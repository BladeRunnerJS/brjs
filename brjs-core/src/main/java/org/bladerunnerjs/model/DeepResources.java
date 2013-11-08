package org.bladerunnerjs.model;

import java.io.File;

// TODO: this class needs to be written properly to create one ShallowResources for each dir it finds
public class DeepResources extends ShallowResources {
	public DeepResources(BRJS brjs, File dir) {
		super(brjs, dir);
	}
}

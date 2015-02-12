package org.bladerunnerjs.model.events;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.plugin.Event;


public class BundleSetCreatedEvent implements Event
{
	private BundleSet bundleSet;
	private long creationDuration;

	public BundleSetCreatedEvent(BundleSet bundleSet, long creationDuration) {
		this.bundleSet = bundleSet;
	}
	
	public BundleSet getBundleSet() {
		return bundleSet;
	}
	
	public long getCreationDuration() {
		return creationDuration;
	}
}

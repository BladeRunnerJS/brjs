package org.bladerunnerjs.core.plugin.bundlesource.js;

import java.io.IOException;
import java.io.Writer;

import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.TagAppender;

public class NullTagAppender implements TagAppender {
	@Override
	public void writePreTagContent(BundleSet bundleSet, Writer writer) throws IOException {
		// do nothing
	}
	
	@Override
	public void writePostTagContent(BundleSet bundleSet, Writer writer) throws IOException {
		// do nothing
	}
}

package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Writer;

public interface TagAppender {
	void writePreTagContent(BundleSet bundleSet, Writer writer) throws IOException;
	void writePostTagContent(BundleSet bundleSet, Writer writer) throws IOException;
}

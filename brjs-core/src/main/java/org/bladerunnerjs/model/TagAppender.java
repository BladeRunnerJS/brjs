package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface TagAppender {
	void writePreTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException;
	void writePostTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException;
}

package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface TagAppender {
	void writeTagContent(List<String> bundlerRequestPaths, Writer writer) throws IOException;
}

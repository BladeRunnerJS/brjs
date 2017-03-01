package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

public interface AssetReaderFactory {
	Reader createReader() throws IOException;
}

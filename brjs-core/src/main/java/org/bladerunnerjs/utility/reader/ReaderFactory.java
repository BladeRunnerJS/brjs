package org.bladerunnerjs.utility.reader;

import java.io.Reader;

public interface ReaderFactory {
	Reader createReader(Reader reader);
}

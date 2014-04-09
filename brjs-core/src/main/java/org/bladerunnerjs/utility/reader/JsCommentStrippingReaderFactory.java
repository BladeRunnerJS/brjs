package org.bladerunnerjs.utility.reader;

import java.io.Reader;

public class JsCommentStrippingReaderFactory implements ReaderFactory {
	@Override
	public Reader createReader(Reader reader) {
		return new JsCommentStrippingReader(reader, false);
	}
}

package org.bladerunnerjs.utility.reader;

import java.io.Reader;

public class JsCommentAndCodeBlockStrippingReaderFactory implements ReaderFactory {
	@Override
	public Reader createReader(Reader reader) {
		Reader commentStrippingReader = new JsCommentStrippingReader(reader, false);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader);
		Reader commentStrippingAndStringStrippingAndCodeBlockStrippingReader = new JsCodeBlockStrippingReader(commentStrippingAndStringStrippingReader);
		
		return commentStrippingAndStringStrippingAndCodeBlockStrippingReader;
	}
}

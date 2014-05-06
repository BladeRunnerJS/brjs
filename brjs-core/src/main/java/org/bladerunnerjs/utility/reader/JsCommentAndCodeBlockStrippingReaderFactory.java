package org.bladerunnerjs.utility.reader;

import java.io.Reader;

public class JsCommentAndCodeBlockStrippingReaderFactory implements ReaderFactory {
	
	private int codeBlockStrippingReaderStartDepth;

	public JsCommentAndCodeBlockStrippingReaderFactory(int codeBlockStrippingReaderStartDepth)
	{
		this.codeBlockStrippingReaderStartDepth = codeBlockStrippingReaderStartDepth;
	}
	
	@Override
	public Reader createReader(Reader reader) {
		Reader commentStrippingReader = new JsCommentStrippingReader(reader, false);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader);
		Reader commentStrippingAndStringStrippingAndCodeBlockStrippingReader = new JsCodeBlockStrippingReader(commentStrippingAndStringStrippingReader, codeBlockStrippingReaderStartDepth);
		
		return commentStrippingAndStringStrippingAndCodeBlockStrippingReader;
	}
}

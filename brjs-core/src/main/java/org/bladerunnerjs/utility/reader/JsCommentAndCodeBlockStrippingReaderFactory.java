package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.AssetContentAlteringSourceModule;

public class JsCommentAndCodeBlockStrippingReaderFactory implements AssetReaderFactory {
	
	private AssetContentAlteringSourceModule sourceModule;

	public JsCommentAndCodeBlockStrippingReaderFactory(AssetContentAlteringSourceModule sourceModule)
	{
		this.sourceModule = sourceModule;
	}
	
	@Override
	public Reader createReader() throws IOException {
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceModule.getBaseReader(), false);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader);
		Reader commentStrippingAndStringStrippingAndCodeBlockStrippingReader = new JsCodeBlockStrippingReader(commentStrippingAndStringStrippingReader);
		
		return commentStrippingAndStringStrippingAndCodeBlockStrippingReader;
	}
}

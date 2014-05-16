package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.AugmentedContentSourceModule;

public class JsCommentAndCodeBlockStrippingReaderFactory implements AssetReaderFactory {
	
	private AugmentedContentSourceModule sourceModule;

	public JsCommentAndCodeBlockStrippingReaderFactory(AugmentedContentSourceModule sourceModule)
	{
		this.sourceModule = sourceModule;
	}
	
	@Override
	public Reader createReader() throws IOException {
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceModule.getUnalteredContentReader(), false);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader);
		Reader commentStrippingAndStringStrippingAndCodeBlockStrippingReader = new JsCodeBlockStrippingReader(commentStrippingAndStringStrippingReader);
		
		return commentStrippingAndStringStrippingAndCodeBlockStrippingReader;
	}
}

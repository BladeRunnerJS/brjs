package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.model.SourceModule;

public class JsCommentAndCodeBlockStrippingReaderFactory implements AssetReaderFactory {
	
	private SourceModule sourceModule;

	public JsCommentAndCodeBlockStrippingReaderFactory(SourceModule sourceModule)
	{
		this.sourceModule = sourceModule;
	}
	
	@Override
	public Reader createReader() throws IOException {
		Reader reader;
		if (sourceModule instanceof AugmentedContentSourceModule)
		{
			reader = ((AugmentedContentSourceModule) sourceModule).getUnalteredContentReader();
		}
		else
		{
			reader = sourceModule.getReader();
		}
		
		Reader commentStrippingReader = new JsCommentStrippingReader(reader, false);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader);
		Reader commentStrippingAndStringStrippingAndCodeBlockStrippingReader = new JsCodeBlockStrippingReader(commentStrippingAndStringStrippingReader);
		
		return commentStrippingAndStringStrippingAndCodeBlockStrippingReader;
	}
}

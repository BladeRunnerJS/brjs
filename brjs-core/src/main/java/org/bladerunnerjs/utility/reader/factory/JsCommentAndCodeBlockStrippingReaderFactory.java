package org.bladerunnerjs.utility.reader.factory;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsStringStrippingReader;

public class JsCommentAndCodeBlockStrippingReaderFactory implements AssetReaderFactory {
	
	private AugmentedContentSourceModule sourceModule;

	public JsCommentAndCodeBlockStrippingReaderFactory(AugmentedContentSourceModule sourceModule)
	{
		this.sourceModule = sourceModule;
	}
	
	@Override
	public Reader createReader(CharBufferPool pool) throws IOException {
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceModule.getUnalteredContentReader(), false, pool);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader, pool);
		Reader commentStrippingAndStringStrippingAndCodeBlockStrippingReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingAndStringStrippingReader, pool);
		
		return commentStrippingAndStringStrippingAndCodeBlockStrippingReader;
	}
}

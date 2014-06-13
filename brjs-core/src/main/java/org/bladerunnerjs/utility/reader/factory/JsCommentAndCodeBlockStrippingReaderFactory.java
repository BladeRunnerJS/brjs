package org.bladerunnerjs.utility.reader.factory;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
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
	public Reader createReader() throws IOException {
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceModule.getUnalteredContentReader(), false);
		Reader commentStrippingAndStringStrippingReader = new JsStringStrippingReader(commentStrippingReader);
		Reader commentStrippingAndStringStrippingAndCodeBlockStrippingReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingAndStringStrippingReader);
		
		return commentStrippingAndStringStrippingAndCodeBlockStrippingReader;
	}
}

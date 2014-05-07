package org.bladerunnerjs.utility.reader;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsSourceModule;

public class JsCommentAndCodeBlockStrippingReaderFactory implements AssetReaderFactory {
	
	private SourceModule sourceModule;

	public JsCommentAndCodeBlockStrippingReaderFactory(SourceModule sourceModule)
	{
		this.sourceModule = sourceModule;
	}
	
	@Override
	public Reader createReader() throws IOException {
		Reader reader;
		if (sourceModule instanceof NamespacedJsSourceModule)
		{
			reader = ((NamespacedJsSourceModule) sourceModule).getBaseReader();
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

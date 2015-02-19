package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.model.AugmentedContentSourceModule;
import org.bladerunnerjs.utility.reader.AssetReaderFactory;
import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsModuleExportsStrippingReader;

public class NamespacedJsPostExportDefineTimeDependenciesReader extends Reader {
	private Reader namespacedJsPostExportDefineTimeDependenciesReader;
	
	public NamespacedJsPostExportDefineTimeDependenciesReader(AugmentedContentSourceModule sourceModule) throws IOException
	{
		CharBufferPool pool = sourceModule.assetLocation().root().getCharBufferPool();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceModule.getUnalteredContentReader(), false, pool);
		Reader codeBlockStrippingReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingReader, pool);
		namespacedJsPostExportDefineTimeDependenciesReader = new JsModuleExportsStrippingReader(codeBlockStrippingReader, pool, false);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return namespacedJsPostExportDefineTimeDependenciesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		namespacedJsPostExportDefineTimeDependenciesReader.close();
	}
	
	static class Factory implements AssetReaderFactory {
		
		private AugmentedContentSourceModule sourceModule;

		public Factory(AugmentedContentSourceModule sourceModule)
		{
			this.sourceModule = sourceModule;
		}
		
		@Override
		public Reader createReader() throws IOException {
			return new NamespacedJsPostExportDefineTimeDependenciesReader(sourceModule);
		}
	}
	
}

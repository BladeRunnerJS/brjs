package org.bladerunnerjs.plugin.plugins.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsModuleExportsStrippingReader;


public class CommonJsPreExportDefineTimeDependenciesReader extends Reader 
{

	private Reader defineTimeDependencesReader;

	public CommonJsPreExportDefineTimeDependenciesReader(CommonJsSourceModule sourceModule) throws IOException {
		CharBufferPool pool = sourceModule.assetLocation().root().getCharBufferPool();
		Reader sourceReader = sourceModule.getUnalteredContentReader();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceReader, false, pool);
		Reader codeBlockStrippingReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingReader , pool); 
		defineTimeDependencesReader = new JsModuleExportsStrippingReader(codeBlockStrippingReader, pool);		
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return defineTimeDependencesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		defineTimeDependencesReader.close();
	}
	
}
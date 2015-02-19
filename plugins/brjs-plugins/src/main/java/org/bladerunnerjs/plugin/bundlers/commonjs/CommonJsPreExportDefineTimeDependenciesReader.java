package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.utility.reader.CharBufferPool;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;
import org.bladerunnerjs.utility.reader.JsModuleExportsStrippingReader;

// TODO: merge this class with CommonJsPostExportDefineTimeDependenciesReader
public class CommonJsPreExportDefineTimeDependenciesReader extends Reader 
{

	private Reader preExportDefineTimeDependencesReader;

	public CommonJsPreExportDefineTimeDependenciesReader(CommonJsSourceModule sourceModule) throws IOException {
		CharBufferPool pool = sourceModule.assetLocation().root().getCharBufferPool();
		Reader sourceReader = sourceModule.getUnalteredContentReader();
		Reader commentStrippingReader = new JsCommentStrippingReader(sourceReader, false, pool);
		Reader codeBlockStrippingReader = new JsCodeBlockStrippingDependenciesReader(commentStrippingReader, pool);
		preExportDefineTimeDependencesReader = new JsModuleExportsStrippingReader(codeBlockStrippingReader, pool, true);
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return preExportDefineTimeDependencesReader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException
	{
		preExportDefineTimeDependencesReader.close();
	}
	
}
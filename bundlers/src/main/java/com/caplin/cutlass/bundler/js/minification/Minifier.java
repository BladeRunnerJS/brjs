package com.caplin.cutlass.bundler.js.minification;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface Minifier
{
	public void minifySourceCode(List<File> sourceFiles, Writer writer, int offsetLine, int offsetIndex) throws IOException;
	
	public void writeSourceMap(Writer outputWriter) throws IOException;
}

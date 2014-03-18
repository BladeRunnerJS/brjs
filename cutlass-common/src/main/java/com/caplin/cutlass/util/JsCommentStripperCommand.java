package com.caplin.cutlass.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.utility.JsCommentStrippingReader;

public class JsCommentStripperCommand
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		File sourceDir = new File(args[0]);
		File processedDir = new File(args[1]);
		
		stripAllFiles(sourceDir, processedDir);
	}
	
	private static void stripAllFiles(File sourceDir, File processedDir) throws IOException
	{
		File[] sourceFiles = sourceDir.listFiles();
		
		for(File sourceFile : sourceFiles)
		{
			File processedFile = null;
			processedFile = new File(processedDir, sourceFile.getName());
			
			if(sourceFile.isDirectory() && !sourceFile.isHidden())
			{
				stripAllFiles(sourceFile, processedFile);
			}
			else
			{
				processedFile.getParentFile().mkdirs();
				
				try(Reader fileReader = new JsCommentStrippingReader(new BufferedReader(new FileReader(sourceFile)), true);
					Writer fileWriter = new FileWriter(processedFile))
				{
					IOUtils.copy(fileReader, fileWriter);
				}
				catch(IOException e)
				{
					throw new IOException("Error while stripping comments from '" + sourceFile.getAbsolutePath() +
						"' (being copied to '" + processedFile.getAbsolutePath() + "')", e);
				}
			}
		}
	}
}
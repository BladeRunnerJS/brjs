package com.caplin.cutlass.bundler.xml.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.caplin.cutlass.util.FileUtility;

public class XmlDocumentLocator {
	
	public static List<File> locateXmlDocuments(final List<String> directories, String contextBasePath) throws IOException {
		return locateXmlDocuments(directories, contextBasePath, new String[0]);
	}
	
	public static List<File> locateXmlDocuments(final List<File> directories) throws IOException {
		return locateXmlDocuments(directories, new String[0]);
	}
	
	public static List<File> locateXmlDocuments(final List<File> directories, String[] excludeDirs) throws IOException {
		List<String> strDirectories = new ArrayList<String>();
		for(File directory : directories) {
			strDirectories.add(directory.getAbsolutePath());
		}
		if (strDirectories.size() == 0) {
			strDirectories.add("");
		}
		return locateXmlDocuments(strDirectories, "", excludeDirs);
	}
	
	public static List<File> locateXmlDocuments(final List<String> directories, String contextBasePath, String[] excludePaths) throws IOException {
		// make the current directory the default if none are provided
		if(directories.size() == 0) {
			directories.add(".");
		}
		contextBasePath = (contextBasePath == null) ? "" : contextBasePath;
		List<File> xmlDocuments = new ArrayList<File>();
		
		for(String directoryName : directories) {
			File directory = new File(directoryName);
			if (contextBasePath.length() > 0 && !directory.isAbsolute()) {
				directory = new File(contextBasePath + directoryName);
			}
			if(directory.isDirectory() && !directory.isHidden()) {
				addXmlDocumentsToList(directory, xmlDocuments, excludePaths);				
			}
		}
		return xmlDocuments;
	}

	private static boolean fileOrDirIsExcluded(File directory, String[] excludePaths) {
		String directoryName = directory.getName();
		for (String exclude : excludePaths) {
			if (directoryName.equals(exclude)) {
				return true;
			}
		}
		return false;
	}

	private static void addXmlDocumentsToList(final File directory, final List<File> xmlDocuments, String[] excludePaths) {
		File[] directoryContents = FileUtility.sortFiles(directory.listFiles());
		for(File file : directoryContents) {
			if(file.isDirectory() && !fileOrDirIsExcluded(file,excludePaths)) {
				addXmlDocumentsToList(file, xmlDocuments, excludePaths);
			}
			else if(file.getName().endsWith(".xml") && !fileOrDirIsExcluded(file,excludePaths)) {
				xmlDocuments.add(file);
			}
		}
	}
}

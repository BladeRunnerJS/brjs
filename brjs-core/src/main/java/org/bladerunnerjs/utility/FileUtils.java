package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.Node;

public class FileUtils {	
	
	public static File createTemporaryFile(Class<?> testClass, String... suffixes) throws IOException
	{		
		String folderName = "brjs-"+testClass.getSimpleName()+StringUtils.join(suffixes);
		final File tempFile = File.createTempFile(folderName, "");
		Runtime.getRuntime().addShutdownHook(new DeleteTempFileShutdownHook(tempFile));
		return tempFile;
	}
	
	public static File createTemporaryDirectory(Class<?> testClass, String... subFolderName) throws IOException
	{		
		final File tempDir = createTemporaryFile(testClass);
		tempDir.delete();
		tempDir.mkdir();
		
		if (subFolderName.length > 0) {
			String joinedSubFolderName = StringUtils.join(subFolderName,"-");
			if (joinedSubFolderName.contains("/"))
			{
				throw new IOException("subFolderName can't contain a /");
			}
			File subFolder = new File(tempDir, joinedSubFolderName);
			subFolder.mkdir();
			return subFolder;
		}
		
		return tempDir;
	}
	
	
	
	/** 
	 * Deletes directories from deepest to shallowest to prevent file locking issues caused by the Watch Service on Windows 
	 * (see https://stackoverflow.com/questions/6255463/java7-watchservice-access-denied-error-trying-to-delete-recursively-watched-ne) 
	 */ 
	public static void deleteDirectoryFromBottomUp(File dir) throws IOException
	{
		if (dir.isFile())
		{
			throw new IOException("Expected as dir as an argument, got a file");
		}
		
		File[] files = dir.listFiles();
		if (files == null)
		{
			return;
		}
		
		for (File child : files)
		{
			if (child.isDirectory())
			{
				deleteDirectoryFromBottomUp(child);
			}
			dir.delete();
		}
	}
	
	public static void moveDirectoryContents(BRJS brjs, File srcDir, File destDir) throws IOException {
		moveDirectoryContents(brjs, srcDir, destDir, true);
	}
	
	private static void moveDirectoryContents(BRJS brjs, File srcDir, File destDir, boolean incrementVersions) throws IOException {
		if(!destDir.exists()) {
			org.apache.commons.io.FileUtils.moveDirectory(srcDir, destDir);
		}
		else {
			for(File srcFile : srcDir.listFiles()) {
				File destFile = new File(destDir, srcFile.getName());
				
				if(!destFile.exists()) {
					org.apache.commons.io.FileUtils.moveToDirectory(srcFile, destDir, false);
				}
				else {
					if(srcFile.isFile()) {
						destFile.delete();
						org.apache.commons.io.FileUtils.moveToDirectory(srcFile, destDir, false);
					}
					else {
						moveDirectoryContents(brjs, srcFile, destFile, false);
					}
				}
			}
		}
		if (incrementVersions) {
			brjs.getFileModificationRegistry().incrementChildFileVersions(destDir);
		}
	}
	
	public static String getOS() {
		String osNameProperty = System.getProperty("os.name").toLowerCase();
		String os;
		
		if(osNameProperty.startsWith("linux")) {
			os = "linux";
		}
		else if(osNameProperty.startsWith("mac")) {
			os = "mac";
		}
		else if(osNameProperty.startsWith("windows")) {
			os = "windows";
		}
		else {
			os = "unix";
		}
		
		return os;
	}

	
	public static void cleanDirectory(Node brjsNode, File dir) throws IOException { 
		cleanDirectory(brjsNode.root().getMemoizedFile(dir)); 
	}
	public static void cleanDirectory(MemoizedFile dir) throws IOException
	{
		org.apache.commons.io.FileUtils.cleanDirectory(dir.getUnderlyingFile());
		dir.incrementChildFileVersions();
	}

	public static void copyFile(Node brjsNode, File srcFile, File destFile) throws IOException { 
		copyFile(brjsNode.root().getMemoizedFile(srcFile), brjsNode.root().getMemoizedFile(destFile)); 
	}
	public static void copyFile(Node brjsNode, MemoizedFile srcFile, File destFile) throws IOException {
		copyFile(srcFile, brjsNode.root().getMemoizedFile(destFile));
	}
	public static void copyFile(Node brjsNode, File srcFile, MemoizedFile destFile) throws IOException {
		copyFile(brjsNode.root().getMemoizedFile(srcFile), destFile);		
	}
	public static void copyFile(MemoizedFile srcFile, MemoizedFile destFile) throws IOException {
		org.apache.commons.io.FileUtils.copyFile(srcFile.getUnderlyingFile(), destFile.getUnderlyingFile());
		destFile.incrementFileVersion();
	}
	
	public static void moveDirectory(Node brjsNode, File srcFile, File destFile) throws IOException { 
		moveDirectory(brjsNode.root().getMemoizedFile(srcFile), brjsNode.root().getMemoizedFile(destFile)); 
	}
	public static void moveDirectory(Node brjsNode, MemoizedFile srcFile, File destFile) throws IOException {
		copyDirectory(srcFile, brjsNode.root().getMemoizedFile(destFile));
	}
	public static void moveDirectory(Node brjsNode, File srcFile, MemoizedFile destFile) throws IOException {
		moveDirectory(brjsNode.root().getMemoizedFile(srcFile), destFile);		
	}
	public static void moveDirectory(MemoizedFile srcFile, MemoizedFile destFile) throws IOException {
		org.apache.commons.io.FileUtils.moveDirectory(srcFile.getUnderlyingFile(), destFile.getUnderlyingFile());
		srcFile.incrementChildFileVersions();
		destFile.incrementChildFileVersions();
	}
	
	public static void copyDirectory(Node brjsNode, File srcFile, File destFile) throws IOException { 
		copyDirectory(brjsNode.root().getMemoizedFile(srcFile), brjsNode.root().getMemoizedFile(destFile)); 
	}
	public static void copyDirectory(Node brjsNode, MemoizedFile srcFile, File destFile) throws IOException {
		copyDirectory(srcFile, brjsNode.root().getMemoizedFile(destFile));
	}
	public static void copyDirectory(Node brjsNode, File srcFile, MemoizedFile destFile) throws IOException {
		copyDirectory(brjsNode.root().getMemoizedFile(srcFile), destFile);		
	}
	public static void copyDirectory(MemoizedFile srcFile, MemoizedFile destFile) throws IOException {
		org.apache.commons.io.FileUtils.copyDirectory(srcFile.getUnderlyingFile(), destFile.getUnderlyingFile());
		destFile.incrementChildFileVersions();
	}
	
	public static void copyDirectory(Node brjsNode, File srcFile, File destFile, IOFileFilter fileFilter) throws IOException { 
		copyDirectory(brjsNode.root().getMemoizedFile(srcFile), brjsNode.root().getMemoizedFile(destFile), fileFilter); 
	}
	public static void copyDirectory(Node brjsNode, MemoizedFile srcFile, File destFile, IOFileFilter fileFilter) throws IOException {
		copyDirectory(srcFile, brjsNode.root().getMemoizedFile(destFile), fileFilter);
	}
	public static void copyDirectory(Node brjsNode, File srcFile, MemoizedFile destFile, IOFileFilter fileFilter) throws IOException {
		copyDirectory(brjsNode.root().getMemoizedFile(srcFile), destFile, fileFilter);
	}
	public static void copyDirectory(MemoizedFile srcFile, MemoizedFile destFile, IOFileFilter fileFilter) throws IOException {
		org.apache.commons.io.FileUtils.copyDirectory(srcFile.getUnderlyingFile(), destFile.getUnderlyingFile(), fileFilter);
		destFile.incrementChildFileVersions();
	}
	
	public static boolean deleteQuietly(Node brjsNode, File file) {
		return deleteQuietly(brjsNode.root().getMemoizedFile(file));
	}
	public static boolean deleteQuietly(MemoizedFile file) {
		boolean deleted = org.apache.commons.io.FileUtils.deleteQuietly(file.getUnderlyingFile());
		file.incrementChildFileVersions();
		return deleted;
	}
	
	public static void deleteDirectory(Node brjsNode, File file) throws IOException {
		deleteDirectory(brjsNode.root().getMemoizedFile(file));
	}
	public static void deleteDirectory(MemoizedFile file) throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(file.getUnderlyingFile());
		file.incrementChildFileVersions();
	}
	
	public static void forceMkdir(Node brjsNode, File file) throws IOException {
		forceMkdir(brjsNode.root().getMemoizedFile(file));
	}
	public static void forceMkdir(MemoizedFile file) throws IOException {
		org.apache.commons.io.FileUtils.forceMkdir(file.getUnderlyingFile());
		file.incrementChildFileVersions();
	}
	
	public static void forceDelete(Node brjsNode, File file) throws IOException {
		forceDelete(brjsNode.root().getMemoizedFile(file));
	}
	public static void forceDelete(MemoizedFile file) throws IOException {
		org.apache.commons.io.FileUtils.forceDelete(file.getUnderlyingFile());
		file.incrementChildFileVersions();
	}
	
	
	public static void write(Node brjsNode, File file, String contents) throws IOException {
		write(brjsNode.root().getMemoizedFile(file), contents, null, false);
	}
	public static void write(Node brjsNode, File file, String contents, boolean append) throws IOException {
		write(brjsNode.root().getMemoizedFile(file), contents, null, append);
	}
	public static void write(Node brjsNode, File file, String contents, String encoding) throws IOException {
		write(brjsNode.root().getMemoizedFile(file), contents, encoding, false);
	}
	public static void write(Node brjsNode, File file, String contents, String encoding, boolean append) throws IOException {
		write(brjsNode.root().getMemoizedFile(file), contents, encoding, append);
	}
	public static void write(MemoizedFile file, String contents) throws IOException {
		write(file, contents, null, false);
	}
	public static void write(MemoizedFile file, String contents, boolean append) throws IOException {
		write(file, contents, null, append);
	}
	public static void write(MemoizedFile file, String contents, String encoding, boolean append) throws IOException {
		org.apache.commons.io.FileUtils.write(file.getUnderlyingFile(), contents, encoding, append);
		file.incrementFileVersion();
	}

	public static Collection<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter)
	{
		return org.apache.commons.io.FileUtils.listFiles(directory, fileFilter, dirFilter);
	}
	
	public static Collection<File> listFilesAndDirs(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter)
	{
		return org.apache.commons.io.FileUtils.listFilesAndDirs(directory, fileFilter, dirFilter);
	}
	
	public static Collection<File> listFiles(File directory, String[] extensions, boolean recurse)
	{
		return org.apache.commons.io.FileUtils.listFiles(directory, extensions, recurse);
	}
	
	
}

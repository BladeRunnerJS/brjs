package com.caplin.cutlass.util;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import com.caplin.cutlass.util.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.List;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

public class FileUtilityTest
{
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	
	private final String temporaryFilesDirectory = System.getProperty("java.io.tmpdir");
	private final File warFile = new File(temporaryFilesDirectory, "tempWar.war");
	private final File unwaredFolder = new File(temporaryFilesDirectory, "tempWar");
	private final File unwaredFolderContent = new File(unwaredFolder, "folderContainingSubfolders/folderA/fileA.txt");
	private final File unwaredFolderContentWithoutParentFolder = new File(unwaredFolder, "folderA/fileA.txt");

	private File tempDir;
	
	@After
	public void tearDown() throws Exception
	{
		if(unwaredFolder.exists())
		{
			FileUtility.deleteDirContent(unwaredFolder);
		}
		FileUtility.deleteDirAndContents(tempDir);
		
		assertTrue(!tempDir.exists());
		
		unwaredFolder.delete();
		warFile.delete();
	}

	@Before
	public void setUp() throws Exception
	{
		tempDir = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
	}
	
	@Test
	public void testCreateResourcesFromSdkTemplateDoesntCopyHiddenFiles() throws Exception
	{
		File folderContainingSubfoldersAndHiddenFiles = new File("src/test/resources/FileUtiltyTest/folderContainingSubfoldersAndHiddenFiles");
		
		FileUtility.createResourcesFromSdkTemplate(folderContainingSubfoldersAndHiddenFiles, tempDir, TrueFileFilter.TRUE);
		String temp = tempDir.getAbsolutePath();

		assertTrue(tempDir.listFiles().length == 2);
		assertTrue(new File(temp + "/aFolder/fileA.txt").exists());
		assertTrue(new File(temp + "/anotherFolder/fileB.txt").exists());
		assertTrue(new File(temp + "/anotherFolder/folderC/fileC.txt").exists());
	}
	
	@Test
	public void testCopyDirectoryContentsForFolderContainingSubfolders() throws Exception
	{
		File testFolderWithSubfolders = new File("src/test/resources/FileUtiltyTest/folderContainingSubfolders");
		FileUtility.copyDirectoryContents(testFolderWithSubfolders, tempDir);
		String temp = tempDir.getAbsolutePath();
		
		assertTrue(tempDir.listFiles().length == 3);
		assertTrue(new File(temp + "/folderA/fileA.txt").exists());
		assertTrue(new File(temp + "/folderB/fileB.txt").exists());
		assertTrue(new File(temp + "/folderB/folderC/fileC.txt").exists());
		assertTrue(new File(temp + "/example.conf").exists());
	}
	
	@Test
	public void testCopyDirectoryContentsToFolderStructureThatDoesntExist() throws Exception
	{
		File testFolderWithSubfolders = new File("src/test/resources/FileUtiltyTest/folderContainingSubfolders");
		File nonExistantTargetDirectory = new File(tempDir, "folder/that/does/not/exist");
		FileUtility.copyDirectoryContents(testFolderWithSubfolders, nonExistantTargetDirectory);
		
		assertTrue(nonExistantTargetDirectory.exists());
		assertTrue(nonExistantTargetDirectory.listFiles().length == 3);
	}
	
	@Test
	public void testRecursivelyDeleteEmptyDirectories() throws Exception
	{
		File emptyTopLevelDirectory = new File(tempDir, "folder");
		File emptySecondLevelDirectory = new File(emptyTopLevelDirectory, "that");
		File emptyThirdLevelDirectory = new File(emptySecondLevelDirectory, "does");
		File emptyFourthLevelDirectory = new File(emptyThirdLevelDirectory, "not");
		
		emptyFourthLevelDirectory.mkdirs();
		
		assertTrue(emptyTopLevelDirectory.exists());
		assertTrue(emptyThirdLevelDirectory.exists());
		assertTrue(emptyFourthLevelDirectory.exists());
		
		FileUtility.recursivelyDeleteEmptyDirectories(emptyTopLevelDirectory);
		
		assertFalse(emptyTopLevelDirectory.exists());
		assertFalse(emptyThirdLevelDirectory.exists());
		assertFalse(emptyFourthLevelDirectory.exists());
	}
	
	@Test
	public void testZipUnzipFolder() throws Exception
	{
		assertFalse(warFile.exists());
		
		File testFolderWithSubfolders = new File("src/test/resources/FileUtiltyTest/folderContainingSubfolders");
		FileUtility.zipFolder(testFolderWithSubfolders, warFile, false);
		
		assertTrue(warFile.exists());
		assertFalse(warFile.isDirectory());
		
		assertFalse(unwaredFolder.exists());
		assertFalse(unwaredFolderContent.exists());
		
		FileUtility.unzip(new ZipFile(warFile), unwaredFolder);
		
		assertTrue(unwaredFolder.exists());
		assertTrue(unwaredFolderContent.exists());
	}
	
	@Test
	public void testZipUnzipFolderContents() throws Exception
	{
		assertFalse(warFile.exists());
		
		File testFolderWithSubfolders = new File("src/test/resources/FileUtiltyTest/folderContainingSubfolders");
		File confFileToBeAddedToWar = new File(testFolderWithSubfolders, "example.conf");
		List<String> content = FileUtils.readLines(confFileToBeAddedToWar);
		
		assertTrue(content.contains("requirePrefix : namespace"));
		assertTrue(content.contains("locales : en, de_DE"));
		
		FileUtility.zipFolder(testFolderWithSubfolders, warFile, true);
		
		assertTrue(warFile.exists());
		assertFalse(warFile.isDirectory());
		
		assertFalse(unwaredFolder.exists());
		assertFalse(unwaredFolderContentWithoutParentFolder.exists());
		
		FileUtility.unzip(new ZipFile(warFile), unwaredFolder);
		
		File unwaredConfFile = new File(unwaredFolder, "example.conf");
		List<String> unwaredContent = FileUtils.readLines(unwaredConfFile);
		
		assertTrue(unwaredContent.contains("requirePrefix : namespace"));
		assertTrue(unwaredContent.contains("locales : en, de_DE"));
		assertTrue(content.equals(unwaredContent));
		
		assertTrue(unwaredFolder.exists());
		assertTrue(unwaredFolderContentWithoutParentFolder.exists());
	}
	
	@Test
	public void getAllFilesAndFoldersMatchingFilterIncludingSubdirectories() throws Exception
	{
		List<File> files = FileUtility.getAllFilesAndFoldersMatchingFilterIncludingSubdirectories(new File("src/test/resources/FileUtiltyTest"), new NameFileFilter("folderB"));
		assertEquals(1, files.size());
		assertEquals("folderB", files.get(0).getName());
	}
}

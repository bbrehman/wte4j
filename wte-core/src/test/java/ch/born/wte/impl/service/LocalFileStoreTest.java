package ch.born.wte.impl.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalFileStoreTest {

	private File tempDir;

	@Before
	public void createTempDir() throws IOException {
		tempDir = File.createTempFile("temp", Long.toString(System.nanoTime()));
		tempDir.delete();
		tempDir.mkdir();

	}

	@After
	public void removeTempDir() {
		for (File file : tempDir.listFiles()) {
			file.delete();
		}
		tempDir.delete();
	}

	@Test
	public void storeFile() throws IOException {
		LocalFileStore fileStore = new LocalFileStore();

		fileStore.setTemplateDirectory(tempDir);
		URL url = ClassLoader.getSystemResource("ch/born/wte/impl/test.txt");
		File testFile = FileUtils.toFile(url);
		OutputStream out = null;
		InputStream in = null;
		try {
			final String fileName = "fileStoreTest.txt";
			out = fileStore.getOutStream(fileName);
			in = FileUtils.openInputStream(testFile);
			IOUtils.copy(in, out);

			File[] filesInDir = tempDir.listFiles();
			assertEquals(1, filesInDir.length);
			assertEquals(fileName, filesInDir[0].getName());
			assertTrue(FileUtils.contentEquals(testFile, filesInDir[0]));

		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	@Test
	public void deleteFile() throws IOException {
		final String fileName = "fileStoreTest.txt";
		File file = new File(tempDir, fileName);
		assertTrue(file.createNewFile());

		LocalFileStore fileStore = new LocalFileStore();
		fileStore.setTemplateDirectory(tempDir);

		fileStore.deleteFile(fileName);
		assertFalse(file.exists());
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteNoExistingFile() throws IOException {
		final String fileName = "fileStoreTest.txt";
		File file = new File(tempDir, fileName);
		assertTrue(file.createNewFile());

		LocalFileStore fileStore = new LocalFileStore();
		fileStore.setTemplateDirectory(tempDir);

		fileStore.deleteFile(fileName + "bla");
		fail("Exception Expected");
	}
}
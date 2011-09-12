package nl.openedge.modules.impl.mailjob.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.activation.FileDataSource;

import nl.openedge.modules.impl.mailjob.MailJob;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author buit
 */
public final class AttachmentTest
{
	private File file1;

	private File file2;

	private MailJob mailJob;

	@Before
	public void setUp() throws Exception
	{
		file1 = new File("file1");
		FileOutputStream fos1 = new FileOutputStream(file1);
		byte[] data1 = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		fos1.write(data1);
		fos1.flush();
		fos1.close();
		assertTrue("file1 does not exists", file1.exists());

		file2 = new File("file2");
		FileOutputStream fos2 = new FileOutputStream(file2);
		byte[] data2 = new byte[] {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
		fos2.write(data2);
		fos2.flush();
		fos2.close();
		assertTrue("file1 does not exists", file2.exists());

		assertNotNull("file1 is null", file1);
		assertNotNull("file2 is null", file2);

		mailJob = new MailJob();
		assertNotNull("MailJob is null", mailJob);
	}

	@Test
	public void testAttachment()
	{
		StringBuffer attachmentString = new StringBuffer();
		try
		{
			attachmentString.append(file1.getCanonicalFile().getName()
				+ System.getProperty("path.separator"));
			attachmentString.append(file2.getCanonicalFile().getName()
				+ System.getProperty("path.separator"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			HashMap<String, FileDataSource> files =
				mailJob.getAttachments(attachmentString.toString());
			assertNotNull("List with files is null", files);
			assertEquals("The size of the hashmap with files is not correct", files.size(), 2);

			assertTrue("Files does not contain the key of file1",
				files.containsKey(file1.getCanonicalFile().getName()));
			assertTrue("Files does not contain the key of file2",
				files.containsKey(file2.getCanonicalFile().getName()));

			FileDataSource fds = files.get(file1.getCanonicalFile().getName());
			assertNotNull("FileDataSource for file1 is null", fds);
			assertTrue(
				"HashMap with FileDataSources does not contain the FileDataSource for file1", fds
					.getName().equals(file1.getCanonicalFile().getName()));
			fds = files.get(file2.getCanonicalFile().getName());
			assertNotNull("FileDataSource for file1 is null", fds);
			assertTrue(
				"HashMap with FileDataSources does not contain the FileDataSource for file1", fds
					.getName().equals(file2.getCanonicalFile().getName()));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testAttachmentWithDescription()
	{
		String description1 = "FileDes1";
		String description2 = "FileDes2";
		StringBuffer attachmentString = new StringBuffer();
		try
		{
			attachmentString.append(file1.getCanonicalFile().getName() + "<" + description1 + ">"
				+ System.getProperty("path.separator"));
			attachmentString.append(file2.getCanonicalFile().getName() + "<" + description2 + ">"
				+ System.getProperty("path.separator"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			HashMap<String, FileDataSource> files =
				mailJob.getAttachments(attachmentString.toString());
			assertNotNull("List with files is null", files);
			assertEquals("The size of the hashmap with files is not correct", files.size(), 2);

			assertTrue("Files does not contain the key of file1", files.containsKey(description1));
			assertTrue("Files does not contain the key of file2", files.containsKey(description2));

			FileDataSource fds = files.get(description1);
			assertNotNull("FileDataSource for file1 is null", fds);
			assertTrue(
				"HashMap with FileDataSources does not contain the FileDataSource for file1", fds
					.getName().equals(file1.getCanonicalFile().getName()));
			fds = files.get(description2);
			assertNotNull("FileDataSource for file1 is null", fds);
			assertTrue(
				"HashMap with FileDataSources does not contain the FileDataSource for file1", fds
					.getName().equals(file2.getCanonicalFile().getName()));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testAttachmentWithWrongDescription()
	{
		String description2 = "FileDes2";
		StringBuffer attachmentString = new StringBuffer();
		try
		{
			attachmentString.append(file2.getCanonicalFile().getName() + "<" + description2 + ""
				+ System.getProperty("path.separator"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			HashMap<String, FileDataSource> files =
				mailJob.getAttachments(attachmentString.toString());
			assertNotNull("List with files is null", files);
			assertEquals("The size of the hashmap with files is not correct", files.size(), 1);

			assertTrue("Files does not contain the key of file1",
				files.containsKey(file2.getCanonicalFile().getName()));

			FileDataSource fds = files.get(file2.getCanonicalFile().getName());
			assertNotNull("FileDataSource for file1 is null", fds);
			assertTrue(
				"HashMap with FileDataSources does not contain the FileDataSource for file1", fds
					.getName().equals(file2.getCanonicalFile().getName()));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@After
	public void tearDown()
	{
		file1.delete();
		file2.delete();

		assertFalse("file1 still exists", file1.exists());
		assertFalse("file1 still exists", file2.exists());
	}
}

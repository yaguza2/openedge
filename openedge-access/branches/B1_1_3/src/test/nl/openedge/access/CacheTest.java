package nl.openedge.access;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import nl.openedge.access.cache.Cache;
import nl.openedge.access.cache.HashMapCacheImpl;
import junit.framework.TestCase;

public class CacheTest extends TestCase 
{
	public void testSerialization() throws Exception
	{
		Cache cache = new HashMapCacheImpl();
		
		FileOutputStream ostream = new FileOutputStream("c:/tree.tmp");
	       /* Create the output stream */
	       ObjectOutputStream p = new ObjectOutputStream(ostream);

	       /* Create a tree with three levels. */

	       p.writeObject(cache); // Write the tree to the stream.
	       p.flush();
	       ostream.close();    // close the file.

		
	}
}

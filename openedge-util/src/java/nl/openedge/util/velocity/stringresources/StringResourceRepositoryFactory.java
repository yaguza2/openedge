/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package nl.openedge.util.velocity.stringresources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for constructing and obtaining the instance of 
 * StringResourceRepository implementation
 * 
 * Users can provide their own implementation by setting the property 'repositoryimpl'
 * for the resource loader. Note that at this time only one instance of a
 * string resource repository can be used in a sinlge VM.
 * 
 * @author <a href="mailto:eelco.hillenius@openedge.nl">Eelco Hillenius</a>
 */
public final class StringResourceRepositoryFactory
{
	/*
	 * is the factory initialised properly?
	 */
	private static boolean loaded = false;
	
	private static Log log = LogFactory.getLog(StringResourceRepositoryFactory.class);

	/**
	 * repository instance
	 */
	protected static StringResourceRepository repository = null;

	/**
	 * initialise factory
	 * @param implementation class that implements StringResourceRepository
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected static void init(String implementation)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		if (implementation != null)
		{
			log.info("using stringresource repo " + implementation);
			ClassLoader classLoader = StringResourceRepositoryFactory.class.getClassLoader();
			Class clazz = classLoader.loadClass(implementation);
			repository = (StringResourceRepository)clazz.newInstance();
		}
		else
		{
			log.info("using stringresource repo " + StringResourceRepositoryImpl.class);
			repository = new StringResourceRepositoryImpl();
		}

		loaded = true;
	}

	/**
	 * get the repository
	 * @return StringResourceRepository
	 * @throws StringResourceException if the factory was not set up properly
	 */
	public static StringResourceRepository getRepository() throws StringResourceException
	{
		if (!loaded)
		{
			log.info("not properly initialized; fallback to default implementation...");
			try
			{
				init(null);
				return repository;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new StringResourceException(e.getMessage());
			}
			
		}
		else
		{
			return repository;
		}
	}

}

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * Resource loader that works with Strings. Users should manualy add
 * resources to the repository that is know by the factory of this package. 
 * 
 * Below is an example configuration for this loader.
 * Note that 'repositoryimpl' is not mandatory;
 * if not provided, the factory will fall back on using the default
 * implementation of this package.
 * 
 * stringres.resource.loader.description = Velocity StringResource loader
 * stringres.resource.loader.class = 
 * 	nl.promedico.asp.util.velocitytools.stringresources.StringResourceLoader
 * stringres.resource.loader.repositoryimpl = 
 * 	nl.promedico.asp.util.velocitytools.stringresources.StringResourceRepositoryImpl
 * 
 * Resources can be added to the repository like this:
 * <code>
 * StringResourceRepositoryFactory vsRepository = null;
 * vsRepository = StringResourceRepositoryFactory.getRepository();
 * 
 * String myTemplateName = "/somewhere/intherepo/name";
 * String myTemplateBody = "Hi, ${username}... this is a some template!";
 * vsRepository.putStringResource(myTemplateName, myTemplateBody);
 * </code>
 * 
 * After this, the templates can be retrieved as allways
 * 
 * @author <a href="mailto:eelco.hillenius@openedge.nl">Eelco Hillenius</a>
 */
public final class StringResourceLoader extends ResourceLoader
{

	/*
	 * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#init(org.apache.commons.collections.ExtendedProperties)
	 */
	public void init(ExtendedProperties configuration)
	{
		rsvc.info("StringResourceLoader : initialization starting.");

		String implClass = configuration.getString("repositoryimpl");

		if (implClass == null)
		{
			rsvc.warn("'repositoryimpl' was not set... using default implementation");
		}
		else
		{
			rsvc.info("using " + implClass + " for string resource repository");
		}
		try
		{
			StringResourceRepositoryFactory.init(implClass);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}

		rsvc.info("StringResourceLoader : initialization complete.");
	}

	/**
	 * Get an InputStream so that the Runtime can build a
	 * template with it.
	 *
	 * @param name name of template to get
	 * @return InputStream containing the template
	 * @throws ResourceNotFoundException if template not found
	 *         in  classpath.
	 */
	public InputStream getResourceStream(String name) throws ResourceNotFoundException
	{
		InputStream result = null;

		if (name == null || name.length() == 0)
		{
			throw new ResourceNotFoundException("No template name provided");
		}

		try
		{
			StringResourceRepository repository = 
				StringResourceRepositoryFactory.getRepository();
			StringResource resource = repository.getStringResource(name);

			if (resource != null)
			{
				byte[] byteArray = resource.getBody().getBytes();
				result = new ByteArrayInputStream(byteArray);
			}
		}
		catch (Exception fnfe)
		{
			/*
			 *  log and convert to a general Velocity ResourceNotFoundException
			 */

			throw new ResourceNotFoundException(fnfe.getMessage());
		}

		return result;
	}

	/*
	 * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#isSourceModified(org.apache.velocity.runtime.resource.Resource)
	 */
	public boolean isSourceModified(Resource resource)
	{
		StringResource original = null;

		try
		{
			StringResourceRepository repository = 
				StringResourceRepositoryFactory.getRepository();
			original = repository.getStringResource(resource.getName());
		}
		catch (StringResourceException e)
		{
			e.printStackTrace();
		}

		if (original != null)
		{
			if (original.getLastModified() != resource.getLastModified())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		// fallthrough        
		return true;
	}

	/*
	 * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#getLastModified(org.apache.velocity.runtime.resource.Resource)
	 */
	public long getLastModified(Resource resource)
	{
		StringResource original = null;

		try
		{
			StringResourceRepository repository = 
				StringResourceRepositoryFactory.getRepository();
			original = repository.getStringResource(resource.getName());
		}
		catch (StringResourceException e)
		{
			e.printStackTrace();
		}

		if (original != null)
		{
			return original.getLastModified();
		}
		else
		{
			return 0;
		}
	}
}

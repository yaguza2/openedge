/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

//got this class from Tyrex (Exolab, http://www.exolab.org/)
//... made a few cosmetic enhancements

package nl.openedge.modules.util.jndi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.NotContextException;
import javax.naming.spi.InitialContextFactory;

/**
 * Implements a context factory for {@link MemoryContext}. When set properly
 * {@link javax.naming.InitialContext} will return a {@link
 * MemoryContext} referencing the named path in the shared memory space.
 * <p>
 * To use this context factory the JNDI properties file must include
 * the following properties:
 * <pre>
 * java.naming.factory.initial=tyrex.naming.MemoryContextFactory
 * java.naming.provider.url=
 * </pre>
 * Any non-empty URL will return a context to that path in the object tree,
 * relative to the same shared root. The returned context is read/write.
 * 
 *
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @author Eelco Hillenius
 */
public final class MemoryContextFactory implements InitialContextFactory {

    /* The shared root of the binding tree. */
    private static final MemoryBinding contextRoot = new MemoryBinding();

    /**
     * Returns a binding in the specified path. If the binding does
     * not exist, the full path is created and a new binding is returned.
     * The binding is always obtained from the shared root.
     *
     * @param path The path
     * @return The memory binding for the path
     * @throws NamingException Name is invalid
     */
    synchronized static MemoryBinding getBindings( String path )
        					throws NamingException {
        	
        MemoryBinding binding;
        MemoryBinding newBinding;
        CompositeName name;
        int i;
        
        name = new CompositeName( path );
        binding = contextRoot;
        for ( i = 0 ; i < name.size() ; ++i ) {
            if ( name.get( i ).length() > 0 ) {
                try {
                    newBinding = (MemoryBinding) binding.get( name.get( i ) );
                    if ( newBinding == null ) {
                        newBinding = new MemoryBinding();
                        binding.put( name.get( i ), newBinding );
                    }
                    binding = newBinding;
                } catch ( ClassCastException except ) {
                    throw new NotContextException( path + " does not specify a context" );
                }
            }
        }
        return binding;
    }


    /**
     * @see InitialContextFactory#getInitialContext(Hashtable)
     */
    public Context getInitialContext(Hashtable env) throws NamingException {
    	
        String url = null;
		Context ctx = null;
        if ( env.get( Context.PROVIDER_URL ) != null )
            url = env.get( Context.PROVIDER_URL ).toString();
        if ( url == null || url.length() == 0 ) {
            ctx = new MemoryContext( new MemoryBinding(), env );
        } else {
            ctx = new MemoryContext( getBindings( url ), env );
        }
        return ctx;
    }

}


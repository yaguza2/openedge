/*
 * $Id$
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

package nl.openedge.util.jndi;


import java.util.NoSuchElementException;
import javax.naming.Reference;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.LinkRef;
import javax.naming.CompositeName;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;


/**
 * Name/value bindings for use inside {@link MemoryContext}.
 * This one is never constructed directly but through {@link
 * MemoryContext}, {@link MemoryContextFactory} and related classes.
 * <p>
 * Provides heirarchial storage for name/value binding in memory
 * that is exposed through the JNDI context model. Each context
 * (not in the tree) is represented by one instance of {@link
 * MemoryBinding}, with each sub-context (child node) or bound
 * value represented by a name/value pair.
 * <p>
 * This object is thread-safe.
 *
 * @author <a href="arkin@intalio.com">Assaf Arkin</a>
 * @author Eelco Hillenius
 */
public final class MemoryBinding {

    /** The initial capacity for the hashtable. */
    public static final int INITIAL_CAPACITY    = 11;

    /** The maximum capacity for the hashtable. */
    public static final int MAXIMUM_CAPACITY = 191;

    /** The load factor for the hashtable. */
    public static final float LOAD_FACTOR = 0.75f;

    /** The path of this binding. */
    private String name = "";

    /** The parent memory binding. */
    private MemoryBinding parent;

    /** The number of bindings in the hash table. */
    private int count;

    /** The threshold for resizing the hash table. */
    private int threshold;

    /** The hashtable of memory binding entries. */
    private BindingEntry[] hashTable;

	/**
	 * construct
	 */
    public MemoryBinding() {
    	
        hashTable = new BindingEntry[ INITIAL_CAPACITY ];
        threshold = (int)( INITIAL_CAPACITY * LOAD_FACTOR );
    }

	/**
	 * get memory context
	 * @return Context
	 */
    public Context getContext() {
    	
        return new MemoryContext( this, null );
    }

	/**
	 * get instance from bindings
	 * @param name
	 * @return Object
	 */
    public synchronized Object get( String name ) {
        
        int hashCode;
        int index;
        BindingEntry entry;

        if ( name == null ) {
            throw new IllegalArgumentException( "Argument name is null" );
        }
        hashCode = name.hashCode();
        index = ( hashCode & 0x7FFFFFFF ) % hashTable.length;
        entry = hashTable[ index ];
        while( entry != null ) {
            
            if ( entry._hashCode == hashCode && entry._name.equals( name ) ) {
                return entry._value;
            }
            entry = entry._next;
        }
        return null;
    }

	/**
	 * put instance in bindings with key name
	 * @param name
	 * @param value
	 */
    public synchronized void put( String name, Object value ) {
        
        int hashCode;
        int index;
        BindingEntry entry;
        BindingEntry next;

        if ( name == null ) {
            throw new IllegalArgumentException( "Argument name is null" );
        }
        if ( value == null ) {
            throw new IllegalArgumentException( "Argument value is null" );
        }
        if ( value instanceof MemoryBinding ) {
            ( (MemoryBinding) value ).parent = this;
            ( (MemoryBinding) value ).name = name;
        }

        hashCode = name.hashCode();
        index = ( hashCode & 0x7FFFFFFF ) % hashTable.length;
        entry = hashTable[ index ];
        if ( entry == null ) {
            entry = new BindingEntry( name, hashCode, value );
            hashTable[ index ] = entry;
            ++count;
        } else {
            if ( entry._hashCode == hashCode && entry._name.equals( name ) ) {
                entry._value = value;
                return;
            } else {
                next = entry._next;
                while ( next != null ) {
                    if ( next._hashCode == hashCode && next._name.equals( name ) ) {
                        next._value = value;
                        return;
                    }
                    entry = next;
                    next = next._next;
                }
                entry._next = new BindingEntry( name, hashCode, value );
                ++count;
            }
        }
        if ( count >= threshold ) {
        	rehash();
        }
    }

	/**
	 * remove instance from bindings
	 * @param name
	 * @return Object
	 */
    public synchronized Object remove( String name ) {
    	
        int hashCode;
        int index;
        BindingEntry entry;
        BindingEntry next;

        if ( name == null ) {
            throw new IllegalArgumentException( "Argument name is null" );
        }
        hashCode = name.hashCode();
        index = ( hashCode & 0x7FFFFFFF ) % hashTable.length;
        entry = hashTable[ index ];
        if ( entry == null ) {
            return null;
        }
        if ( entry._hashCode == hashCode && entry._name.equals( name ) ) {
            hashTable[ index ] = entry._next;
            --count;
            return entry._value;
        }
        next = entry._next;
        while ( next != null ) {
            if ( next._hashCode == hashCode && next._name.equals( name ) ) {
                entry._next = next._next;
                --count;
                return next._value;
            }
            entry = next;
            next = next._next;
        }
        return null;
    }

	/**
	 * get name recursively
	 * @return String
	 */
    public String getName() {
    	
        if ( parent != null && parent.getName().length() > 0 ) {
            return parent.getName() + MemoryContext.NAME_SEPARATOR + name;
        }
        return name;
    }

	/**
	 * is this binding the root
	 * @return boolean
	 */
    public boolean isRoot() {
    	
        return ( parent == null );
    }

	/**
	 * is this binding empty
	 * @return boolean
	 */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Called when destroying the subcontext and binding associated
     * with it.
     */
    public void destroy() {
    	
        hashTable = null;
    }
    
    /**
     * get the enumerator of this binding
     * @param context
     * @param nameOnly
     * @return NamingEnumeration
     */
    public NamingEnumeration enumerate( 
    				Context context, boolean nameOnly ) {
        
        return new MemoryBindingEnumeration( context, nameOnly );
    }

	/*
	 * rehash context/ binding
	 */
    private void rehash() {
        
        int newSize;
        BindingEntry[] newTable;
        BindingEntry entry;
        BindingEntry next;
        int index;

        newSize = hashTable.length * 2 + 1;
        // Prevent the hash table from being resized beyond some maximum capacity limit.
        if ( newSize > MAXIMUM_CAPACITY ) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        newTable = new BindingEntry[ newSize ];
        for ( int i = hashTable.length ; i-- > 0 ; ) {
            entry = hashTable[ i ];
            while ( entry != null ) {
                next = entry._next;
                index = ( entry._hashCode & 0x7FFFFFFF) % newSize;
                entry._next = newTable[ index ];
                newTable[ index ] = entry;
                entry = next;
            }
        }
        hashTable = newTable;
        threshold = (int)( newSize * LOAD_FACTOR );
    }

    /*
     * Name to value binding entry in the memory binding hashtable.
     */
    private static class BindingEntry {
    	
        /* The binding name. */
        private final String  _name;
        /* The binding name hash code. */
        private final int     _hashCode;
        /* The bound value. */
        private Object _value;
        /** The next binding in the hash table entry. */
        private BindingEntry  _next;
		
		/** construct with name, hasCode and instance */
        protected BindingEntry( String name, int hashCode, Object value ) {	
            _name = name;
            _hashCode = hashCode;
            _value = value;
        }
    }

    /*
     * Naming enumeration supporting {@link NamClassPair} and {@link Binding},
     * created based of a {@link MemoryBinding}.
     *
     * @author <a href="arkin@intalio.com">Assaf Arkin</a>
     * @version $Revision$ $Date$
     * @see MemoryBinding
     */
    private final class MemoryBindingEnumeration implements NamingEnumeration {
        
        /*
         * Holds a reference to the next entry to be returned by
         * {@link next}. Becomes null when there are no more
         * entries to return.
         */
        private BindingEntry _entry;
        
        /*
         * Index to the current position in the hash table.
         */
        private int _index;

        /*
         * True to return an enumeration of {@link NameClassPair},
         * false to return an enumeration of {@link Binding}
         */
        private final boolean _nameOnly;
        
        /*
         * The context is required to create a duplicate.
         */
        private final Context _context;

        /*
         * The class name of the context class
         */
        private final String _contextClassName;

        /*
         * The value of the next element to return. Can be null.
         */
        private Object _nextValue;

        /*
         * The name of the next element to return. Can be null.
         * <P>
         * If the value is null this means that there are no element
         * to return.
         *
         * @see #hasMore
         */
        private String _nextName;

        /*
         * The class name of the next element to return. Can be null.
         */
        private String _nextClassName;

        /** construct with context */
        protected MemoryBindingEnumeration( 
        			Context context, boolean nameOnly ) {
        				
            if ( context == null )
                throw new IllegalArgumentException( "Argument context is null" );
            _context = context;
            _contextClassName = nameOnly ? context.getClass().getName() : null;
            _nameOnly = nameOnly;
            _index = hashTable.length;
        }

		/*
		 * @see java.util.Enumeration#hasMoreElements()
		 */
        public boolean hasMoreElements() {
        	
            return hasMore();
        }
        
        /*
         * @see java.util.Enumeration#nextElement()
         */
        public Object nextElement() {
        	
            return next();
        }
        
    	/*
    	 * @see javax.naming.NamingEnumeration#close()
    	 */
        public void close() {
        	
            _entry = null;
            _index = -1;
            _nextValue = null;
            _nextName = null;
            _nextClassName = null;
        }
    
		/*
		 * @see javax.naming.NamingEnumeration#hasMore()
		 */
        public boolean hasMore() {
        	
            if ( -1 == _index ) {
                return false;    
            }
            if ( null != _nextName  ) {
                return true;    
            }
            return internalHasMore();
        }
        
        /*
         * @see javax.naming.NamingEnumeration#next()
         */
        public Object next() throws NoSuchElementException {
            	
            Object value;
            String name;
            
            if ( ! hasMore() ) {
                throw new NoSuchElementException( "No more elements in enumeration" );    
            }

            name = _nextName;
            _nextName = null;
            
            if ( _nameOnly ) {
                return new NameClassPair( name, _nextClassName, true );
            }
            
            value = _nextValue;
            _nextValue = null;
            return new Binding( name, _nextClassName, value, true );
        }

        /*
         * Return true if there are more elements in the 
         * enumeration.
         * <P>
         * This method has the side effects of setting 
         * {@link #_nextValue}, {@link #_nextName}, 
         * {@link #_nextClassName}.
         *
         * @return true if there are more elements in the 
         *      enumeration.
         */
        private boolean internalHasMore() {
        	
            BindingEntry entry;
            Object value;
            
            entry = nextEntry();

            if ( null == entry ) {
                return false;    
            }

            // the variable _nextName is set last in every clause so that
            // it wont have to be reset to null when making recursive calls.
            // This is done because the test in #hasMore relys on testing
            // _nextName for null

            value = entry._value;
            if ( value instanceof MemoryBinding ) {
                if ( _nameOnly ) {
                    _nextClassName = _contextClassName;
                } else {
                    try {
                        // If another context, must use lookup to create a duplicate.
                        _nextValue = _context.lookup( entry._name );
                        _nextClassName = _nextValue.getClass().getName();
                    } catch ( NamingException except ) {
                        // Skip this entry and go immediately to next one.
                        return internalHasMore();
                    }    
                }
                _nextName = entry._name;
            } else if ( ( value instanceof LinkRef ) ) {
                try {
                    // Need to resolve the link.
                    _nextValue = _context.lookup( entry._name );
                    _nextClassName = ( null == _nextValue ) ? null : _nextValue.getClass().getName();
                } catch ( NamingException except ) {
                    // Skip this entry and go immediately to next one.
                    return internalHasMore();
                }
                if ( _nameOnly ) {
                    _nextValue = null;
                }
                _nextName = entry._name;
            } else if ( value instanceof Reference ) {
                if ( !_nameOnly ) {
                    try {
                        _nextValue = NamingManager.getObjectInstance( value, new CompositeName( entry._name ), _context, null );
                    } catch ( Exception except ) {
                        // Skip this entry and go immediately to next one.
                        return internalHasMore();
                    }
                }
                _nextClassName = ( ( Reference ) value ).getClassName();
                _nextName = entry._name;
                
            } else {
                if ( !_nameOnly ) {
                    _nextValue = value;
                }
                _nextClassName = ( null == value ) ? null : value.getClass().getName();
                _nextName = entry._name;
            } 
            return true;
        }
        
        /*
         * @return BindingEntry
         */
        private BindingEntry nextEntry() {
            
            BindingEntry   entry;
            int            index;
            BindingEntry[] table;
            
            entry = _entry;
            if ( entry != null ) {
                _entry = entry._next;
                return entry;
            }
            table = hashTable;
            index = _index;
            while ( index > 0 ) {
                entry = table[ --index ];
                if ( entry != null ) {
                    _entry = entry._next;
                    _index = index;
                    return entry;
                }
            }
            return null;
        }   
    }

}


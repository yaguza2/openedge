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
package nl.openedge.modules.util.jndi;

import java.util.NoSuchElementException;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.LinkRef;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;

/**
 * Name/value bindings for use inside {@link MemoryContext}. This one is never constructed directly
 * but through {@linkMemoryContext},{@link MemoryContextFactory}and related classes.
 * <p>
 * Provides heirarchial storage for name/value binding in memory that is exposed through the JNDI
 * context model. Each context (not in the tree) is represented by one instance of {@link
 * MemoryBinding}, with each sub-context (child node) or bound value represented by a name/value
 * pair.
 * <p>
 * This object is thread-safe.
 * 
 * @author <a href="arkin@intalio.com">Assaf Arkin </a>
 * @author Eelco Hillenius
 */
public final class MemoryBinding
{

	private static final int INDEX_AND = 0x7FFFFFFF;
	/** The initial capacity for the hashtable. */
	public static final int INITIAL_CAPACITY = 11;

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
	 * construct.
	 */
	public MemoryBinding()
	{

		hashTable = new BindingEntry[INITIAL_CAPACITY];
		threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
	}

	/**
	 * get memory context.
	 * 
	 * @return Context
	 */
	public Context getContext()
	{

		return new MemoryContext(this, null);
	}

	/**
	 * get instance from bindings.
	 * 
	 * @param name binding name
	 * @return Object
	 */
	public synchronized Object get(String name)
	{

		int hashCode;
		int index;
		BindingEntry entry;

		if (name == null)
		{
			throw new IllegalArgumentException("Argument name is null");
		}
		hashCode = name.hashCode();
		index = (hashCode & INDEX_AND) % hashTable.length;
		entry = hashTable[index];
		while (entry != null)
		{

			if (entry.bindingHashCode == hashCode && entry.bindingName.equals(name))
			{
				return entry.bindingValue;
			}
			entry = entry.nextBinding;
		}
		return null;
	}

	/**
	 * put instance in bindings with key name.
	 * 
	 * @param bindingName binding name
	 * @param value value to bind
	 */
	public synchronized void put(String bindingName, Object value)
	{

		int hashCode;
		int index;
		BindingEntry entry;
		BindingEntry next;

		if (bindingName == null)
		{
			throw new IllegalArgumentException("Argument name is null");
		}
		if (value == null)
		{
			throw new IllegalArgumentException("Argument value is null");
		}
		if (value instanceof MemoryBinding)
		{
			((MemoryBinding) value).parent = this;
			((MemoryBinding) value).name = bindingName;
		}

		hashCode = bindingName.hashCode();
		index = (hashCode & INDEX_AND) % hashTable.length;
		entry = hashTable[index];
		if (entry == null)
		{
			entry = new BindingEntry(bindingName, hashCode, value);
			hashTable[index] = entry;
			++count;
		}
		else
		{
			if (entry.bindingHashCode == hashCode && entry.bindingName.equals(bindingName))
			{
				entry.bindingValue = value;
				return;
			}
			else
			{
				next = entry.nextBinding;
				while (next != null)
				{
					if (next.bindingHashCode == hashCode && next.bindingName.equals(bindingName))
					{
						next.bindingValue = value;
						return;
					}
					entry = next;
					next = next.nextBinding;
				}
				entry.nextBinding = new BindingEntry(bindingName, hashCode, value);
				++count;
			}
		}
		if (count >= threshold)
		{
			rehash();
		}
	}

	/**
	 * remove instance from bindings.
	 * 
	 * @param bindingName binding name
	 * @return Object previously bound object if any
	 */
	public synchronized Object remove(String bindingName)
	{

		int hashCode;
		int index;
		BindingEntry entry;
		BindingEntry next;

		if (bindingName == null)
		{
			throw new IllegalArgumentException("Argument name is null");
		}
		hashCode = bindingName.hashCode();
		index = (hashCode & INDEX_AND) % hashTable.length;
		entry = hashTable[index];
		if (entry == null)
		{
			return null;
		}
		if (entry.bindingHashCode == hashCode && entry.bindingName.equals(bindingName))
		{
			hashTable[index] = entry.nextBinding;
			--count;
			return entry.bindingValue;
		}
		next = entry.nextBinding;
		while (next != null)
		{
			if (next.bindingHashCode == hashCode && next.bindingName.equals(bindingName))
			{
				entry.nextBinding = next.nextBinding;
				--count;
				return next.bindingValue;
			}
			entry = next;
			next = next.nextBinding;
		}
		return null;
	}

	/**
	 * get name recursively.
	 * 
	 * @return binding name
	 */
	public String getName()
	{

		if (parent != null && parent.getName().length() > 0)
		{
			return parent.getName() + MemoryContext.NAME_SEPARATOR + name;
		}
		return name;
	}

	/**
	 * is this binding the root.
	 * 
	 * @return is this binding the root
	 */
	public boolean isRoot()
	{

		return (parent == null);
	}

	/**
	 * is this binding empty.
	 * 
	 * @return is this binding empty
	 */
	public boolean isEmpty()
	{
		return count == 0;
	}

	/**
	 * Called when destroying the subcontext and binding associated with it.
	 */
	public void destroy()
	{

		hashTable = null;
	}

	/**
	 * get the enumerator of this binding.
	 * 
	 * @param context JNDI context
	 * @param nameOnly is name only
	 * @return NamingEnumeration
	 */
	public NamingEnumeration enumerate(Context context, boolean nameOnly)
	{

		return new MemoryBindingEnumeration(context, nameOnly);
	}

	/**
	 * rehash context/ binding.
	 */
	private void rehash()
	{

		int newSize;
		BindingEntry[] newTable;
		BindingEntry entry;
		BindingEntry next;
		int index;

		newSize = hashTable.length * 2 + 1;
		// Prevent the hash table from being resized beyond some maximum capacity limit.
		if (newSize > MAXIMUM_CAPACITY)
		{
			threshold = Integer.MAX_VALUE;
			return;
		}

		newTable = new BindingEntry[newSize];
		for (int i = hashTable.length; i-- > 0;)
		{
			entry = hashTable[i];
			while (entry != null)
			{
				next = entry.nextBinding;
				index = (entry.bindingHashCode & INDEX_AND) % newSize;
				entry.nextBinding = newTable[index];
				newTable[index] = entry;
				entry = next;
			}
		}
		hashTable = newTable;
		threshold = (int) (newSize * LOAD_FACTOR);
	}

	/**
	 * Name to value binding entry in the memory binding hashtable.
	 */
	private static class BindingEntry
	{

		/** The binding name. */
		private final String bindingName;

		/** The binding name hash code. */
		private final int bindingHashCode;

		/** The bound value. */
		private Object bindingValue;

		/** The next binding in the hash table entry. */
		private BindingEntry nextBinding;

		/**
		 * construct with name, hasCode and instance.
		 * @param name binding name
		 * @param hashCode hash code
		 * @param value value to bind
		 */
		protected BindingEntry(String name, int hashCode, Object value)
		{
			bindingName = name;
			bindingHashCode = hashCode;
			bindingValue = value;
		}
	}

	/**
	 * Naming enumeration supporting {@link NamClassPair}and {@link Binding}, created based of a
	 * {@link MemoryBinding}. @author <a href="arkin@intalio.com">Assaf Arkin </a>
	 */
	private final class MemoryBindingEnumeration implements NamingEnumeration
	{

		/**
		 * Holds a reference to the next entry to be returned by {@link next}. Becomes null when
		 * there are no more entries to return.
		 */
		private BindingEntry bindingEntry;

		/**
		 * Index to the current position in the hash table.
		 */
		private int bindingIndex;

		/**
		 * True to return an enumeration of {@link NameClassPair}, false to return an enumeration
		 * of {@link Binding}
		 */
		private final boolean bindingNameOnly;

		/**
		 * The context is required to create a duplicate.
		 */
		private final Context bindingContext;

		/**
		 * The class name of the context class
		 */
		private final String contextClassName;

		/**
		 * The value of the next element to return. Can be null.
		 */
		private Object nextValue;

		/**
		 * The name of the next element to return. Can be null. <P> If the value is null this means
		 * that there are no element to return.
		 */
		private String nextName;

		/**
		 * The class name of the next element to return. Can be null.
		 */
		private String nextClassName;

		/**
		 * Construct with context.
		 * @param context JNDI context
		 * @param nameOnly is name only
		 */
		protected MemoryBindingEnumeration(Context context, boolean nameOnly)
		{
			if (context == null)
				throw new IllegalArgumentException("Argument context is null");
			bindingContext = context;
			if (nameOnly)
			{
				contextClassName = context.getClass().getName();
			}
			else
			{
				contextClassName = null;
			}
			bindingNameOnly = nameOnly;
			bindingIndex = hashTable.length;
		}

		/**
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		public boolean hasMoreElements()
		{
			return hasMore();
		}

		/**
		 * @see java.util.Enumeration#nextElement()
		 */
		public Object nextElement()
		{
			return next();
		}

		/**
		 * @see javax.naming.NamingEnumeration#close()
		 */
		public void close()
		{
			bindingEntry = null;
			bindingIndex = -1;
			nextValue = null;
			nextName = null;
			nextClassName = null;
		}

		/**
		 * @see javax.naming.NamingEnumeration#hasMore()
		 */
		public boolean hasMore()
		{
			if (-1 == bindingIndex)
			{
				return false;
			}
			if (null != nextName)
			{
				return true;
			}
			return internalHasMore();
		}

		/**
		 * @see javax.naming.NamingEnumeration#next()
		 */
		public Object next()
		{
			Object value;
			String currentName;

			if (!hasMore())
			{
				throw new NoSuchElementException("No more elements in enumeration");
			}

			currentName = nextName;
			nextName = null;

			if (bindingNameOnly)
			{
				return new NameClassPair(currentName, nextClassName, true);
			}

			value = nextValue;
			nextValue = null;
			return new Binding(currentName, nextClassName, value, true);
		}

		/**
		 * Return true if there are more elements in the enumeration. <P> This method has the side
		 * effects of setting {@link #nextValue},{@link #nextName},{@link #nextClassName}.
		 * @return true if there are more elements in the enumeration.
		 */
		private boolean internalHasMore()
		{

			BindingEntry entry;
			Object value;

			entry = nextEntry();

			if (null == entry)
			{
				return false;
			}

			// the variable nextName is set last in every clause so that
			// it wont have to be reset to null when making recursive calls.
			// This is done because the test in #hasMore relys on testing
			// nextName for null

			value = entry.bindingValue;
			if (value instanceof MemoryBinding)
			{
				if (bindingNameOnly)
				{
					nextClassName = contextClassName;
				}
				else
				{
					try
					{
						// If another context, must use lookup to create a duplicate.
						nextValue = bindingContext.lookup(entry.bindingName);
						nextClassName = nextValue.getClass().getName();
					}
					catch (NamingException except)
					{
						// Skip this entry and go immediately to next one.
						return internalHasMore();
					}
				}
				nextName = entry.bindingName;
			}
			else if ((value instanceof LinkRef))
			{
				try
				{
					// Need to resolve the link.
					nextValue = bindingContext.lookup(entry.bindingName);
					nextClassName = (null == nextValue) ? null : nextValue.getClass()
							.getName();
				}
				catch (NamingException except)
				{
					// Skip this entry and go immediately to next one.
					return internalHasMore();
				}
				if (bindingNameOnly)
				{
					nextValue = null;
				}
				nextName = entry.bindingName;
			}
			else if (value instanceof Reference)
			{
				if (!bindingNameOnly)
				{
					try
					{
						nextValue = NamingManager.getObjectInstance(value,
								new CompositeName(entry.bindingName), bindingContext, null);
					}
					catch (Exception except)
					{
						// Skip this entry and go immediately to next one.
						return internalHasMore();
					}
				}
				nextClassName = ((Reference) value).getClassName();
				nextName = entry.bindingName;

			}
			else
			{
				if (!bindingNameOnly)
				{
					nextValue = value;
				}
				nextClassName = (null == value) ? null : value.getClass().getName();
				nextName = entry.bindingName;
			}
			return true;
		}

		/*
		 * @return BindingEntry
		 */
		private BindingEntry nextEntry()
		{

			BindingEntry entry;
			int index;
			BindingEntry[] table;

			entry = bindingEntry;
			if (entry != null)
			{
				bindingEntry = entry.nextBinding;
				return entry;
			}
			table = hashTable;
			index = bindingIndex;
			while (index > 0)
			{
				entry = table[--index];
				if (entry != null)
				{
					bindingEntry = entry.nextBinding;
					bindingIndex = index;
					return entry;
				}
			}
			return null;
		}
	}

}


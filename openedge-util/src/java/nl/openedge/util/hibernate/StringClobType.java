/*
 */
package nl.openedge.util.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Clob;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;

/**
 * @author shofstee
 *
 * Mapped een SQL CLOB op een java String.
 */
public class StringClobType implements UserType
{
	/**
	 * Geeft een lijst met de toegestane sql types terug.
	 * @return int[] een array met java.sql.Types.
	 */
	public int[] sqlTypes()
	{
		return new int[] { Types.CLOB };
	}

	/**
	 * Geeft de java class terug die de data gaat krijgen.
	 * @return het type van de uitvoer dat door deze UserType terug gegeven wordt.
	 */
	public Class returnedClass()
	{
		return String.class;
	}

	/**
	 * Geeft true terug als x == y
	 * @return true als x == y
	 */
	public boolean equals(Object x, Object y)
	{
		return (x == y)
			|| (x != null
				&& y != null
				&& (x.equals(y)));
	}

	/**
	 * 
	 * @param rs
	 * @param names
	 * @param owner
	 * @return 
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
		throws HibernateException, SQLException
	{
		Clob clob = rs.getClob(names[0]);
		return clob.getSubString(1, (int) clob.length());
	}

	/**
	 * 
	 * @param st
	 * @param value
	 * @param index
	 * @return
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index)
		throws HibernateException, SQLException
	{
		st.setClob(index, Hibernate.createClob((String) value));
	}

	/**
	 * @param value het object dat gekopieerd moet worden.
	 * @return een echte kopie van value.
	 */
	public Object deepCopy(Object value)
	{
		if (value == null) 
		{
			return null;
		}
		return new String((String) value);
	}

	/**
	 * Geeft terug of de waarde van dit type te wijzigen is.
	 * @return false.
	 */
	public boolean isMutable()
	{
		return false;
	}
}

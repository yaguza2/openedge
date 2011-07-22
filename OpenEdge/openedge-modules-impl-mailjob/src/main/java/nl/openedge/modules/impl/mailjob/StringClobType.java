/*
 * StringClobType.java
 * Created on Feb 17, 2004 by hop
 *
 */
package nl.openedge.modules.impl.mailjob;

import java.io.Serializable;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Afkomstig van http://hibernate.bluemars.net/76.html
 * 
 * @author hop
 */
public class StringClobType implements UserType
{
	@Override
	public int[] sqlTypes()
	{
		return new int[] {Types.CLOB};
	}

	@Override
	public Class< ? > returnedClass()
	{
		return String.class;
	}

	@Override
	public boolean equals(Object x, Object y)
	{
		return (x == y) || (x != null && y != null && (x.equals(y)));
	}

	@Deprecated
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException
	{
		String result = null;
		Clob clob = rs.getClob(names[0]);
		if (clob != null)
			result = clob.getSubString(1, (int) clob.length());
		return result;
	}

	@Deprecated
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException
	{
		if (value != null && ((String) value).length() > 0)
		{
			// Clob clob = Hibernate.createClob((String) value);
			st.setString(index, (String) value);
		}
		else
			st.setNull(index, Types.CLOB);
	}

	@Override
	public Object deepCopy(Object value)
	{
		if (value == null)
			return null;
		return new String((String) value);
	}

	@Override
	public boolean isMutable()
	{
		return false;
	}

	@Override
	public Object assemble(Serializable arg0, Object arg1) throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Serializable disassemble(Object arg0) throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException
	{
		throw new UnsupportedOperationException();
	}
}

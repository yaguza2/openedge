/*
 * StringClobType.java
 * Created on Feb 17, 2004 by hop
 *
 */
package nl.openedge.modules.impl.mailjob;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.UserType;

/**
 * @author hop
 *
 * Afkomstig van http://hibernate.bluemars.net/76.html
 */
public class StringClobType implements UserType
{

	public int[] sqlTypes()
	{
		return new int[] { Types.CLOB };
	}

	public Class returnedClass()
	{
		return String.class;
	}

	public boolean equals(Object x, Object y)
	{
		return (x == y) || (x != null && y != null && (x.equals(y)));
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
		throws HibernateException, SQLException
	{
		String result = null;
		Clob clob = rs.getClob(names[0]);
		if (clob != null)
			result = clob.getSubString(1, (int)clob.length());
		return result;
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index)
		throws HibernateException, SQLException
	{
		if (value != null && ((String)value).length() > 0)
		{
			//			Clob clob = Hibernate.createClob((String) value); 
			st.setString(index, (String)value);
		}
		else
			st.setNull(index, Types.CLOB);
	}

	public Object deepCopy(Object value)
	{
		if (value == null)
			return null;
		return new String((String)value);
	}

	public boolean isMutable()
	{
		return false;
	}

}

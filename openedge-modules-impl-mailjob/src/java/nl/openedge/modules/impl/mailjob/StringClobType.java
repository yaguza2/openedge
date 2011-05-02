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

	@Override
	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		// TODO Auto-generated method stub
		
		throw new RuntimeException(" not implemented");
//		return null;
	}

	@Override
	public Serializable disassemble(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		throw new RuntimeException(" not implemented");
//		return null;
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		throw new RuntimeException(" not implemented");
//		return 0;
	}

	@Override
	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		// TODO Auto-generated method stub
		throw new RuntimeException(" not implemented");
//		return null;
	}

}

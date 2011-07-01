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
package nl.openedge.modules.impl.usermanager.hib;

import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ognl.Ognl;
import ognl.OgnlException;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import nl.openedge.access.AccessException;
import nl.openedge.access.UserManagerModule;
import nl.openedge.access.util.PasswordHelper;
import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.util.hibernate.HibernateHelper;

/**
 * @author Eelco Hillenius
 */
public class UserManagerModuleImpl 
	implements UserManagerModule, SingletonType
{

	/** props */
	protected Properties properties = new Properties();

	/**
	 * construct
	 */
	public UserManagerModuleImpl()
	{
		super();
		try
		{
			// first, see if there's an override in the root class dir
			properties.load(UserManagerModuleImpl.class.getResourceAsStream(
				"/UserManagerModuleImpl.properties"));
		}
		catch (Exception e)
		{
			// nope, so load from package
			try
			{
				properties.load(UserManagerModuleImpl.class.getResourceAsStream(
					"UserManagerModuleImpl.properties"));
			}
			catch (Exception e2)
			{ // should not happen!
				e.printStackTrace();
				throw new RuntimeException(e2);
			}
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#createUser(java.lang.String, java.lang.String, java.util.Map)
	 */
	public Principal createUser(String name, String password, Map attributes) 
			throws AccessException
	{

		UserPrincipal user = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			user = new UserPrincipal(name);
			String cryptedPassword = new String(
				PasswordHelper.cryptPassword(password.toCharArray()));

			populate(user, attributes);
			user.setPassword(cryptedPassword);
			session.save(user);
			tx.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return user;
	}
	
	/**
	 * populate bean.
	 * @param componentInstance
	 * @param properties
	 * @return true if populate did not have any troubles, false otherwise
	 */
	protected void populate(Object bean, Map properties) 
		throws OgnlException
	{
		if(properties != null)
		{
			for(Iterator i = properties.keySet().iterator(); i.hasNext(); )
			{
				String key = (String)i.next();
				Object value = properties.get(key);
				Ognl.setValue(key, bean, value);
			}	
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#getUser(java.lang.String)
	 */
	public Principal getUser(String name) throws AccessException
	{

		UserPrincipal user = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			user = getUser(session, name);
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return user;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#listUsers()
	 */
	public List listUsers() throws AccessException
	{

		List users = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			users = session.find("from u in class " + UserPrincipal.class.getName());
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return users;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#resetPassword(nl.openedge.access.Principal, java.lang.String)
	 */
	public void resetPassword(Principal user, String newPassword) 
			throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			if (u != null)
			{
				String cryptedPassword = new String(
					PasswordHelper.cryptPassword(newPassword.toCharArray()));
				u.setPassword(cryptedPassword);
				
				tx.commit();
			}
			else
			{
				throw new AccessException("user " + user + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#deleteUser(nl.openedge.access.Principal)
	 */
	public void deleteUser(Principal user) throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			if (u != null)
			{
				session.delete(u);
				tx.commit();
			}
			else
			{
				throw new AccessException("user " + user + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#createRole(java.lang.String)
	 */
	public Principal createRole(String name) throws AccessException
	{

		RolePrincipal role = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			role = new RolePrincipal(name);
			session.save(role);
			
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return role;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#getRole(java.lang.String)
	 */
	public Principal getRole(String name) throws AccessException
	{

		RolePrincipal role = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			List l =
				session.find(
					"from r in class " + RolePrincipal.class.getName() 
						+ " where r.name = ?",
					name,
					Hibernate.STRING);
			if (l != null && (!l.isEmpty()))
			{
				role = (RolePrincipal)l.get(0);
			}
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return role;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#listRoles()
	 */
	public List listRoles() throws AccessException
	{

		List roles = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			roles = session.find("from user in class " + 
				RolePrincipal.class.getName());
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return roles;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#listUsersInRole(nl.openedge.access.Principal)
	 */
	public List listUsersInRole(Principal role) throws AccessException
	{

		List users = new ArrayList();
		if (role == null)
			return users;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Session session;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			String q = properties.getProperty("selectUserIdsInRoleStmt");

			Connection conn = session.connection();
			pstmt = conn.prepareStatement(q);
			pstmt.setObject(1, role.getName());
			rs = pstmt.executeQuery();
			List l = new ArrayList();
			while (rs.next())
			{
				l.add(new Long(rs.getLong(1)));
			}
			if (!l.isEmpty())
			{
				for (Iterator i = l.iterator(); i.hasNext();)
				{
					users.add(session.load(UserPrincipal.class, (Long)i.next()));
				}
			}
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (SQLException sqle)
				{
				}
			if (pstmt != null)
				try
				{
					pstmt.close();
				}
				catch (SQLException sqle)
				{
					// nada
				}
		}
		return users;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#listRolesForUser(nl.openedge.access.Principal)
	 */
	public Set listRolesForUser(Principal user) throws AccessException
	{

		Set roles = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			if (u != null)
			{
				tx.commit();
				roles = u.getRoles();
			}
			else
			{
				throw new AccessException("user " + user + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return roles;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#addUserToRole(nl.openedge.access.Principal, nl.openedge.access.Principal)
	 */
	public void addUserToRole(Principal user, Principal role) throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			RolePrincipal r = (RolePrincipal)session.load(
				RolePrincipal.class, role.getName());
			if (u != null && r != null)
			{
				u.addRole(r);
				tx.commit();
			}
			else
			{
				throw new AccessException("user " + user + " and/ or role " + 
					role + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#removeUserFromRole(nl.openedge.access.Principal, nl.openedge.access.Principal)
	 */
	public void removeUserFromRole(Principal user, Principal role) throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			if (u != null)
			{

				RolePrincipal r = (RolePrincipal)role;
				if (u.containsRole(r))
				{
					u.removeRole(role);
					tx.commit();
				}
				else
				{
					try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
					throw new AccessException(
						"user " + user + " did not have role " + role);
				}
			}
			else
			{
				throw new AccessException("user " + user + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#deleteRole(nl.openedge.access.Principal)
	 */
	public void deleteRole(Principal role) throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			RolePrincipal r = (RolePrincipal)
				session.load(RolePrincipal.class, role.getName());
			if (r != null)
			{
				session.delete(r);
				tx.commit();
			}
			else
			{
				throw new AccessException("role " + role + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#createGroup(java.lang.String)
	 */
	public Principal createGroup(String name) throws AccessException
	{

		GroupPrincipal group = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			group = new GroupPrincipal(name);
			session.save(group);
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return group;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#getGroup(java.lang.String)
	 */
	public Principal getGroup(String name) throws AccessException
	{

		GroupPrincipal group = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			List l =
				session.find(
					"from g in class " + GroupPrincipal.class.getName() 
						+ " where g.name = ?",
					name,
					Hibernate.STRING);
			if (l != null && (!l.isEmpty()))
			{
				group = (GroupPrincipal)l.get(0);
			}
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return group;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#listGroups()
	 */
	public List listGroups() throws AccessException
	{

		List groups = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			groups = session.find("from u in class " + 
				GroupPrincipal.class.getName());
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return groups;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#listUsersInGroup(nl.openedge.access.Principal)
	 */
	public List listUsersInGroup(Principal group) throws AccessException
	{

		List users = new ArrayList();
		if (group == null)
			return users;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Session session;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			String q = properties.getProperty("selectUserIdsInGroupStmt");

			Connection conn = session.connection();
			pstmt = conn.prepareStatement(q);
			pstmt.setObject(1, group.getName());
			rs = pstmt.executeQuery();
			List l = new ArrayList();
			while (rs.next())
			{
				l.add(new Long(rs.getLong(1)));
			}
			if (!l.isEmpty())
			{
				for (Iterator i = l.iterator(); i.hasNext();)
				{
					users.add(session.load(UserPrincipal.class, (Long)i.next()));
				}
			}
			tx.commit();
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (SQLException sqle)
				{
				}
			if (pstmt != null)
				try
				{
					pstmt.close();
				}
				catch (SQLException sqle)
				{
				}
		}
		return users;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#listGroupsForUser(nl.openedge.access.Principal)
	 */
	public Set listGroupsForUser(Principal user) throws AccessException
	{

		Set groups = null;
		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			if (u != null)
			{
				tx.commit();
				groups = u.getGroups();
			}
			else
			{
				throw new AccessException("user " + user + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
		return groups;
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#addUserToGroup(nl.openedge.access.Principal, nl.openedge.access.Principal)
	 */
	public void addUserToGroup(Principal user, Principal group) throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			GroupPrincipal g = (GroupPrincipal)
				session.load(GroupPrincipal.class, group.getName());
			if (u != null && g != null)
			{

				u.addGroup(g);
				tx.commit();
			}
			else
			{
				throw new AccessException("user " + user + " and/ or group " 
					+ group + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#removeUserFromGroup(nl.openedge.access.Principal, nl.openedge.access.Principal)
	 */
	public void removeUserFromGroup(Principal user, Principal group) 
			throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			UserPrincipal u = getUser(session, user.getName());
			if (u != null)
			{

				GroupPrincipal g = (GroupPrincipal)group;
				if (u.containsGroup(g))
				{
					u.removeGroup(group);
					tx.commit();
				}
				else
				{
					throw new AccessException("user " + user 
							+ " did not have group " + group);
				}
			}
			else
			{
				throw new AccessException("user " + user + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/*
	 * @see nl.openedge.access.UserManagerModule#deleteGroup(nl.openedge.access.Principal)
	 */
	public void deleteGroup(Principal group) throws AccessException
	{

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSession();
			tx = session.beginTransaction();
			GroupPrincipal g = (GroupPrincipal)
				session.load(GroupPrincipal.class, group.getName());
			if (g != null)
			{
				session.delete(g);
				tx.commit();
			}
			else
			{
				throw new AccessException("group " + group + " is not persistent");
			}
		}
		catch (Exception e)
		{
			try	{ tx.rollback(); } catch (HibernateException e1) { e1.printStackTrace(); }
			throw new AccessException(e);
		}
	}

	/* get user */
	protected UserPrincipal getUser(Session session, String name) throws Exception
	{

		List l =
			session.find(
				"from u in class " + UserPrincipal.class.getName() 
					+ " where u.name = ?",
				name,
				Hibernate.STRING);
		if (l != null && (!l.isEmpty()))
		{
			return (UserPrincipal)l.get(0);
		}
		else
		{
			return null;
		}
	}

}

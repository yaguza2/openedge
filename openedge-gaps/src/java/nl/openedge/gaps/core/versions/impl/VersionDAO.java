/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.core.versions.impl;

import java.util.HashSet;
import java.util.List;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.impl.GroupWrapper;
import nl.openedge.gaps.core.parameters.impl.ParameterWrapper;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * DAO voor Version objecten.
 */
public final class VersionDAO {

    /** Log. */
    private static Log log = LogFactory.getLog(VersionDAO.class);

    /**
     * Construct.
     */
    public VersionDAO() {
        //
    }

    /**
     * Laad alle bekende versies in.
     * @return een lijst met alle bekende versies
     */
    public List getAllVersions() throws VersionDAOException {
        List versions = null;
        Transaction tx = null;
        try {
            Session session = HibernateHelper.getSession();
            tx = session.beginTransaction();
            versions = session.find("from " + Version.class.getName());
            tx.commit();
        } catch (HibernateException e) {
            rollback(tx);
            throw new VersionDAOException(e);
        }
        return versions;
    }

    /**
     * Haal versie op naam op (gooit exception indien niet gevonden).
     * @param name versienaam
     * @return de versie
     */
    public Version getVersion(String name) throws VersionDAOException {
        Version version = null;
        Transaction tx = null;
        try {
            Session session = HibernateHelper.getSession();
            tx = session.beginTransaction();
            List result = session.find(
                    "from " + Version.class.getName()
                    + " v where v.name = ?",
                    name, Hibernate.STRING);
            if(result.isEmpty()) {
                throw new VersionDAOException("versie " + name + " niet gevonden");
            } else if(result.size() > 1) {
                throw new IllegalStateException(
                        "meerdere versie gevonden met dezelfde naam (" + name + ")");
            } else {
                version = (Version)result.get(0);
            }
            tx.commit();
        } catch (HibernateException e) {
            rollback(tx);
            throw new RegistryException(e);
        }
        return version;
    }

    /**
     * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#updateVersion(nl.openedge.gaps.core.Version)
     */
    public void updateVersion(Version version) throws VersionDAOException {
        Transaction tx = null;
        try {
            Session session = HibernateHelper.getSession();
            tx = session.beginTransaction();
            session.update(version);
            tx.commit();
        } catch (HibernateException e) {
            rollback(tx);
            throw new VersionDAOException(e);
        }
    }

    /**
     * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#deleteVersion(nl.openedge.gaps.core.Version)
     */
    public void deleteVersion(Version version) throws VersionDAOException {
        // TODO dit dienen we alleen toe te staan voor een versie die nog
        // niet actief is
        Transaction tx = null;
        try {
            Session session = HibernateHelper.getSession();
            tx = session.beginTransaction();
            session.delete(version);
            tx.commit();
        } catch (HibernateException e) {
            rollback(tx);
            throw new VersionDAOException(e);
        }
    }

    /**
     * @see nl.openedge.gaps.core.versions.VersionRegistryDelegate#saveVersion(nl.openedge.gaps.core.versions.Version, StructuralGroup)
     */
    public void saveVersion(Version version) throws VersionDAOException {
        Transaction tx = null;
        try {
            Session session = HibernateHelper.getSession();
            tx = session.beginTransaction();
            session.save(version);
            tx.commit();
        } catch (HibernateException e) {
            rollback(tx);
            throw new VersionDAOException(e);
        }
    }

    /**
     * Geeft het associatieobject waarmee de versies voor de gegeven
     * entiteit worden bijgehouden.
     * @param entity de groep
     * @return het associatieobject (nooit null)
     */
    public EntityVersions getVersions(Entity entity)
    	throws VersionDAOException {

        String entityId = entity.getId();
        EntityVersions setOfVersionIds = new EntityVersions();
        setOfVersionIds.setEntityId(entityId);
        Transaction tx = null;
        Class clazz = null;
        if(entity instanceof Group) {
            clazz = GroupWrapper.class;
        } else {
            clazz = ParameterWrapper.class;
        }
        try {
            Session session = HibernateHelper.getSession();
            tx = session.beginTransaction();
            Query query = session.createQuery(
                    "select w.versionId from " +  clazz.getName()
                    + " w where w.path = ?");
            query.setString(0, entityId);
            List result = query.list();
            setOfVersionIds.setVersionIds(new HashSet(result));
            tx.commit();
        } catch (HibernateException e) {
            rollback(tx);
            throw new VersionDAOException(e);
        }       
        return setOfVersionIds;
    }

    /**
     * Rollback transactie.
     * @param tx transactie
     */
    private void rollback(Transaction tx) {
        if (tx != null) {
            try {
                tx.rollback();
            } catch (HibernateException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
    }

}

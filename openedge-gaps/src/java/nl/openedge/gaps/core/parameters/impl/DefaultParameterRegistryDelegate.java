/*
 * $Id: DefaultParameterRegistryDelegate.java,v 1.1 2004/08/12 00:10:50
 * hillenius Exp $ $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.util.HashMap;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Transaction;
import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.NotFoundException;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.core.groups.impl.GroupDAO;
import nl.openedge.gaps.core.groups.impl.GroupDAOException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistryDelegate;
import nl.openedge.gaps.core.parameters.SaveException;
import nl.openedge.gaps.core.versions.Version;
import nl.openedge.gaps.core.versions.VersionRegistry;
import nl.openedge.gaps.core.versions.VersionRegistryDelegate;
import nl.openedge.gaps.util.EntityUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * De standaard delegate die zal worden gebruikt indien er geen andere delegate
 * is geregistreerd bij de
 * {@link nl.openedge.gaps.core.parameters.ParameterRegistry}.
 */
public class DefaultParameterRegistryDelegate implements ParameterRegistryDelegate {

    /** Log. */
    private static Log log = LogFactory.getLog(DefaultParameterRegistryDelegate.class);

    /** map klasse - prototype. */
    private Map mapClassPrototype = new HashMap();

    /** de default root groep. */
    private StructuralGroup root;

    /** Data access object parameters. */
    private ParameterDAO parameterDao = new ParameterDAO();

    /** Data access object groepen. */
    private GroupDAO groupDao = new GroupDAO();

    /** Data access object parameters. */
    private ParameterDAO paramDao = new ParameterDAO();

    /**
     * Construct.
     */
    public DefaultParameterRegistryDelegate() {

        //TODO vul de prototype map
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getParameterPrototypes()
     */
    public Parameter[] getParameterPrototypes() {

        return (Parameter[]) mapClassPrototype.values().toArray(
                new Parameter[mapClassPrototype.size()]).clone();
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getParameterPrototype(java.lang.Class)
     */
    public Parameter getParameterPrototype(Class clazz) {

        Parameter param = (Parameter) mapClassPrototype.get(clazz);
        Parameter clone = (Parameter) param.clone();
        return clone;
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getParameter(java.lang.String)
     */
    public Parameter getParameter(String id) throws NotFoundException, RegistryException {

//        StructuralGroup group = getStructuralGroupForEntityId(id);
        Parameter dummy = new StringParameter();
        dummy.setId(id); // we hebben een dummy nodig voor de method call, die
        // weer het id EN de klasse nodig heeft
        Version version = VersionRegistry.getCurrent(dummy);
        return getParameter(id, version);
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getParameter(java.lang.String, nl.openedge.gaps.core.Version)
     */
    public Parameter getParameter(String id, Version version) throws NotFoundException,
            RegistryException {

        Parameter param = null;
        if(id != null) {
	        try {
	            param = parameterDao.findParameter(id, version);
	            if(param == null) {
	                throw new NotFoundException(
	                        "Parameter met id " + id + ", versie " + version
	                        + " niet gevonden");
	            }
	        } catch (ParameterDAOException e) {
	            log.error(e.getMessage(), e);
	            throw new RegistryException(e);
	        }
        }
        return param;
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#saveParameter(nl.openedge.gaps.core.parameters.Parameter)
     */
    public void saveParameter(Parameter parameter) throws SaveException, RegistryException {
        try {
            parameterDao.saveOrUpdateParameter(parameter);
        } catch (ParameterDAOException e) {
            log.error(e.getMessage(), e);
            throw new RegistryException(e);
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#removeParameter(nl.openedge.gaps.core.parameters.Parameter)
     */
    public void removeParameter(Parameter parameter) throws SaveException {
        try {
            parameterDao.deleteParameter(parameter);
        } catch (ParameterDAOException e) {
            log.error(e.getMessage(), e);
            throw new RegistryException(e);
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getRootGroup()
     */
    public StructuralGroup getRootGroup() throws RegistryException {

        return root;
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#setRootGroup(nl.openedge.gaps.core.versions.VersionRegistryDelegate, nl.openedge.gaps.core.groups.StructuralRootGroup)
     */
    public void setRootGroup(VersionRegistryDelegate callee, StructuralRootGroup root) {
        if(callee == null) {
            throw new IllegalStateException("ongeldige aanroeper voor setRootGroup");
        }
        this.root = root;
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getParameterGroup(java.lang.String)
     */
    public ParameterGroup getParameterGroup(String groupId) throws RegistryException,
            NotFoundException {

        ParameterGroup dummy = new ParameterGroup();
        dummy.setId(groupId);
        Version version = VersionRegistry.getCurrent(dummy);
        return getParameterGroup(groupId, version);
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getParameterGroup(java.lang.String,
     *      nl.openedge.gaps.core.Version)
     */
    public ParameterGroup getParameterGroup(String groupId, Version version)
            throws RegistryException, NotFoundException {

        Group group = getGroup(groupId, version);
        if(group instanceof ParameterGroup) {
            return (ParameterGroup)group;
        } else {
            throw new RegistryException("de gevonden groep is geen parametergroep");
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getStructuralGroup(java.lang.String)
     */
    public StructuralGroup getStructuralGroup(String groupId) throws RegistryException,
            NotFoundException {

        StructuralGroup dummyGroup = new StructuralGroup();
        dummyGroup.setId(groupId);
        Version version = VersionRegistry.getCurrent(dummyGroup);
        return getStructuralGroup(groupId, version);
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#getStructuralGroup(java.lang.String,
     *      nl.openedge.gaps.core.Version)
     */
    public StructuralGroup getStructuralGroup(String groupId, Version version)
            throws RegistryException, NotFoundException {

        Group group = getGroup(groupId, version);
        if(group instanceof StructuralGroup) {
            return (StructuralGroup)group;
        } else {
            throw new RegistryException("de gevonden groep is geen structuurgroep");
        }
    }

    /**
     * Haal groep op.
     * @param groupId groep id
     * @param version versie
     * @return de groep
     * @throws RegistryException bij onverwachte fouten
     * @throws NotFoundException indien de groep niet is gevonden
     */
    private Group getGroup(String groupId, Version version)
		throws RegistryException, NotFoundException {

	    try {
	        Group group = groupDao.findGroup(groupId, version);
	        if(group == null) {
	            throw new NotFoundException(
	                    "Groep met id " + groupId + ", versie " + version
	                    + " niet gevonden");
	        } else {
	            return group;
	        }
	    } catch (GroupDAOException e) {
	        log.error(e.getMessage(), e);
	        throw new RegistryException(e);
	    }     
	}

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#saveGroup(StructuralGroup)
     */
    public void saveGroup(StructuralGroup group) throws RegistryException {
        try {
            groupDao.saveOrUpdateGroup(group);
        } catch (GroupDAOException e) {
            log.error(e.getMessage(), e);
            throw new RegistryException(e);
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#saveGroup(nl.openedge.gaps.core.parameters.groups.ParameterGroup)
     */
    public void saveGroup(ParameterGroup group) throws RegistryException {
        try {
            groupDao.saveOrUpdateGroup(group);
        } catch (GroupDAOException e) {
            log.error(e.getMessage(), e);
            throw new RegistryException(e);
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#createVersion(Entity, nl.openedge.gaps.core.versions.Version)
     */
    public void createVersion(
            Entity entity, Version version)
    		throws RegistryException {

        Entity clone = null;
        if(entity == null) {
            entity = (StructuralGroup)getRootGroup().clone();
        }
        clone = (Entity)entity.clone();
        clone.setVersion(version);
        if(log.isDebugEnabled()) {
            log.debug("kopieer " + entity.getId() + " voor nieuwe versie " + version);
        }
        if(clone instanceof Group) {
            if(clone instanceof StructuralGroup) {
                saveGroup((StructuralGroup)clone);
            } else {
                saveGroup((ParameterGroup)clone);
            }
        } else if(clone instanceof Parameter) {
            try {
                saveParameter((Parameter)clone);
            } catch (SaveException e) {
                throw new RegistryException(e);
            }
        } else {
            throw new RegistryException("onbekend entiteittype");
        }
        createVersionForChilds(entity, version);
    }

    /**
     * Maakt kinderen van de gegeven parent voor de gegeven versie.
     * @param parent de parent
     * @param version de versie
     * @throws RegistryException
     */
    private void createVersionForChilds(
            Entity parent, Version version)
			throws RegistryException {

        if(parent instanceof StructuralGroup) {
            StructuralGroup structGroup = (StructuralGroup)parent;
            createVersionForStructuralGroups(structGroup.getStructuralChilds(), version);
            createVersionForParameterGroups(structGroup.getParameterChilds(), version);   
        } else if(parent instanceof ParameterGroup) {
            ParameterGroup paramGroup = (ParameterGroup)parent;
            createVersionForParameterGroup(paramGroup, version);
        }
    }

    /**
     * Creeert de gegeven groepen inclusief alles wat er onder hangt.
     * @param groups de groupen
     * @param version de versie
     * @throws RegistryException
     */
    private void createVersionForStructuralGroups(
            StructuralGroup[] groups, Version version)
    		throws RegistryException {

        if(groups != null) {
            int len = groups.length;
            for(int i = 0; i < len; i++) {
                StructuralGroup clone = (StructuralGroup)groups[i].clone();
                clone.setVersion(version);
                if(log.isDebugEnabled()) {
                    log.debug("kopieer " + groups[i].getId()
                            + " voor nieuwe versie " + version);
                }
                saveGroup(clone);
                createVersionForChilds(groups[i], version);
            }
        }
    }

    /**
     * Creeert de gegeven parametergroepen inclusief alles wat er onder hangt.
     * @param groups de groepen
     * @param version de versie
     * @throws RegistryException
     */
    private void createVersionForParameterGroups(
            ParameterGroup[] groups, Version version)
    		throws RegistryException {

        if(groups != null) {
            int len = groups.length;
            for(int i = 0; i < len; i++) {
                createVersionForParameterGroup(groups[i], version);
            }
        }
    }

    /**
     * Creeert de gegeven parametergroep inclusief alles wat er onder hangt.
     * @param group de groep
     * @param version de versie
     */
    private void createVersionForParameterGroup(
            ParameterGroup group, Version version) {

        ParameterGroup clone = (ParameterGroup)group.clone();
        clone.setVersion(version);
        if(log.isDebugEnabled()) {
            log.debug("kopieer " + group.getId()
                    + " voor nieuwe versie " + version);
        }
        saveGroup(clone);
        createVersionForParameters(group.getParameters(), version);
    }

    /**
     * Creeert de gegeven parameters voor de gegeven versie.
     * @param params de parameters
     * @param version de versie
     * @throws RegistryException
     */
    private void createVersionForParameters(
            Parameter[] params, Version version)
    		throws RegistryException {

        if(params != null) {
            int len = params.length;
            for(int i = 0; i < len; i++) {
                if(log.isDebugEnabled()) {
                    log.debug("kopieer " + params[i].getId()
                            + " voor nieuwe versie " + version);
                }
                Parameter clone = (Parameter)params[i].clone();
                clone.setVersion(version);
                try {
                    saveParameter(clone);
                } catch (SaveException e) {
                    throw new RegistryException(e);
                }
            }   
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#removeGroup(StructuralGroup)
     */
    public void removeGroup(StructuralGroup group) throws NotFoundException, RegistryException {
        try {
            groupDao.deleteGroup(group);
        } catch (GroupDAOException e) {
            log.error(e.getMessage(), e);
            throw new RegistryException(e);
        }
    }

    /**
     * @see nl.openedge.gaps.core.parameters.ParameterRegistryDelegate#createId(nl.openedge.gaps.core.parameters.Entity)
     */
    public String createId(Entity entity) {
        return EntityUtil.createId(entity);
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
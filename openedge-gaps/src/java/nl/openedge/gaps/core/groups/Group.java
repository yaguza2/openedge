/*
 * $Id$ $Revision:
 * 1.3 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.groups;

import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.groups.impl.GroupDAO;
import nl.openedge.gaps.core.groups.impl.GroupDAOException;

/**
 * Basisklasse voor groepen.
 */
public abstract class Group extends Entity {

    /** DAO voor groepen. */
    private static GroupDAO groupDao = new GroupDAO();

    /** Omschrijving van de parametergroep. */
    private String description;

    /** id parent. */
    private String parentId;

    /** localId parent voor makkelijke toegang. */
    private String parentLocalId;

    /**
     * Construct.
     */
    public Group() {
        // niets
    }

    /**
     * Get parent.
     * @return parent.
     */
    public StructuralGroup getParent() {

        StructuralGroup parent = null;
        try {
            parent = (StructuralGroup)groupDao.findGroup(
                    getParentId(), getVersion());
        } catch (GroupDAOException e) {
            throw new RegistryException(e);
        }
        return parent;
    }

    /**
     * Get description.
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description.
     * @param description description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get parentId.
     * @return parentId.
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Set parentId.
     * @param parentId parentId.
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Get parentLocalId.
     * @return parentLocalId.
     */
    public String getParentLocalId() {
        return parentLocalId;
    }

    /**
     * Set parentLocalId.
     * @param parentLocalId parentLocalId.
     */
    public void setParentLocalId(String parentLocalId) {
        this.parentLocalId = parentLocalId;
    }

    /**
     * Geeft het 'gpath' van de huidige groep.
     * @return het 'gpath' van de huidige groep
     */
    public abstract String getPath();

}
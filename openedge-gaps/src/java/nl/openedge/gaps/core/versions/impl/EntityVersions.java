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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Struct voor indexeren entiteiten/ versies.
 */
public final class EntityVersions implements Cloneable, Serializable {

    /**
     * Id van de entiteit.
     */
    private String entityId;

    /**
     * De versie-ids bij deze entiteit.
     */
    private Set versionIds = new HashSet();

    /**
     * Construct.
     */
    public EntityVersions() {
        //
    }

    /**
     * Get entityId.
     * @return entityId.
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Set entityId.
     * @param entityId entityId.
     */
    public void setEntityId(String groupId) {
        this.entityId = groupId;
    }

    /**
     * Get versionIds.
     * @return versionIds.
     */
    public Set getVersionIds() {
        return versionIds;
    }

    /**
     * Set versionIds.
     * @param versionIds versionIds.
     */
    public void setVersionIds(Set versions) {
        this.versionIds = versions;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String clsName = getClass().getName();
        String simpleClsName = clsName.substring(clsName.lastIndexOf('.') + 1);
        StringBuffer b = new StringBuffer(simpleClsName)
        	.append("{entiteit-id = ")
        	.append(getEntityId()).append(", versies(");
        Set vids = getVersionIds();
        if(vids != null && (!vids.isEmpty())) {
            for(Iterator i = vids.iterator(); i.hasNext();) {
                b.append(i.next());
                if(i.hasNext()) {
                    b.append(", ");
                }
            }
        } else {
            b.append("<none>");
        }
        b.append(")}");
        return b.toString();
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

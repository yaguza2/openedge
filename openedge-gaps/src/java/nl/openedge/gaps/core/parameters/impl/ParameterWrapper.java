/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.io.Serializable;

import nl.openedge.util.ser.SerializedAndZipped;

/**
 * Persistentieklasse voor {@link nl.openedge.gaps.core.parameters.Parameter}.
 */
public final class ParameterWrapper implements Serializable, Cloneable {

    /** Opslag id. */
    private Long id;

    /** Pad property. */
    private String path;

    /** Versie id. */
    private String versionId;

    /** data repr. */
    private SerializedAndZipped data;

    /**
     * Construct.
     */
    public ParameterWrapper() {
        //
    }

    /**
     * Get id.
     * @return id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id.
     * @param id id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get data.
     * @return data.
     */
    public SerializedAndZipped getData() {
        return data;
    }

    /**
     * Set data.
     * @param data data.
     */
    public void setData(SerializedAndZipped data) {
        this.data = data;
    }

    /**
     * Get path.
     * @return path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set path.
     * @param path path.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get versionId.
     * @return versionId.
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * Set versionId.
     * @param versionId versionId.
     */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
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

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String clsName = getClass().getName();
        String simpleClsName = clsName.substring(clsName.lastIndexOf('.') + 1);
        int len = 0;
        if(getData() != null && (getData().getCompressedData() != null)) {
            len = getData().getCompressedData().length;
        }
        return simpleClsName + "{id = " + getId() + ", path = "
        	+ getPath() + ", datalength = " + len + "}[" + getVersionId() + "]";
    }
}
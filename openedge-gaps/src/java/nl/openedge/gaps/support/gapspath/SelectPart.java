/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

/**
 * Select onderdeel; een select onderdeel ziet er bijvoorbeel zo uit.
 * <pre>
 *  @id='paramid'
 * </pre>, waarbij de gehele select:
 * <pre>
 *  [@id='paramid' and @version='theversion']
 * </pre>
 * zou kunnen zijn.
 */
public class SelectPart {

    /** 'AND' relatie. */
    public static final String AND_LINK = "AND";

    /** 'OR' relatie. */
    public static final String OR_LINK = "OR";

    /**
     * het parameter gedeelte, bijv 'id' uit.
     * <pre>
     *  @id='paramid
     * </pre>'.
     */
    private String parameter;

    /**
     * het expressie gedeelte, bijv 'paramid' uit.
     * <pre>
     *  @id='paramid
     * </pre>'.
     */
    private String expr;

    /**
     * Construct.
     */
    public SelectPart() {
        //
    }

    /**
     * Get expr.
     * @return expr.
     */
    public String getExpr() {
        return expr;
    }

    /**
     * Set expr.
     * @param expr expr.
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }

    /**
     * Get parameter.
     * @return parameter.
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Set parameter.
     * @param parameter parameter.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
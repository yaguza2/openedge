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
package nl.openedge.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Struct class voor Jaar Maand Dag van een datum.
 * @author Peter Veenendaal (Levob)
 * @author Eelco Hillenius
 */
public final class JaarMaandDag {

    /** Factor jaar; waarde = 10000. */
    private static final int JAAR_FACTOR = 10000;

    /** Factor maand; waarde = 100. */
    private static final int MAAND_FACTOR = 100;

    /** het jaar. */
    private int jaar;

    /** de maand (januari = 1, februari = 2; let op: Calendar telt maanden vanaf 0). */
    private int maand;

    /**
     * Constante voor maand Januari; waarde == 1.
     */
    public static final int JANUARI = 1;
    /**
     * Constante voor maand Februari; waarde == 2.
     */
    public static final int FEBRUARI = 2;
    /**
     * Constante voor maand Maart; waarde == 3.
     */
    public static final int MAART = 3;
    /**
     * Constante voor maand April; waarde == 4.
     */
    public static final int APRIL = 4;
    /**
     * Constante voor maand Mei; waarde == 5.
     */
    public static final int MEI = 5;
    /**
     * Constante voor maand Juni; waarde == 6.
     */
    public static final int JUNI = 6;
    /**
     * Constante voor maand Juli; waarde == 7.
     */
    public static final int JULI = 7;
    /**
     * Constante voor maand Augustus; waarde == 8.
     */
    public static final int AUGUSTUS = 8;
    /**
     * Constante voor maand September; waarde == 9.
     */
    public static final int SEPTEMBER = 9;
    /**
     * Constante voor maand Oktober; waarde == 10.
     */
    public static final int OKTOBER = 10;
    /**
     * Constante voor maand November; waarde == 11.
     */
    public static final int NOVEMBER = 11;
    /**
     * Constante voor maand December; waarde == 12.
     */
    public static final int DECEMBER = 12;

    /**
     * Het aantal maanden in een jaar; waarde = 12.
     */
    public static final int AANTAL_MAANDEN_IN_EEN_JAAR = 12;

    /**
     * Het aantal kwartalen in een jaar; waarde = 4.
     */
    public static final int AANTAL_KWARTALEN_IN_EEN_JAAR = 4;

    /**
     * Het aantal halfjaren in een jaar; waarde = 2.
     */
    public static final int AANTAL_HALFJAREN_IN_EEN_JAAR = 2;

    /** de dag (binnen de maand). */
    private int dag;

    /**
     * Constructor met datum object.
     * @param datum datum object
     */
    public JaarMaandDag(final Date datum) {
        Calendar calendar = getCalendar(datum);
        jaar = calendar.get(Calendar.YEAR);
        maand = JaarMaandDagHelper.getOnzeMaand(calendar.get(Calendar.MONTH));
        dag = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * constructor met jaar en maand; de dag is en blijft gelijk aan 1.
     * @param hetJaar het jaar
     * @param deMaand de maand
     */
    public JaarMaandDag(final int hetJaar, final int deMaand) {
        jaar = hetJaar;
        maand = deMaand;
        while (maand > AANTAL_MAANDEN_IN_EEN_JAAR) {
            maand -= AANTAL_MAANDEN_IN_EEN_JAAR;
            jaar++;
        }
        dag = 1;
    }

    /**
     * Verhoogt <tt>datum</tt> met j, m en d.
     * @param datum begindatum
     * @param j jaar
     * @param m maand
     * @param d dag
     * @return Date verhoogde datum.
     */
    public static Date verhoogDatum(final Date datum, final int j, final int m, final int d) {
        if (datum == null) {
            throw new IllegalArgumentException("datum is leeg.");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datum);
        calendar.add(Calendar.YEAR, j);
        calendar.add(Calendar.MONTH, m);
        calendar.add(Calendar.DATE, d);
        return calendar.getTime();

    }

    /**
     * Hulpmethode voor maken calendar.
     * @param datum datum
     * @return Calendar gevulde calendar object op basis van datum
     */
    private Calendar getCalendar(final Date datum) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datum);
        return calendar;
    }

    /**
     * Geeft Jaar, maand, dag als een getal terug.
     * @return int getal (jaar * 10000 + maand * 100 + dag)
     */
    public int getJaarMaandDagGetal() {
        return (jaar * JAAR_FACTOR + maand * MAAND_FACTOR + dag);
    }

    /**
     * Geeft de dag van een datum terug.
     * @return int de dag.
     */
    public int getDag() {
        return dag;
    }

    /**
     * Geeft het jaar van een datum terug.
     * @return int het jaar.
     */
    public int getJaar() {
        return jaar;
    }

    /**
     * Geeft de maand van een datum terug.
     * @return int de maand (januari = 1, februari = 2; let op: Calendar telt maanden vanaf 0).
     */
    public int getMaand() {
        return maand;
    }

    /**
     * Geeft object als een Date.
     * @return Date object als Date
     */
    public Date toDate() {
        return JaarMaandDagHelper.getDatum(jaar, maand, dag);
    }

}
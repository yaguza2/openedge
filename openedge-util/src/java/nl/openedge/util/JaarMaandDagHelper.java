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
import java.util.Comparator;
import java.util.Date;

import nl.openedge.util.rekenen.Rekenhulp;

/**
 * Comparator uitgebreid met statische hulpmethoden voor het vergelijken van
 * datums waarbij alleen de jaar/ maand/ dag velden worden meegenomen.
 * @author Eelco Hillenius
 */
public final class JaarMaandDagHelper implements Comparator {


    /**
     * Speciale variabele voor testdoeleinden.
     */
    private static Date vandaag = null;

    /**
     * Voor intern gebruik: Vergelijk argument datum1 met datum2 voor ordering.
     * Geeft een negatieve int (-1), 0 (nul) of een positieve int (1) indien datum1
     * kleiner, gelijk aan of groter is dan datum2.
     * @param datum1 de eerste datum
     * @param datum2 de tweede datum
     * @return int -1, 0 of 1 indien datum1 kleiner, gelijk aan of groter is dan
     *         datum2.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final Object datum1, final Object datum2) {
        return internalCompare(datum1, datum2);
    }

    /**
     * Voor intern gebruik: Vergelijk argument datum1 met datum2 voor ordering.
     * Geeft een negatieve int (-1), 0 (nul) of een positieve int (1) indien datum1
     * kleiner, gelijk aan of groter is dan datum2.
     * @param datum1 de eerste datum
     * @param datum2 de tweede datum
     * @return int -1, 0 of 1 indien datum1 kleiner, gelijk aan of groter is dan
     *         datum2.
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    protected static int internalCompare(
        final Object datum1,
        final Object datum2) {
        if (datum1 == null || datum2 == null) {
            throw new IllegalArgumentException(
                "ongeldige argumenten: " + datum1 + ", " + datum2);
        }

        JaarMaandDag jmd1 = new JaarMaandDag((Date) datum1);
        JaarMaandDag jmd2 = new JaarMaandDag((Date) datum2);

        int jaarVerschil = jmd1.getJaar() - jmd2.getJaar();
        int maandVerschil = jmd1.getMaand() - jmd2.getMaand();
        int dagVerschil = jmd1.getDag() - jmd2.getDag();

        if (jaarVerschil != 0) {
            return Rekenhulp.sign(jaarVerschil);
        } else if (maandVerschil != 0) {
            return Rekenhulp.sign(maandVerschil);
        } else if (dagVerschil != 0) {
            return Rekenhulp.sign(dagVerschil);
        } else {
            return 0;
        }
    }

    /**
     * Maak Date object op basis van jaar, maand en dag van de maand.
     * @param jaar jaar
     * @param maand maand (januari = 1, februari = 2; let op: Calendar telt maanden vanaf 0)
     * @param dagInMaand dag van de maand
     * @return Date date object op basis van gegeven jaar, maand en dag van de maand
     */
    public static Date getDatum(final int jaar, final int maand, final int dagInMaand) {
        Calendar calendar = getCalendar(jaar, maand, dagInMaand);
        return calendar.getTime();
    }

    /**
     * Maak Calendar object op basis van jaar, maand en dag van de maand.
     * @param jaar jaar
     * @param maand maand (januari = 1, februari = 2; let op: Calendar telt maanden vanaf 0)
     * @param dagInMaand dag van de maand
     * @return Calendar calendar object op basis van gegeven jaar, maand en dag van de maand
     */
    public static Calendar getCalendar(final int jaar, final int maand, final int dagInMaand) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, jaar);
        calendar.set(Calendar.MONTH, getCalendarMaand(maand));
        calendar.set(Calendar.DAY_OF_MONTH, dagInMaand);
        return calendar;
    }

    /**
     * Geeft of datum1 na datum2 is.
     * @param datum1 eerste datum
     * @param datum2 tweede datum
     * @return boolean of datum1 na datum2 is
     */
    public static boolean isDatum1NaDatum2(final Date datum1, final Date datum2) {
        return (internalCompare(datum1, datum2) > 0);
    }

    /**
     * Geeft of datum1 na of op datum2 is.
     * @param datum1 eerste datum
     * @param datum2 tweede datum
     * @return boolean of datum1 na of op datum2 is
     */
    public static boolean isDatum1NaOfOpDatum2(final Date datum1, final Date datum2) {
        return (internalCompare(datum1, datum2) >= 0);
    }

    /**
     * Geeft of datum1 voor datum2 is.
     * @param datum1 eerste datum
     * @param datum2 tweede datum
     * @return boolean of datum1 voor datum2 is
     */
    public static boolean isDatum1VoorDatum2(final Date datum1, final Date datum2) {
        return (internalCompare(datum1, datum2) < 0);
    }

    /**
     * Geeft of datum1 voor of op datum2 is.
     * @param datum1 eerste datum
     * @param datum2 tweede datum
     * @return boolean of datum1 voor of op datum2 is
     */
    public static boolean isDatum1VoorOfOpDatum2(final Date datum1, final Date datum2) {
        return (internalCompare(datum1, datum2) <= 0);
    }

    /**
     * Geeft of datum1 gelijk aan datum2 is.
     * @param datum1 eerste datum
     * @param datum2 tweede datum
     * @return boolean of datum1 gelijk aan datum2 is
     */
    public static boolean isDatum1GelijkAanDatum2(final Date datum1, final Date datum2) {
        return (internalCompare(datum1, datum2) == 0);
    }

    /**
     * Het Calendar component telt maanden vanaf 0; wij tellen vanaf 1, deze
     * methode past dat aan.
     * @param onzeMaand de te converteren maand
     * @return int geconverteerde maand (gegeven maand - 1)
     */
    public static int getCalendarMaand(final int onzeMaand) {
        return onzeMaand - 1;
    }

    /**
     * Het Calendar component telt maanden vanaf 0; wij tellen vanaf 1, deze
     * methode past dat aan.
     * @param calendarMaand de te converteren maand
     * @return int geconverteerde maand (gegeven maand + 1)
     */
    public static int getOnzeMaand(final int calendarMaand) {
        return calendarMaand + 1;
    }

    /**
     * Geef de datum van vandaag. Indien statische variable 'vandaag'
     * is gezet, wordt deze geretourneerd... gebruik alleen bij testen in
     * dat geval.
     * @return Date datum
     */
    public static Date getVandaag() {
        if (vandaag == null) {
            Calendar calendar = Calendar.getInstance();
            int jaar = calendar.get(Calendar.YEAR);
            int maand = calendar.get(Calendar.MONTH);
            int dag = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.clear(); // wis alle velden
            calendar.set(jaar, maand, dag); // en zet alleen de interessante terug
            return calendar.getTime();
        } else {
            return vandaag;
        }
    }
    /**
     * Speciale methode voor testen; als vandaag wordt gezet wordt
     * deze datum altijd teruggegeven als de datum van vandaag, anders
     * wordt de werkelijke datum van vandaag gegeven. Geef null om weer
     * op de echte datum van vandaag over te gaan;
     * @param testVandaag te zetten datum.
     */
    public static void setVandaag(final Date testVandaag) {
        vandaag = testVandaag;
    }
}
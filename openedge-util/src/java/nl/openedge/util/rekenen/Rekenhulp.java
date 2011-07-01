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
package nl.openedge.util.rekenen;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import nl.openedge.util.YearMonthDay;

/**
 * Bevat allerlei rekenfuncties die {@link java.lang.Math}niet ondersteunt.
 * 
 * @author Martijn Dashorst
 */
public final class Rekenhulp
{
	/**
	 * Constante voor het afronden op 4 decimalen.
	 */
	public static final int AFRONDING_DECIMALEN_4 = 4;

	/**
	 * Constante voor het afronden op 2 decimalen.
	 */
	public static final int AFRONDING_DECIMALEN_2 = 2;

	/**
	 * Constante voor 1 procent.
	 */
	public static final double EEN_PERCENT = 0.01d;

	/**
	 * Constante voor 100 procent.
	 */
	public static final double HON_PERCENT = 100d;

	/**
	 * Private Constructor i.v.m. Utility class.
	 */
	private Rekenhulp()
	{
	}

	/**
	 * Rondt <code>waarde</code> af op <code>posities</code> decimalen. Voorbeeld:
	 * <ul>
	 * <li><code>rondAf(2.5, 0) = 3</code></li>
	 * <li><code>rondAf(2.4, 0) = 2</code></li>
	 * <li><code>rondAf(2.54, 1) = 2.5</code></li>
	 * </ul>
	 * 
	 * @param waarde
	 *            de waarde die op <code>posities</code> afgerond moet worden.
	 * @param posities
	 *            het aantal posities achter de comma waarop afgerond moet worden.
	 * @return de afgeronde <code>waarde</code>
	 */
	public static double rondAf(final double waarde, final int posities)
	{
		final BigDecimal tijdelijk = new BigDecimal(waarde);
		final BigDecimal resultaat = tijdelijk.setScale(posities, BigDecimal.ROUND_HALF_UP);
		return resultaat.doubleValue();
	}

	/**
	 * Rondt <code>waarde</code> af op 0 decimalen.
	 * 
	 * @param waarde
	 *            de waarde die op <code>posities</code> afgerond moet worden.
	 * @return de afgeronde <code>waarde</code>
	 */
	public static double rondAf(final double waarde)
	{
		return rondAf(waarde, 0);
	}

	/**
	 * Rondt <code>waarde</code> af op 2 decimalen.
	 * 
	 * @param waarde
	 *            de waarde die op <code>posities</code> afgerond moet worden.
	 * @return de afgeronde <code>waarde</code>
	 */
	public static double rondAf2(final double waarde)
	{
		return rondAf(waarde, AFRONDING_DECIMALEN_2);
	}

	/**
	 * Rondt <code>waarde</code> af op 4 decimalen.
	 * 
	 * @param waarde
	 *            de waarde die op <code>posities</code> afgerond moet worden.
	 * @return de afgeronde <code>waarde</code>
	 */
	public static double rondAf4(final double waarde)
	{
		return rondAf(waarde, AFRONDING_DECIMALEN_4);
	}

	/**
	 * Geeft het teken terug van de <code>integer</code>.
	 * 
	 * @param integer
	 *            het getal waarvan het teken bepaald moet worden.
	 * @return 1 als integer > 0, -1 als integer < 0, 0 als integer == 0.
	 */
	public static int sign(final int integer)
	{
		if (integer < 0)
		{
			return -1;
		}
		if (integer > 0)
		{
			return 1;
		}
		return 0;
	}

	/**
	 * Berekent het verschil in jaren tussen <tt>datum1</tt> en <tt>datum2</tt>. Als datum1 >
	 * datum2 levert deze functie nog steeds een verschil > 0. Het betreft dus het daadwerkelijke
	 * verschil in jaren tussen de twee datums.
	 * 
	 * @param datum1
	 *            eerste datum.
	 * @param datum2
	 *            tweede datum
	 * @return het verschil in jaren tussen <tt>datum1</tt> en <tt>datum2</tt>.
	 */
	public static int verschilJaren(final Date datum1, final Date datum2)
	{
		if (datum1 == null)
		{
			throw new IllegalArgumentException("datum1 is leeg.");
		}
		if (datum2 == null)
		{
			throw new IllegalArgumentException("datum2 is leeg.");
		}
		Calendar calendar0 = Calendar.getInstance();
		Calendar calendar1 = Calendar.getInstance();
		if (datum1.before(datum2))
		{
			calendar0.setTime(datum1);
			calendar1.setTime(datum2);
		}
		else
		{
			calendar0.setTime(datum2);
			calendar1.setTime(datum1);
		}
		int jaren0 = calendar0.get(Calendar.YEAR);
		int jaren1 = calendar1.get(Calendar.YEAR);

		int maanden0 = calendar0.get(Calendar.MONTH);
		int maanden1 = calendar1.get(Calendar.MONTH);

		int dag0 = calendar0.get(Calendar.DAY_OF_MONTH);
		int dag1 = calendar1.get(Calendar.DAY_OF_MONTH);

		int jarenVerschil = jaren1 - jaren0;
		int maandenVerschil = maanden1 - maanden0;

		if (dag0 > dag1)
		{
			maandenVerschil -= 1;
		}

		if (maandenVerschil < 0)
		{
			jarenVerschil -= 1;
		}
		return Math.abs(jarenVerschil);
	}

	/**
	 * Berekent het verschil in maanden tussen <tt>datum1</tt> en <tt>datum2</tt>. Als datum1 >
	 * datum2 levert deze functie nog steeds een verschil > 0. Het betreft dus het daadwerkelijke
	 * verschil in maanden tussen de twee datums.
	 * 
	 * @param datum1
	 *            eerste datum.
	 * @param datum2
	 *            tweede datum
	 * @return het verschil in maanden tussen <tt>datum1</tt> en <tt>datum2</tt>.
	 */
	public static int verschilMaanden(final Date datum1, final Date datum2)
	{
		if (datum1 == null)
		{
			throw new IllegalArgumentException("datum1 is leeg.");
		}
		if (datum2 == null)
		{
			throw new IllegalArgumentException("datum2 is leeg.");
		}
		Calendar calendar0 = Calendar.getInstance();
		Calendar calendar1 = Calendar.getInstance();
		if (datum1.before(datum2))
		{
			calendar0.setTime(datum1);
			calendar1.setTime(datum2);
		}
		else
		{
			calendar0.setTime(datum2);
			calendar1.setTime(datum1);
		}

		int maanden0 = calendar0.get(Calendar.MONTH);
		int maanden1 = calendar1.get(Calendar.MONTH);
		int dag0 = calendar0.get(Calendar.DAY_OF_MONTH);
		int dag1 = calendar1.get(Calendar.DAY_OF_MONTH);

		int maandenVerschil = maanden1 - maanden0;
		if (dag0 > dag1)
		{
			maandenVerschil -= 1;
		}

		if (maandenVerschil < 0)
		{
			maandenVerschil += YearMonthDay.NUMBER_OF_MONTHS_IN_A_YEAR;
		}
		return maandenVerschil;
	}

	/**
	 * Berekent het rente op rente percentage dat per termijn nodig is om uit te komen op
	 * <tt>percentage</tt> over de gehele periode. Bijvoorbeeld: als de jaarrente 5% is, is per
	 * halfjaar (2 termijnen) een rente op rente nodig van (ongeveer) 2.47%
	 * 
	 * @param aantalTermijnen
	 *            het aantal termijnen waarover de rente op rente uitgesmeerd moet worden.
	 * @param percentage
	 *            het percentage dat effectief moet gelden over het aantal <tt>termijnen</tt>.
	 * @return het rente op rente percentage per termijn om effectief op <tt>percentage</tt> uit
	 *         te komen.
	 */
	public static double termPercentage(final int aantalTermijnen, final double percentage)
	{
		double p = 1 + percentage * EEN_PERCENT;
		double m = 1 / (double) aantalTermijnen;
		return (Math.pow(p, m) - 1d) * HON_PERCENT;
	}

	/**
	 * Berekent de <tt>c</tt> -de-machts wortel van <tt>d</tt>, beter bekend als
	 * <tt>d<sup>1/c</sup></tt>.
	 * 
	 * @param d
	 *            de basis.
	 * @param c
	 *            de macht.
	 * @return de <tt>c</tt> -de machts wortel van <tt>d</tt>.
	 */
	public static double root(final double d, final double c)
	{
		double temp = Math.log(d);
		return Math.exp(temp / c);
	}

	/**
	 * Interpoleert de bedragen evenredig over een jaar gegeven het aantal maanden waarvoor het
	 * eerste bedrag mee telt.
	 * 
	 * @param maandenEerstePeriode
	 *            het aantal maanden waarvoor het bedragEerstePeriode geldig is.
	 * @param bedragEerstePeriode
	 *            het bedrag dat het aantal maanden in de eerste periode geldig is.
	 * @param bedragTweedePeriode
	 *            het bedrag dat <tt>12 - maandenEerstePeriode</tt> geldig is.
	 * @return het geinterpoleerde bedrag afgerond op 4 decimalen. voorbeelden ter verduidelijking:
	 *         de duur is 22 jaar precies : (bedrag2 * 0 + bedrag1 * 12) / 12 = bedrag1 de duur is
	 *         22 jaar en 6 maanden: (bedrag2 * 6 + bedrag1 * 6) / 12 = 50% bedrag1 en 50% bedrag2
	 *         de duur is 22 jaar en 3 maanden: (bedrag2 * 3 + bedrag1 * 9) / 12 = 75% bedrag1 en
	 *         25% bedrag2 de duur is 22 jaar en 9 maanden: (bedrag2 * 3 + bedrag1 * 9) / 12 = 25%
	 *         bedrag1 en 75% bedrag2
	 */
	public static double interpoleerOverJaar(final int maandenEerstePeriode,
			final double bedragEerstePeriode, final double bedragTweedePeriode)
	{
		double bedrag = (maandenEerstePeriode * bedragTweedePeriode
				+ (YearMonthDay.NUMBER_OF_MONTHS_IN_A_YEAR - maandenEerstePeriode)
				* bedragEerstePeriode)
				/ YearMonthDay.NUMBER_OF_MONTHS_IN_A_YEAR;
		return Rekenhulp.rondAf4(bedrag);
	}

	/**
	 * Slotwaarde.
	 * 
	 * @param n
	 *            duur
	 * @param i
	 *            percentage intrest
	 * @return double slotwaarde.
	 */
	public static double slotWaarde(final int n, final double i)
	{
		return Math.pow(1d + i * EEN_PERCENT, n);
	}

	/**
	 * Contante waarde.
	 * 
	 * @param n
	 *            duur
	 * @param i
	 *            percentage intrest
	 * @return double contante waarde.
	 */
	public static double contanteWaarde(final int n, final double i)
	{
		return Math.pow(1d / (1d + i * EEN_PERCENT), n);
	}

	/**
	 * Praenumerando Annuiteit.
	 * 
	 * @param n
	 *            duur
	 * @param i
	 *            percentage intrest
	 * @return double Praenumerando Annuiteit.
	 */
	// 100 procent = 1 / EEN_PERCENT.
	public static double praenumerandoAnnuiteit(final int n, final double i)
	{
		return (1d - contanteWaarde(n, i)) * (HON_PERCENT + i) / i;
	}

	/**
	 * Postnumerando Annuiteit.
	 * 
	 * @param n
	 *            duur
	 * @param i
	 *            percentage intrest
	 * @return double postnumerande Annuiteit.
	 */
	public static double postnumerandoAnnuitet(final int n, final double i)
	{
		return (1d - contanteWaarde(n, i)) * HON_PERCENT / i;
	}

	/**
	 * Praenumerando Annuiteit in termijnen nauwkeurig.
	 * 
	 * @param n
	 *            duur
	 * @param t
	 *            termijnen in een jaar
	 * @param i
	 *            percentage intrest
	 * @return double Praenumerando Annuiteit in termijnen nauwkeurig.
	 */
	public static double praenumerandoTermAnnuiteit(final int n, final int t, final double i)
	{
		double n1 = Math.floor((double) (n * t));
		double i1 = termPercentage(t, i) / HON_PERCENT;
		double r = 1d / (1d + i1);
		double d = r;
		return (1d - Math.pow(d, n1)) / (i1 * r);
	}

	/**
	 * Postnumerando Annuiteit in termijnen nauwkeurig.
	 * 
	 * @param n
	 *            duur
	 * @param t
	 *            termijnen in een jaar
	 * @param i
	 *            percentage intrest
	 * @return doubel Postnumerando Annuiteit in termijnen nauwkeurig.
	 */
	public static double postnumerandoTermAnnuiteit(final int n, final int t, final double i)
	{
		double n1 = Math.floor((double) (n * t));
		double i1 = termPercentage(t, i) / HON_PERCENT;
		double r = 1d / (1d + i1);
		return (1d - Math.pow(r, n1)) / i1;
	}

	/**
	 * Slotwaarde Praenumerando AnnuïTeit.
	 * 
	 * @param n
	 *            duur
	 * @param i
	 *            percentage intrest
	 * @return double Slotwaarde Praenumerando AnnuïTeit
	 */
	public static double slotwaardePraenumerandoAnnuiTeit(final int n, final double i)
	{
		return (slotWaarde(n, i) - 1d) * (HON_PERCENT + i) / i;
	}

	/**
	 * Slotwaarde Postnumerando AnnuïTeit.
	 * 
	 * @param n
	 *            duur
	 * @param i
	 *            percntege intrest
	 * @return double Slotwaarde Postnumerando AnnuïTeit
	 */
	public static double slotwaardePostnumerandoAnnuiTeit(final int n, final double i)
	{
		return (slotWaarde(n, i) - 1d) * HON_PERCENT / i;
	}

	/**
	 * Slotwaarde Praenumerando AnnuïTeit in termijnen nauwkeurig.
	 * 
	 * @param n
	 *            duur
	 * @param t
	 *            termijnen in een jaar
	 * @param i
	 *            percentage intrest
	 * @return double Slotwaarde Praenumerando AnnuïTeit in termijnen nauwkeurig.
	 */
	public static double slotwaardePraenumerandoTermAnnuiTeit(final int n, final int t,
			final double i)
	{
		double n1 = Math.floor(n * t);
		double i1 = termPercentage(t, i) / HON_PERCENT;
		double r = 1d + i1;
		double d = r;
		return (Math.pow(d, n1) - 1d) / (i1 / r);
	}

	/**
	 * Slotwaarde Postnumerando AnnuïTeit in termijnen nauwkeurig.
	 * 
	 * @param n
	 *            duur
	 * @param t
	 *            termijnen in een jaar
	 * @param i
	 *            percentage intrest
	 * @return double Slotwaarde Postnumerando AnnuïTeit in termijnen nauwkeurig
	 */
	public static double slotwaardePostnumerandoTermAnnuiTeit(final int n, final int t,
			final double i)
	{
		double n1 = Math.floor(n * t);
		double i1 = termPercentage(t, i) / HON_PERCENT;
		double r = 1d + i1;
		return (Math.pow(r, n1) - 1d) / i1;
	}

	/**
	 * Slotwaarde in termijnen nauwkeurig.
	 * 
	 * @param n
	 *            duur
	 * @param t
	 *            termijnen in een jaar
	 * @param i
	 *            percentage intrest
	 * @return .
	 */
	public static double termSlotwaarde(final int n, final int t, final double i)
	{
		double n1 = Math.floor(n * t);
		double r = 1d + termPercentage(t, t) / HON_PERCENT;
		return Math.pow(r, n1);
	}

}
/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.berekeningen;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Map;

import nl.openedge.gaps.support.berekeningen.lexer.Lexer;
import nl.openedge.gaps.support.berekeningen.lexer.LexerException;
import nl.openedge.gaps.support.berekeningen.node.Start;
import nl.openedge.gaps.support.berekeningen.parser.Parser;
import nl.openedge.gaps.support.berekeningen.parser.ParserException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Hulpklasse voor het uitvoeren van berekeningen die als strings worden aangeboden.
 */
public final class BerekeningInterpreter
{
	/** Buffer grootte van de pushback reader. */
	private static final int PB_BUFFER_SIZE = 1024;

	/** Log. */
	private static Log log = LogFactory.getLog(BerekeningInterpreter.class);

	/**
	 * Verborgen utility constructor.
	 */
	private BerekeningInterpreter()
	{
		//
	}

	/**
	 * Evalueer de gegeven berekenings-expressie met de gegeven context (mag null zijn) en
	 * geef het resultaat terug.
	 * @param expression de te evalueren expressie
	 * @param context de context met evt input parameters
	 * @return Double resultaat van de berekeningsevaluatie
	 * @throws BerekeningInterpreterException bij evaluatiefouten
	 */
	public static Double evaluate(String expression, Map context)
			throws BerekeningInterpreterException
	{

		Double result = null;
		Start tree = null;
		try
		{
			tree = parse(expression);
			BerekeningSolver translation = new BerekeningSolver();
			translation.setContext(context);
			tree.apply(translation);
			result = translation.getResult();

		}
		catch (Exception e)
		{
			throw new BerekeningInterpreterException(e);
		}

		return result;
	}

	/**
	 * Evalueer de gegeven berekenings-expressie en geef het resultaat terug.
	 * @param expression de te evalueren expressie
	 * @return Double resultaat van de berekeningsevaluatie
	 * @throws BerekeningInterpreterException bij evaluatiefouten
	 */
	public static Double evaluate(String expression)
			throws BerekeningInterpreterException
	{

		return evaluate(expression, null);
	}

	/**
	 * Parse expressie.
	 * @param expression de te parsen expressie
	 * @return Start de root node van de resultaat tree
	 * @throws ParserException zie ParserException
	 * @throws LexerException zie LexerException
	 * @throws IOException bij een leesfout
	 */
	public static Start parse(String expression) throws ParserException, LexerException,
			IOException
	{

		Start tree;
		StringReader reader = new StringReader(expression);
		PushbackReader pushbackReader = new PushbackReader(reader, PB_BUFFER_SIZE);
		Lexer lexer = new Lexer(pushbackReader);
		Parser p = new Parser(lexer);
		tree = p.parse();
		if (log.isDebugEnabled())
		{
			log.debug("AST '" + expression + "':");
			ASTPrinter printer = new ASTPrinter(); // gaat gewoon naar STDOUT
			tree.apply(printer);
		}
		return tree;
	}

	/**
	 * Main methode.
	 * @param args argumenten
	 */
	public static void main(String[] args)
	{

		Start tree = null;
		String expression = "1 + 3 * (4 + 8 / 2)";
		try
		{
			tree = parse(expression);
			ASTPrinter printer = new ASTPrinter();
			tree.apply(printer);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
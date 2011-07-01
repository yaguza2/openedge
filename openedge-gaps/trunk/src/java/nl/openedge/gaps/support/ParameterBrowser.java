/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nl.openedge.gaps.support.gapspath.GPathInterpreter;
import nl.openedge.gaps.support.gapspath.GPathInterpreterException;
import nl.openedge.gaps.support.gapspath.GPathTranslator;
import nl.openedge.gaps.support.gapspath.lexer.LexerException;
import nl.openedge.gaps.support.gapspath.node.Start;
import nl.openedge.gaps.support.gapspath.parser.ParserException;

/**
 * Implementeert browser gedrag voor groepen en parameters.
 */
public final class ParameterBrowser
{

	/** Log. */
	private static Log log = LogFactory.getLog(ParameterBrowser.class);

	/**
	 * parser/ interpreter.
	 */
	private GPathTranslator translator = new GPathTranslator();

	/**
	 * Construct.
	 */
	public ParameterBrowser()
	{
		super();
	}

	/**
	 * Voer query uit en retourneer de resulterende positie/ het resultaat (zelfde als
	 * getCurrentPosition()).
	 * @param query navigatie query
	 * @return huidige positie/ resultaat (zelfde als getCurrentPosition())
	 * @throws GPathInterpreterException indien de query niet kan worden uitgevoerd
	 */
	public Object navigate(String query) throws GPathInterpreterException
	{

		return internalEvaluate(translator, query);
	}

	/**
	 * Voer query uit en retourneer de resulterende positie/ het resultaat zonder deze
	 * positie/ resultaat current te maken.
	 * @param query navigatie query
	 * @return resulterende positie/ resultaat
	 * @throws GPathInterpreterException indien de query niet kan worden uitgevoerd
	 */
	public Object evaluate(String query) throws GPathInterpreterException
	{

		return internalEvaluate(new GPathTranslator(), query);
	}

	/**
	 * Evalueer query met het gegeven translatie object en retourneer het resultaat.
	 * @param trans te gebruiken gpath translator
	 * @param query de query
	 * @return resultaat parse/ translatie
	 * @throws GPathInterpreterException indien de query niet kan worden uitgevoerd
	 */
	private Object internalEvaluate(GPathTranslator trans, String query)
			throws GPathInterpreterException
	{

		translator.setQuery(query);
		Start tree;
		try
		{
			tree = GPathInterpreter.parse(query);
			tree.apply(trans);
		}
		catch (ParserException e)
		{
			throwException(query, e);
		}
		catch (LexerException e)
		{
			throwException(query, e);
		}
		catch (IOException e)
		{
			throwException(query, e);
		}
		return trans.getCurrentPosition();
	}

	/**
	 * Log en gooi daarna GPathInterpreterException.
	 * @param query de query
	 * @param e exceptie
	 */
	private void throwException(String query, Exception e)
	{
		log.error("exception voor query " + query + ":", e);
		throw new GPathInterpreterException(e);
	}

	/**
	 * Get currentPosition.
	 * @return currentPosition.
	 */
	public Object getCurrentPosition()
	{
		return translator.getCurrentPosition();
	}

	/**
	 * Get lastQuery.
	 * @return lastQuery.
	 */
	public String getLastQuery()
	{
		return translator.getQuery();
	}
}


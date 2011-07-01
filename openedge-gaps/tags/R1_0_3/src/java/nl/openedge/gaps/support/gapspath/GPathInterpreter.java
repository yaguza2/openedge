/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support.gapspath;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import nl.openedge.gaps.support.gapspath.lexer.Lexer;
import nl.openedge.gaps.support.gapspath.lexer.LexerException;
import nl.openedge.gaps.support.gapspath.node.Start;
import nl.openedge.gaps.support.gapspath.parser.Parser;
import nl.openedge.gaps.support.gapspath.parser.ParserException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Hulpklasse voor interpretatie van GPATH expressies.
 */
public final class GPathInterpreter
{

	/** Buffer grootte van de pushback reader. */
	private static final int PB_BUFFER_SIZE = 1024;

	/** Log. */
	private static Log log = LogFactory.getLog(GPathInterpreter.class);

	/**
	 * Verborgen utility constructor.
	 */
	private GPathInterpreter()
	{
		//
	}

	/**
	 * Parse expressie.
	 * @param expression de te parsen expressie
	 * @return Start de root node van de resultaat tree
	 * @throws ParserException zie ParserException
	 * @throws LexerException zie LexerException
	 * @throws IOException bij onverwachte leesfouten
	 */
	public static Start parse(String expression) throws ParserException, LexerException,
			IOException
	{

		Start tree = null;
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
	 * Debug methode.
	 * @param expression de expressie die dient te worden geevalueerd
	 * @throws ParserException zie ParserException
	 * @throws LexerException zie LexerException
	 * @throws IOException bij onverwachte leesfouten
	 */
	private static void exec(String expression) throws ParserException, LexerException,
			IOException
	{
		Start tree;
		tree = parse(expression);
		ASTPrinter printer = new ASTPrinter();
		tree.apply(printer);
	}

	/**
	 * Main methode.
	 * @param args argumenten
	 */
	public static void main(String[] args)
	{

		try
		{

			exec("/");
			exec("/foo/bar");
			exec("./foo/bar");
			exec("../foo/bar");
			exec("foo/../foo/bar");
			exec("/foo/bar:pgroup");
			exec(".:pgroup");
			exec("foo/../foo/bar:pgroup");
			exec("/foo/bar:pgroup/2");
			exec("/foo/bar:pgroup/2@value");
			exec("/foo/bar:pgroup/2['blah']@value");
			exec("/foo/bar:pgroup/2['blah']@value{version='3'}");
			exec("/foo/bar{version='1'}");
			exec("/foo/bar:pgroup{version='1'}");
			exec("/foo/bar:pgroup/2{version='1'}");
			exec("/foo/bar:pgroup/2@value{version='1'}");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.berekeningen.node;

import nl.openedge.gaps.support.berekeningen.analysis.Analysis;

public final class EOF extends Token
{

	public EOF()
	{
		setText("");
	}

	public EOF(int line, int pos)
	{
		setText("");
		setLine(line);
		setPos(pos);
	}

	public Object clone()
	{
		return new EOF(getLine(), getPos());
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseEOF(this);
	}
}
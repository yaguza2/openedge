/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.node;

import nl.openedge.gaps.support.gapspath.analysis.Analysis;

public final class TOr extends Token
{

	public TOr()
	{
		super.setText("or");
	}

	public TOr(int line, int pos)
	{
		super.setText("or");
		setLine(line);
		setPos(pos);
	}

	public Object clone()
	{
		return new TOr(getLine(), getPos());
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseTOr(this);
	}

	public void setText(String text)
	{
		throw new RuntimeException("Cannot change TOr text.");
	}
}
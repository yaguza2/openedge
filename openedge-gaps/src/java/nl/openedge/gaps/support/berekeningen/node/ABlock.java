/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.berekeningen.node;

import nl.openedge.gaps.support.berekeningen.analysis.Analysis;

public final class ABlock extends PBlock
{

	private PExp _exp_;

	public ABlock()
	{
	}

	public ABlock(PExp _exp_)
	{
		setExp(_exp_);

	}

	public Object clone()
	{
		return new ABlock((PExp) cloneNode(_exp_));
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseABlock(this);
	}

	public PExp getExp()
	{
		return _exp_;
	}

	public void setExp(PExp node)
	{
		if (_exp_ != null)
		{
			_exp_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_exp_ = node;
	}

	public String toString()
	{
		return "" + toString(_exp_);
	}

	void removeChild(Node child)
	{
		if (_exp_ == child)
		{
			_exp_ = null;
			return;
		}

	}

	void replaceChild(Node oldChild, Node newChild)
	{
		if (_exp_ == oldChild)
		{
			setExp((PExp) newChild);
			return;
		}

	}
}
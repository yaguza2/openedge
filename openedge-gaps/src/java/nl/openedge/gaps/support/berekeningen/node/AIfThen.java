/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.berekeningen.node;

import nl.openedge.gaps.support.berekeningen.analysis.Analysis;

public final class AIfThen extends PIfThen
{

	private PExp _condition_;

	private PBlock _true_;

	public AIfThen()
	{
	}

	public AIfThen(PExp _condition_, PBlock _true_)
	{
		setCondition(_condition_);

		setTrue(_true_);

	}

	public Object clone()
	{
		return new AIfThen((PExp) cloneNode(_condition_), (PBlock) cloneNode(_true_));
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseAIfThen(this);
	}

	public PExp getCondition()
	{
		return _condition_;
	}

	public void setCondition(PExp node)
	{
		if (_condition_ != null)
		{
			_condition_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_condition_ = node;
	}

	public PBlock getTrue()
	{
		return _true_;
	}

	public void setTrue(PBlock node)
	{
		if (_true_ != null)
		{
			_true_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_true_ = node;
	}

	public String toString()
	{
		return "" + toString(_condition_) + toString(_true_);
	}

	void removeChild(Node child)
	{
		if (_condition_ == child)
		{
			_condition_ = null;
			return;
		}

		if (_true_ == child)
		{
			_true_ = null;
			return;
		}

	}

	void replaceChild(Node oldChild, Node newChild)
	{
		if (_condition_ == oldChild)
		{
			setCondition((PExp) newChild);
			return;
		}

		if (_true_ == oldChild)
		{
			setTrue((PBlock) newChild);
			return;
		}

	}
}
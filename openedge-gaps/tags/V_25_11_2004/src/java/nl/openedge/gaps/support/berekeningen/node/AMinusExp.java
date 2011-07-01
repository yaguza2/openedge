/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.berekeningen.node;

import nl.openedge.gaps.support.berekeningen.analysis.Analysis;

public final class AMinusExp extends PExp
{

	private PExp _left_;

	private PExp _right_;

	public AMinusExp()
	{
	}

	public AMinusExp(PExp _left_, PExp _right_)
	{
		setLeft(_left_);

		setRight(_right_);

	}

	public Object clone()
	{
		return new AMinusExp((PExp) cloneNode(_left_), (PExp) cloneNode(_right_));
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseAMinusExp(this);
	}

	public PExp getLeft()
	{
		return _left_;
	}

	public void setLeft(PExp node)
	{
		if (_left_ != null)
		{
			_left_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_left_ = node;
	}

	public PExp getRight()
	{
		return _right_;
	}

	public void setRight(PExp node)
	{
		if (_right_ != null)
		{
			_right_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_right_ = node;
	}

	public String toString()
	{
		return "" + toString(_left_) + toString(_right_);
	}

	void removeChild(Node child)
	{
		if (_left_ == child)
		{
			_left_ = null;
			return;
		}

		if (_right_ == child)
		{
			_right_ = null;
			return;
		}

	}

	void replaceChild(Node oldChild, Node newChild)
	{
		if (_left_ == oldChild)
		{
			setLeft((PExp) newChild);
			return;
		}

		if (_right_ == oldChild)
		{
			setRight((PExp) newChild);
			return;
		}

	}
}
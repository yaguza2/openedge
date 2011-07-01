/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.node;

import nl.openedge.gaps.support.gapspath.analysis.Analysis;

public final class AAttribute extends PAttribute
{

	private TAt _at_;

	private TId _expr_;

	public AAttribute()
	{
	}

	public AAttribute(TAt _at_, TId _expr_)
	{
		setAt(_at_);

		setExpr(_expr_);

	}

	public Object clone()
	{
		return new AAttribute((TAt) cloneNode(_at_), (TId) cloneNode(_expr_));
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseAAttribute(this);
	}

	public TAt getAt()
	{
		return _at_;
	}

	public void setAt(TAt node)
	{
		if (_at_ != null)
		{
			_at_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_at_ = node;
	}

	public TId getExpr()
	{
		return _expr_;
	}

	public void setExpr(TId node)
	{
		if (_expr_ != null)
		{
			_expr_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_expr_ = node;
	}

	public String toString()
	{
		return "" + toString(_at_) + toString(_expr_);
	}

	void removeChild(Node child)
	{
		if (_at_ == child)
		{
			_at_ = null;
			return;
		}

		if (_expr_ == child)
		{
			_expr_ = null;
			return;
		}

	}

	void replaceChild(Node oldChild, Node newChild)
	{
		if (_at_ == oldChild)
		{
			setAt((TAt) newChild);
			return;
		}

		if (_expr_ == oldChild)
		{
			setExpr((TId) newChild);
			return;
		}

	}
}
/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.node;

import nl.openedge.gaps.support.gapspath.analysis.Analysis;

public final class AOrOptionsPartTail extends POptionsPartTail
{

	private TOr _or_;

	private POptionsPart _optionsPart_;

	public AOrOptionsPartTail()
	{
	}

	public AOrOptionsPartTail(TOr _or_, POptionsPart _optionsPart_)
	{
		setOr(_or_);

		setOptionsPart(_optionsPart_);

	}

	public Object clone()
	{
		return new AOrOptionsPartTail((TOr) cloneNode(_or_),
				(POptionsPart) cloneNode(_optionsPart_));
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseAOrOptionsPartTail(this);
	}

	public TOr getOr()
	{
		return _or_;
	}

	public void setOr(TOr node)
	{
		if (_or_ != null)
		{
			_or_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_or_ = node;
	}

	public POptionsPart getOptionsPart()
	{
		return _optionsPart_;
	}

	public void setOptionsPart(POptionsPart node)
	{
		if (_optionsPart_ != null)
		{
			_optionsPart_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_optionsPart_ = node;
	}

	public String toString()
	{
		return "" + toString(_or_) + toString(_optionsPart_);
	}

	void removeChild(Node child)
	{
		if (_or_ == child)
		{
			_or_ = null;
			return;
		}

		if (_optionsPart_ == child)
		{
			_optionsPart_ = null;
			return;
		}

	}

	void replaceChild(Node oldChild, Node newChild)
	{
		if (_or_ == oldChild)
		{
			setOr((TOr) newChild);
			return;
		}

		if (_optionsPart_ == oldChild)
		{
			setOptionsPart((POptionsPart) newChild);
			return;
		}

	}
}
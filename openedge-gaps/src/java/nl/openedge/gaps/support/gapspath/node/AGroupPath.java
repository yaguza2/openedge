/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.node;

import nl.openedge.gaps.support.gapspath.analysis.Analysis;

public final class AGroupPath extends PPath
{

	private PStructGroup _structGroup_;

	private PParamGroup _paramGroup_;

	private POptions _options_;

	public AGroupPath()
	{
	}

	public AGroupPath(PStructGroup _structGroup_, PParamGroup _paramGroup_,
			POptions _options_)
	{
		setStructGroup(_structGroup_);

		setParamGroup(_paramGroup_);

		setOptions(_options_);

	}

	public Object clone()
	{
		return new AGroupPath((PStructGroup) cloneNode(_structGroup_),
				(PParamGroup) cloneNode(_paramGroup_), (POptions) cloneNode(_options_));
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseAGroupPath(this);
	}

	public PStructGroup getStructGroup()
	{
		return _structGroup_;
	}

	public void setStructGroup(PStructGroup node)
	{
		if (_structGroup_ != null)
		{
			_structGroup_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_structGroup_ = node;
	}

	public PParamGroup getParamGroup()
	{
		return _paramGroup_;
	}

	public void setParamGroup(PParamGroup node)
	{
		if (_paramGroup_ != null)
		{
			_paramGroup_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_paramGroup_ = node;
	}

	public POptions getOptions()
	{
		return _options_;
	}

	public void setOptions(POptions node)
	{
		if (_options_ != null)
		{
			_options_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_options_ = node;
	}

	public String toString()
	{
		return ""
				+ toString(_structGroup_) + toString(_paramGroup_) + toString(_options_);
	}

	void removeChild(Node child)
	{
		if (_structGroup_ == child)
		{
			_structGroup_ = null;
			return;
		}

		if (_paramGroup_ == child)
		{
			_paramGroup_ = null;
			return;
		}

		if (_options_ == child)
		{
			_options_ = null;
			return;
		}

	}

	void replaceChild(Node oldChild, Node newChild)
	{
		if (_structGroup_ == oldChild)
		{
			setStructGroup((PStructGroup) newChild);
			return;
		}

		if (_paramGroup_ == oldChild)
		{
			setParamGroup((PParamGroup) newChild);
			return;
		}

		if (_options_ == oldChild)
		{
			setOptions((POptions) newChild);
			return;
		}

	}
}
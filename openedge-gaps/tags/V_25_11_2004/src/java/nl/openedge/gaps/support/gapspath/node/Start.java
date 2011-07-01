/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.node;

import nl.openedge.gaps.support.gapspath.analysis.Analysis;

public final class Start extends Node
{

	private PPath _pPath_;

	private EOF _eof_;

	public Start()
	{
	}

	public Start(PPath _pPath_, EOF _eof_)
	{
		setPPath(_pPath_);
		setEOF(_eof_);
	}

	public Object clone()
	{
		return new Start((PPath) cloneNode(_pPath_), (EOF) cloneNode(_eof_));
	}

	public void apply(Switch sw)
	{
		((Analysis) sw).caseStart(this);
	}

	public PPath getPPath()
	{
		return _pPath_;
	}

	public void setPPath(PPath node)
	{
		if (_pPath_ != null)
		{
			_pPath_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_pPath_ = node;
	}

	public EOF getEOF()
	{
		return _eof_;
	}

	public void setEOF(EOF node)
	{
		if (_eof_ != null)
		{
			_eof_.parent(null);
		}

		if (node != null)
		{
			if (node.parent() != null)
			{
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		_eof_ = node;
	}

	void removeChild(Node child)
	{
		if (_pPath_ == child)
		{
			_pPath_ = null;
			return;
		}

		if (_eof_ == child)
		{
			_eof_ = null;
			return;
		}
	}

	void replaceChild(Node oldChild, Node newChild)
	{
		if (_pPath_ == oldChild)
		{
			setPPath((PPath) newChild);
			return;
		}

		if (_eof_ == oldChild)
		{
			setEOF((EOF) newChild);
			return;
		}
	}

	public String toString()
	{
		return "" + toString(_pPath_) + toString(_eof_);
	}
}
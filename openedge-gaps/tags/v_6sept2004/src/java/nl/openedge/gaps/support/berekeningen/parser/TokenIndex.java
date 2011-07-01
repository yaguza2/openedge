/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.berekeningen.parser;

import nl.openedge.gaps.support.berekeningen.analysis.AnalysisAdapter;
import nl.openedge.gaps.support.berekeningen.node.EOF;
import nl.openedge.gaps.support.berekeningen.node.TComma;
import nl.openedge.gaps.support.berekeningen.node.TDiv;
import nl.openedge.gaps.support.berekeningen.node.TFuncid;
import nl.openedge.gaps.support.berekeningen.node.TId;
import nl.openedge.gaps.support.berekeningen.node.TLBrace;
import nl.openedge.gaps.support.berekeningen.node.TLPar;
import nl.openedge.gaps.support.berekeningen.node.TMinus;
import nl.openedge.gaps.support.berekeningen.node.TMult;
import nl.openedge.gaps.support.berekeningen.node.TNumber;
import nl.openedge.gaps.support.berekeningen.node.TPlus;
import nl.openedge.gaps.support.berekeningen.node.TRBrace;
import nl.openedge.gaps.support.berekeningen.node.TRPar;

class TokenIndex extends AnalysisAdapter
{

	int index;

	public void caseTLPar(TLPar node)
	{
		index = 0;
	}

	public void caseTRPar(TRPar node)
	{
		index = 1;
	}

	public void caseTPlus(TPlus node)
	{
		index = 2;
	}

	public void caseTMinus(TMinus node)
	{
		index = 3;
	}

	public void caseTMult(TMult node)
	{
		index = 4;
	}

	public void caseTDiv(TDiv node)
	{
		index = 5;
	}

	public void caseTComma(TComma node)
	{
		index = 6;
	}

	public void caseTLBrace(TLBrace node)
	{
		index = 7;
	}

	public void caseTRBrace(TRBrace node)
	{
		index = 8;
	}

	public void caseTNumber(TNumber node)
	{
		index = 9;
	}

	public void caseTId(TId node)
	{
		index = 10;
	}

	public void caseTFuncid(TFuncid node)
	{
		index = 11;
	}

	public void caseEOF(EOF node)
	{
		index = 12;
	}
}
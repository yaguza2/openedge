/* This file was generated by SableCC (http://www.sablecc.org/). */

package nl.openedge.gaps.support.gapspath.analysis;

import nl.openedge.gaps.support.gapspath.node.AAndOptionsPartTail;
import nl.openedge.gaps.support.gapspath.node.AArraySelect;
import nl.openedge.gaps.support.gapspath.node.AAttribute;
import nl.openedge.gaps.support.gapspath.node.AGroupPath;
import nl.openedge.gaps.support.gapspath.node.AOptions;
import nl.openedge.gaps.support.gapspath.node.AOptionsBlock;
import nl.openedge.gaps.support.gapspath.node.AOptionsPart;
import nl.openedge.gaps.support.gapspath.node.AOrOptionsPartTail;
import nl.openedge.gaps.support.gapspath.node.AParam;
import nl.openedge.gaps.support.gapspath.node.AParamGroup;
import nl.openedge.gaps.support.gapspath.node.AParamPath;
import nl.openedge.gaps.support.gapspath.node.ARootStructGroup;
import nl.openedge.gaps.support.gapspath.node.AStructGroup;
import nl.openedge.gaps.support.gapspath.node.AStructGroupTail;
import nl.openedge.gaps.support.gapspath.node.Node;
import nl.openedge.gaps.support.gapspath.node.POptionsPartTail;
import nl.openedge.gaps.support.gapspath.node.PStructGroupTail;
import nl.openedge.gaps.support.gapspath.node.Start;

public class DepthFirstAdapter extends AnalysisAdapter
{

	public void inStart(Start node)
	{
		defaultIn(node);
	}

	public void outStart(Start node)
	{
		defaultOut(node);
	}

	public void defaultIn(Node node)
	{
	}

	public void defaultOut(Node node)
	{
	}

	public void caseStart(Start node)
	{
		inStart(node);
		node.getPPath().apply(this);
		node.getEOF().apply(this);
		outStart(node);
	}

	public void inAGroupPath(AGroupPath node)
	{
		defaultIn(node);
	}

	public void outAGroupPath(AGroupPath node)
	{
		defaultOut(node);
	}

	public void caseAGroupPath(AGroupPath node)
	{
		inAGroupPath(node);
		if (node.getStructGroup() != null)
		{
			node.getStructGroup().apply(this);
		}
		if (node.getParamGroup() != null)
		{
			node.getParamGroup().apply(this);
		}
		if (node.getOptions() != null)
		{
			node.getOptions().apply(this);
		}
		outAGroupPath(node);
	}

	public void inAParamPath(AParamPath node)
	{
		defaultIn(node);
	}

	public void outAParamPath(AParamPath node)
	{
		defaultOut(node);
	}

	public void caseAParamPath(AParamPath node)
	{
		inAParamPath(node);
		if (node.getStructGroup() != null)
		{
			node.getStructGroup().apply(this);
		}
		if (node.getParamGroup() != null)
		{
			node.getParamGroup().apply(this);
		}
		if (node.getParam() != null)
		{
			node.getParam().apply(this);
		}
		if (node.getOptions() != null)
		{
			node.getOptions().apply(this);
		}
		outAParamPath(node);
	}

	public void inARootStructGroup(ARootStructGroup node)
	{
		defaultIn(node);
	}

	public void outARootStructGroup(ARootStructGroup node)
	{
		defaultOut(node);
	}

	public void caseARootStructGroup(ARootStructGroup node)
	{
		inARootStructGroup(node);
		if (node.getSlash() != null)
		{
			node.getSlash().apply(this);
		}
		outARootStructGroup(node);
	}

	public void inAStructGroup(AStructGroup node)
	{
		defaultIn(node);
	}

	public void outAStructGroup(AStructGroup node)
	{
		defaultOut(node);
	}

	public void caseAStructGroup(AStructGroup node)
	{
		inAStructGroup(node);
		if (node.getSlash() != null)
		{
			node.getSlash().apply(this);
		}
		if (node.getId() != null)
		{
			node.getId().apply(this);
		}
		{
			Object temp[] = node.getStructGroupTail().toArray();
			for (int i = 0; i < temp.length; i++)
			{
				((PStructGroupTail) temp[i]).apply(this);
			}
		}
		outAStructGroup(node);
	}

	public void inAStructGroupTail(AStructGroupTail node)
	{
		defaultIn(node);
	}

	public void outAStructGroupTail(AStructGroupTail node)
	{
		defaultOut(node);
	}

	public void caseAStructGroupTail(AStructGroupTail node)
	{
		inAStructGroupTail(node);
		if (node.getSlash() != null)
		{
			node.getSlash().apply(this);
		}
		if (node.getId() != null)
		{
			node.getId().apply(this);
		}
		outAStructGroupTail(node);
	}

	public void inAParamGroup(AParamGroup node)
	{
		defaultIn(node);
	}

	public void outAParamGroup(AParamGroup node)
	{
		defaultOut(node);
	}

	public void caseAParamGroup(AParamGroup node)
	{
		inAParamGroup(node);
		if (node.getColon() != null)
		{
			node.getColon().apply(this);
		}
		if (node.getId() != null)
		{
			node.getId().apply(this);
		}
		outAParamGroup(node);
	}

	public void inAParam(AParam node)
	{
		defaultIn(node);
	}

	public void outAParam(AParam node)
	{
		defaultOut(node);
	}

	public void caseAParam(AParam node)
	{
		inAParam(node);
		if (node.getSlash() != null)
		{
			node.getSlash().apply(this);
		}
		if (node.getId() != null)
		{
			node.getId().apply(this);
		}
		if (node.getArraySelect() != null)
		{
			node.getArraySelect().apply(this);
		}
		if (node.getAttribute() != null)
		{
			node.getAttribute().apply(this);
		}
		outAParam(node);
	}

	public void inAArraySelect(AArraySelect node)
	{
		defaultIn(node);
	}

	public void outAArraySelect(AArraySelect node)
	{
		defaultOut(node);
	}

	public void caseAArraySelect(AArraySelect node)
	{
		inAArraySelect(node);
		if (node.getLBracket() != null)
		{
			node.getLBracket().apply(this);
		}
		if (node.getLeftQuote() != null)
		{
			node.getLeftQuote().apply(this);
		}
		if (node.getSelectExpr() != null)
		{
			node.getSelectExpr().apply(this);
		}
		if (node.getRightQuote() != null)
		{
			node.getRightQuote().apply(this);
		}
		if (node.getRBracket() != null)
		{
			node.getRBracket().apply(this);
		}
		outAArraySelect(node);
	}

	public void inAAttribute(AAttribute node)
	{
		defaultIn(node);
	}

	public void outAAttribute(AAttribute node)
	{
		defaultOut(node);
	}

	public void caseAAttribute(AAttribute node)
	{
		inAAttribute(node);
		if (node.getAt() != null)
		{
			node.getAt().apply(this);
		}
		if (node.getExpr() != null)
		{
			node.getExpr().apply(this);
		}
		outAAttribute(node);
	}

	public void inAOptions(AOptions node)
	{
		defaultIn(node);
	}

	public void outAOptions(AOptions node)
	{
		defaultOut(node);
	}

	public void caseAOptions(AOptions node)
	{
		inAOptions(node);
		if (node.getLBrace() != null)
		{
			node.getLBrace().apply(this);
		}
		if (node.getOptionsBlock() != null)
		{
			node.getOptionsBlock().apply(this);
		}
		if (node.getRBrace() != null)
		{
			node.getRBrace().apply(this);
		}
		outAOptions(node);
	}

	public void inAOptionsBlock(AOptionsBlock node)
	{
		defaultIn(node);
	}

	public void outAOptionsBlock(AOptionsBlock node)
	{
		defaultOut(node);
	}

	public void caseAOptionsBlock(AOptionsBlock node)
	{
		inAOptionsBlock(node);
		if (node.getOptionsPart() != null)
		{
			node.getOptionsPart().apply(this);
		}
		{
			Object temp[] = node.getOptionsPartTail().toArray();
			for (int i = 0; i < temp.length; i++)
			{
				((POptionsPartTail) temp[i]).apply(this);
			}
		}
		outAOptionsBlock(node);
	}

	public void inAAndOptionsPartTail(AAndOptionsPartTail node)
	{
		defaultIn(node);
	}

	public void outAAndOptionsPartTail(AAndOptionsPartTail node)
	{
		defaultOut(node);
	}

	public void caseAAndOptionsPartTail(AAndOptionsPartTail node)
	{
		inAAndOptionsPartTail(node);
		if (node.getAnd() != null)
		{
			node.getAnd().apply(this);
		}
		if (node.getOptionsPart() != null)
		{
			node.getOptionsPart().apply(this);
		}
		outAAndOptionsPartTail(node);
	}

	public void inAOrOptionsPartTail(AOrOptionsPartTail node)
	{
		defaultIn(node);
	}

	public void outAOrOptionsPartTail(AOrOptionsPartTail node)
	{
		defaultOut(node);
	}

	public void caseAOrOptionsPartTail(AOrOptionsPartTail node)
	{
		inAOrOptionsPartTail(node);
		if (node.getOr() != null)
		{
			node.getOr().apply(this);
		}
		if (node.getOptionsPart() != null)
		{
			node.getOptionsPart().apply(this);
		}
		outAOrOptionsPartTail(node);
	}

	public void inAOptionsPart(AOptionsPart node)
	{
		defaultIn(node);
	}

	public void outAOptionsPart(AOptionsPart node)
	{
		defaultOut(node);
	}

	public void caseAOptionsPart(AOptionsPart node)
	{
		inAOptionsPart(node);
		if (node.getParameter() != null)
		{
			node.getParameter().apply(this);
		}
		if (node.getEq() != null)
		{
			node.getEq().apply(this);
		}
		if (node.getLeftQuote() != null)
		{
			node.getLeftQuote().apply(this);
		}
		if (node.getSelectExpr() != null)
		{
			node.getSelectExpr().apply(this);
		}
		if (node.getRightQuote() != null)
		{
			node.getRightQuote().apply(this);
		}
		outAOptionsPart(node);
	}
}
/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.util.tree;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.VariableHeightLayoutCache;

/**
 * Holder and handler for tree state
 * @author Eelco Hillenius
 */
public final class TreeStateCache 
	extends VariableHeightLayoutCache
	implements Serializable
{

	/* currently selected path */
	private TreePath selectedPath;
	/* query that was used to select the current selected path */
	private String selectedPathQuery;
	/* the objects owned by the selected path */
	private List pathContent;

	/**
	 * expand the selected path and set selection to currently selected path
	 * @param selection new selection
	 */
	public void setSelectedPath(TreePath selection)
	{
		setExpandedState(selection, true);
		this.selectedPath = selection;
	}

	/**
	 * expand the selected path and set selection to currently selected path
	 * and set the content owned by the selected path
	 * @param selection new selection
	 * @param content owned by selection
	 */
	public void setSelectedPath(TreePath selection, List pathContent)
	{
		setSelectedPath(selection);
		this.pathContent = pathContent;
	}

	/**
	 * expand the selected path and set selection to currently selected path
	 * , set the content owned by the selected path and set query that was 
	 * used to get the selection
	 * @param selection new selection
	 * @param content owned by selection
	 * @param selectedPathQuery query that was used to get selection  
	 */
	public void setSelectedPath(TreePath selection, List pathContent, 
		String selectedPathQuery)
	{
		setSelectedPath(selection, pathContent);
		this.selectedPathQuery = selectedPathQuery;
	}

	/**
	 * get the currently selected path
	 * @return TreePath
	 */
	public TreePath getSelectedPath()
	{
		if (selectedPath == null && isRootVisible())
		{
			selectedPath = new TreePath(getModel().getRoot());
		}
		return selectedPath;
	}

	/**
	 * get the content that is owned by the current path
	 * @return List list of content
	 */
	public List getPathContent()
	{
		return pathContent;
	}

	/**
	 * set the content that is owned by the current path
	 * @param list list of content 
	 */
	public void setPathContent(List list)
	{
		pathContent = list;
	}

	/**
	 * @return String
	 */
	public String getSelectedPathQuery()
	{
		return selectedPathQuery;
	}

	/**
	 * @param selectedPathQuery
	 */
	public void setSelectedPathQuery(String selectedPathQuery)
	{
		this.selectedPathQuery = selectedPathQuery;
	}

	/**
	 * Returns an <code>Enumerator</code> that increments over the visible paths
	 * starting at the root. The ordering of the enumeration
	 * is based on how the paths are displayed.
	 * 
	 * @return an <code>Enumerator</code> that increments over the visible paths
	 */
	public Enumeration getVisiblePathsFromRoot()
	{
		TreeNode root = (TreeNode) (getModel().getRoot());
		TreePath rootPath = new TreePath(root);
		return getVisiblePathsFrom(rootPath);
	}

	/**
	 * get tree path in model for given user object
	 * @param userObject object to look for in model
	 * @return TreePath
	 */
	public TreePath findTreePath(Object userObject)
	{

		DefaultMutableTreeNode endNode = findNodeForPath(userObject);
		TreePath path = new TreePath(endNode.getPath());
		return path;
	}

	/**
	 * find the node in model that has arg as its userObject
	 * @param userObject object to look for in model
	 * @return DefaultMutableTreeNode
	 */
	public DefaultMutableTreeNode findNodeForPath(Object userObject)
	{

		DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
		return findNodeRecursively(root, null, userObject);
	}

	/* recurse childs */
	private DefaultMutableTreeNode findNodeRecursively(
		DefaultMutableTreeNode currentNode,
		DefaultMutableTreeNode resultNode,
		Object userObject)
	{

		int childCount = currentNode.getChildCount();
		if (currentNode.getUserObject().equals(userObject))
		{
			resultNode = currentNode;
		}
		else if (childCount > 0)
		{
			for (int i = 0; i < childCount; i++)
			{
				resultNode =
					findNodeRecursively((DefaultMutableTreeNode)
						currentNode.getChildAt(i), resultNode, userObject);
				if (resultNode != null)
				{
					// found it! break loop
					break;
				}
			}
		} // else: not found in this path
		return resultNode;
	}

}

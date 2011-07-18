package nl.openedge.modules.impl.menumodule;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.VariableHeightLayoutCache;

/**
 * Holder and handler for tree state.
 * 
 * @author Eelco Hillenius
 */
public final class TreeStateCache extends VariableHeightLayoutCache implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** currently selected path. */
	private TreePath selectedPath;

	/** query that was used to select the current selected path. */
	private String selectedPathQuery;

	/** the objects owned by the selected path. */
	private List<Object> pathContent;

	/**
	 * expand the selected path and set selection to currently selected path.
	 * 
	 * @param selection
	 *            new selection
	 */
	public void setSelectedPath(TreePath selection)
	{
		setExpandedState(selection, true);
		this.selectedPath = selection;
	}

	/**
	 * expand the selected path and set selection to currently selected path and set the
	 * content owned by the selected path.
	 * 
	 * @param selection
	 *            new selection
	 * @param content
	 *            owned by selection
	 */
	public void setSelectedPath(TreePath selection, List<Object> content)
	{
		setSelectedPath(selection);
		this.pathContent = content;
	}

	/**
	 * expand the selected path and set selection to currently selected path , set the
	 * content owned by the selected path and set query that was used to get the
	 * selection.
	 * 
	 * @param selection
	 *            new selection
	 * @param content
	 *            owned by selection
	 * @param query
	 *            query that was used to get selection
	 */
	public void setSelectedPath(TreePath selection, List<Object> content, String query)
	{
		setSelectedPath(selection, content);
		this.selectedPathQuery = query;
	}

	/**
	 * get the currently selected path.
	 * 
	 * @return TreePath the currently select path
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
	 * get the content that is owned by the current path.
	 * 
	 * @return List list of content
	 */
	public List<Object> getPathContent()
	{
		return pathContent;
	}

	/**
	 * set the content that is owned by the current path.
	 * 
	 * @param list
	 *            list of content
	 */
	public void setPathContent(List<Object> list)
	{
		pathContent = list;
	}

	/**
	 * Get the selected path query.
	 * 
	 * @return String the selected path query
	 */
	public String getSelectedPathQuery()
	{
		return selectedPathQuery;
	}

	/**
	 * Set the selected path query.
	 * 
	 * @param selectedPathQuery
	 *            the selected path query
	 */
	public void setSelectedPathQuery(String selectedPathQuery)
	{
		this.selectedPathQuery = selectedPathQuery;
	}

	/**
	 * Returns an <code>Enumerator</code> that increments over the visible paths starting
	 * at the root. The ordering of the enumeration is based on how the paths are
	 * displayed.
	 * 
	 * @return an <code>Enumerator</code> that increments over the visible paths
	 */
	public Enumeration<TreePath> getVisiblePathsFromRoot()
	{
		TreeNode root = (TreeNode) (getModel().getRoot());
		TreePath rootPath = new TreePath(root);
		return getVisiblePathsFrom(rootPath);
	}

	/**
	 * get tree path in model for given user object.
	 * 
	 * @param userObject
	 *            object to look for in model
	 * @return TreePath the treepath for the given user object
	 */
	public TreePath findTreePath(Object userObject)
	{
		TreePath path = null;
		DefaultMutableTreeNode endNode = findNodeForPath(userObject);
		if (endNode != null)
		{
			path = new TreePath(endNode.getPath());
		}
		return path;
	}

	public DefaultMutableTreeNode findNodeForPath(Object userObject)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
		return findNodeRecursively(root, null, userObject);
	}

	private DefaultMutableTreeNode findNodeRecursively(DefaultMutableTreeNode currentNode,
			DefaultMutableTreeNode currentResultNode, Object userObject)
	{
		DefaultMutableTreeNode resultNode = currentResultNode;
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
					findNodeRecursively((DefaultMutableTreeNode) currentNode.getChildAt(i),
						resultNode, userObject);
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

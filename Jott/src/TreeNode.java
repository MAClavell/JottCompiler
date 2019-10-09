import com.sun.source.tree.Tree;

import java.util.ArrayList;

public class TreeNode {

    //The child branches
    private ArrayList<TreeNode> branches;
    //The state of the branch
    private State state;


    /**
     * A constructor for a tree's node
     * @param state the current state of the tree's node
     */
    public TreeNode(State state){
        branches=new ArrayList<TreeNode>();
        this.state=state;
    }

    /**
     * Whether or not the tree node is a leaf
     * @return whether or not the tree node has any children
     */
    public boolean hasChildren(){
        return branches.size()!=0;
    }

    public ArrayList<TreeNode> getChildren(){
        return branches;
    }

    /**
     * Adds a new tree node to the tree
     * @param state the state of the node in the tree
     */
    public TreeNode addTreeNode(State state)
    {
        TreeNode node = new TreeNode(state);
        branches.add(node);
        return node;
    }

    /**
     * Adds an existing tree node to the tree
     * @param node the existing treenode to add
     */
    public TreeNode addTreeNode(TreeNode node)
    {
        branches.add(node);
        return node;
    }

    /**
     * Adds an existing tree node to the front of the tree
     * @param node the existing treenode to add
     */
    public TreeNode addTreeNodeToFront(TreeNode node)
    {
        branches.add(0, node);
        return node;
    }

    /**
     * Gets the node's state in the tree
     * @return the node's state in the tree
     */
    public State getState(){
        return state;
    }

    /**
     * Gets the token corresponding to the state
     * @return the token in the state
     */
    public Token getToken(){
        return state.getToken();
    }

    private static int indentation = 0; //TODO: REMOVE (for testing only)
    @Override
    public String toString() {
        String output = "";
        output += state.getState() + ", \n";
        indentation++;
        for(TreeNode branch : branches)
        {
            for(int i = 0; i < indentation; i++)
                output += "\t";
            output += branch.toString();
        }
        indentation--;
        return output;
    }
}

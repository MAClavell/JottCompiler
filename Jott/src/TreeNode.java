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

    /**
     * Adds a new tree node to the tree
     * @param state the state of the node in the tree
     */
    public void addTreeNode(State state){
        branches.add(new TreeNode(state));
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
}

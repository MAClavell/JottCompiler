import java.util.ArrayList;

public class TreeNode {

    private ArrayList<TreeNode> branches;
    private State state;
    private Token token;

    public TreeNode(State state){
        branches=new ArrayList<TreeNode>();
        this.state=state;
        this.token=null;
    }

    public TreeNode(State state, Token token){
        branches=new ArrayList<TreeNode>();
        this.state=state;
        this.token=token;
    }

    public boolean hasChildren(){
        return branches.size()!=0;
    }

    public void addTreeNode(State state){
        branches.add(new TreeNode(state));
    }

    public void addTreeNode(State state, Token token){
        branches.add(new TreeNode(state, token));
    }

    public State getState(){
        return state;
    }

    public Token getToken(){
        return token;
    }
}

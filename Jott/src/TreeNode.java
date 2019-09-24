import java.util.ArrayList;

public class TreeNode {

    private ArrayList<TreeNode> branches;
    private String contents;

    public TreeNode(String contents){
        branches=new ArrayList<TreeNode>();
        this.contents=contents;
    }

    public boolean hasChildren(){
        return branches.size()!=0;
    }

    public void addTreeNode(String contents){
        branches.add(new TreeNode(contents));
    }

    public String getContents(){
        return contents;
    }

    @Override
    public String toString() {
        String builtString=contents;
        for(TreeNode childBranches:branches){
            contents+=childBranches.branches;
        }
        return builtString;
    }
}

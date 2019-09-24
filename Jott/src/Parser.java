import java.util.ArrayList;

public class Parser {
    /**
     * The main parsing method called to parse the tokenStream into a parse tree
     * @param tokenStream the token stream to parse
     * @return the parse tree
     */
    public static TreeNode parse(ArrayList<Token> tokenStream){
        State state = new State(State.stateType.PROGRAM);
        TreeNode root= new TreeNode(state);
        return root;
    }

    ///Predict Functions
    public void program(TreeNode node){
       // if(State.stateType.PRINT.equals())
       // {
       //     print();
       // }
    }
    public void stmt_list(TreeNode node){
        State state = new State(State.stateType.STMT_LIST);
        node.addTreeNode(state);
    }
    public void end_paren(TreeNode node) {

    }
    public void end_stmt(TreeNode node) {

    }
    public void character(TreeNode node) {

    }
    public void l_char(TreeNode node) {

    }
    public void u_char(TreeNode node) {

    }
    public void digit(TreeNode node) {

    }
    public void sign(TreeNode node) {

    }
    public void id(TreeNode node) {

    }
    public void stmt(TreeNode node) {

    }
    public void expr(TreeNode node) {

    }
    public void print(TreeNode node) {

    }
    public void asmt(TreeNode node) {

    }
    public void op(TreeNode node) {

    }
    public void dbl(TreeNode node) {

    }
    public void d_expr(TreeNode node) {

    }
    public void integer(TreeNode node) {

    }
    public void i_expr(TreeNode node) {

    }
    public void str_literal(TreeNode node) {

    }
    public void str(TreeNode node) {

    }
    public void s_expr(TreeNode node) {

    }

}

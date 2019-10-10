import java.util.HashMap;

public class SemanticAnalysis {

    private static HashMap<String, Symbol> symbols;

    public static void output(TreeNode root, HashMap<String, Symbol> symbolTable){
        symbols=symbolTable;
        program(root);
    }

    private static void program(TreeNode node){

        // Searches through the first stmt_list
        stmt_list(node.getChildren().get(0));
    }

    private static void stmt_list(TreeNode node){

        // Makes sure this list is not epsilon
        if(node.getChildren().get(0).getState().getState()!=State.stateType.EPSILON){

            // Searches through the associated statement
            stmt(node.getChildren().get(0));

            // Searches through the next stmt_list
            stmt_list(node.getChildren().get(1));
        }
    }

    private static void stmt(TreeNode node){

        // Evaluates the print statement
        if(node.getChildren().get(0).getState().getState() == State.stateType.PRINT){
            print(node.getChildren().get(0));
        }

        // Evaluates the asmt statement
        else if(node.getChildren().get(0).getState().getState() == State.stateType.ASMT){
            asmt_stmt(node.getChildren().get(0));
        }

        // Evaluates the <expr><end_statement>
        else{
            // TODO implement this but skip the expr node into i_expr/d_expr/etc like in print
        }
    }

    private static void print(TreeNode node){

        // Either going to be the stmt or id node
        TreeNode childNode=node.getChildren().get(2);

        // If the child's expr's state is an id
        if(node.getChildren().get(2).getChildren().get(0).getState().getState() == State.stateType.ID){
            Symbol childSymbol=symbols.get(node.getChildren().get(2).getToken().getTokenText());
            if(childSymbol.getValue()!=null) {
                System.out.println(childSymbol.getValue());
            }
            else{
                //Error
            }
        }
        else {
            TreeNode grandchildNode = childNode.getChildren().get(0);

            // If the grandchild's expr's state is an I_EXPR
            if (grandchildNode.getState().getState() == State.stateType.I_EXPR) {
                System.out.println(i_expr(grandchildNode));
            }

            // If the grandchild's expr's state is a D_EXPR
            else if (grandchildNode.getState().getState() == State.stateType.D_EXPR) {
                System.out.println(d_expr(grandchildNode));
            }

            // If the grandchild's expr's state is a S_EXPR
            else {
                System.out.println(s_expr(grandchildNode));
            }
        }
    }

    private static void asmt_stmt(TreeNode node){

    }

    private static boolean verifyIntID(TreeNode node){
        if(symbols.containsKey(node.getToken().getTokenText())){
            if(symbols.get(node.getToken().getTokenText()) != null &&
                symbols.get(node.getToken().getTokenText()).getType()== Symbol.variableType.INTEGER){
                return true;
            }
        }
        return false;
    }

    private static int i_expr(TreeNode node){

        // If the leftmost child is an id
        if(node.getChildren().get(0).getState().getState() == State.stateType.ID){

            TreeNode childID=node.getChildren().get(0);

            // It is in the symbol table as the same type and is not undefined
            if(verifyIntID(childID)){

                // If there is a second child
                if(node.getChildren().size()>1){

                    // If the second operation is an id
                    if(){

                    }

                    // If the second operation is an Integer (or +/-)
                    else if(){

                    }

                    // If the second operation is
                    else{

                    }
                }

                // If this is the only state in the expression return it (autoboxes)
                else{
                    return (Integer) symbols.get(0).getValue();
                }
            }

            //TODO ERROR
        }

        // If the leftmost child is an Integer
        else if(node.getChildren().get(0).getState().getState()== State.stateType.INT){

        }

        // If the leftmost child is an i_expr
        else{

        }
    }

    private static double d_expr(TreeNode node){

    }

    private static String s_expr(TreeNode node){

    }

    private static void semanticError(TreeNode node){

    }
}

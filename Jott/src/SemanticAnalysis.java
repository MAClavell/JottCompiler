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
            // TODO is this what we were looking for? See helper function
            checkExpressions(node);
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
            checkExpressions(childNode);
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

                // The first operator
                Integer firstOp=(Integer) symbols.get(childID.getToken().getTokenText()).getValue();;

                // If there is a second child
                if(node.getChildren().size()>1){

                    Integer secondOp = null;

                    // If the second operation is an id
                    if(node.getChildren().get(2).getState().getState() == State.stateType.ID){
                        if(verifyIntID(node.getChildren().get(2))){
                            secondOp=(Integer)symbols.get(node.getChildren().get(2).getToken().getTokenText()).getValue();
                        }
                        else {
                            // TODO ERROR
                        }
                    }

                    // If the second operation is an Integer (or +/-)
                    else if(node.getChildren().get(2).getState().getState()== State.stateType.INT ||
                            node.getChildren().get(2).getState().getState()== State.stateType.SIGN){

                    }

                    // If the second operation is an i_expr
                    else{

                    }

                    if (secondOp != null) {
                        // Return statement based on the operation (+,-,/,*)
                        switch(node.getChildren().get(1).getToken().getTokenType()){
                            case Minus:
                                return firstOp-secondOp;
                            case Plus:
                                return firstOp+secondOp;
                            case Mult:
                                return firstOp*secondOp;
                            case Divide:
                                return firstOp/secondOp;
                        }
                    }
                }

                // If this is the only state in the expression return it (autoboxes)
                else{
                    return firstOp;
                }
            }

            //TODO ERROR
        }

        // If the leftmost child is an Integer
        else if(node.getChildren().get(0).getState().getState()== State.stateType.INT ||
                node.getChildren().get(0).getState().getState()== State.stateType.SIGN){

            /*
             *   Gets the first integer
             */

            Integer firstOp;

            // There is no sign
            if(node.getChildren().size()==1){
                firstOp=Integer.parseInt(node.getChildren().get(0).getToken().getTokenText());
            }

            // There is a sign
            else{
                firstOp=Integer.parseInt(node.getChildren().get(1).getToken().getTokenText());

                // Adds a negative sign
                if(node.getChildren().get(0).getToken().getTokenType()==TokenType.Minus){
                    firstOp*=-1;
                }
            }

            /*
             * Parses a second operand if there is one and handles returning
             */
            if(node.getChildren().size() > 2){
                if(){

                }

                else if(){

                }
                else{

                }
            }

            // There is only one op
            else{
                return firstOp;
            }

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

    ///HELPERS ---------------------------------------------------------------

    public static void checkExpressions(TreeNode node) {
        TreeNode grandchildNode = node.getChildren().get(0);

        // If the grandchild expr's state is an I_EXPR
        if (grandchildNode.getState().getState() == State.stateType.I_EXPR) {
            System.out.println(i_expr(grandchildNode));
        }

        // If the grandchild expr's state is a D_EXPR
        else if (grandchildNode.getState().getState() == State.stateType.D_EXPR) {
            System.out.println(d_expr(grandchildNode));
        }

        // If the grandchild expr's state is a S_EXPR
        else {
            System.out.println(s_expr(grandchildNode));
        }
    }
}

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

    private static int parseIntOperand(TreeNode node, int childNumber){
        TreeNode childNode=node.getChildren().get(childNumber);

        // If it is an id
        if(childNode.getState().getState()== State.stateType.ID){
            // Verifies it is defined, not null, and it is an integer
            if(verifyIntID(node)){
                return (Integer)symbols.get(childNode.getToken().getTokenText()).getValue();
            }
            // Error
        }

        // If it is an integer
        else if(childNode.getState().getState()== State.stateType.INT ||
                childNode.getState().getState()== State.stateType.SIGN){
            int secondOp;

            // If the first symbol is a sign
            if(childNode.getState().getState()== State.stateType.SIGN){
                secondOp=Integer.parseInt(node.getChildren().get(childNumber+1).getToken().getTokenText());

                if(childNode.getToken().getTokenType()==TokenType.Minus){
                    secondOp*=-1;
                }
                return secondOp;
            }

            // If there is no sign
            else{
                return Integer.parseInt(childNode.getToken().getTokenText());
            }
        }

        // If it is another i_expr
        else{
            return i_expr(childNode);
        }

        // This should never be reached
        return -1;
    }

    private static int i_expr(TreeNode node){
        // Gets the first operator
        int firstOp=parseIntOperand(node, 0);

        // There are multiple operators
        if(node.getChildren().size()>2) {

            int secondOp;
            int sign;
            // The first operator had a sign
            if (node.getChildren().get(0).getState().getState() == State.stateType.SIGN) {
                sign=2;

            }

            // The first operator did not have a sign
            else {
                sign=1;
            }

            // Gets the second operand
            secondOp=parseIntOperand(node, sign+1);

            // Uses the sign number
            switch(node.getChildren().get(sign).getToken().getTokenType()){
                case Minus:
                    return firstOp-secondOp;
                case Plus:
                    return firstOp+secondOp;
                case Mult:
                    return firstOp*secondOp;
                case Divide:
                    return firstOp/secondOp;
            }

            //TODO error
        }

        // It is just one operator
        else{
            return firstOp;
        }

        // This should never be reached
        return -1;
    }

    private static double d_expr(TreeNode node){

    }

    private static String s_expr(TreeNode node){

    }

    private static void semanticError(TreeNode node){

    }
}

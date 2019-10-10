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

        TreeNode childNode=node.getChildren().get(0);
        String id=node.getChildren().get(1).getToken().getTokenText();
        TreeNode exprNode=node.getChildren().get(3);

        // An Integer assignment statement
        if(childNode.getToken().getTokenText().equals("Integer")){
            //If this is a valid id
            if(symbols.containsKey(id) && symbols.get(id).getType()== Symbol.variableType.INTEGER){
                symbols.get(id).changeValue(i_expr(exprNode));
            }

            //TODO ERROR
        }

        // A Double assignment statement
        else if(childNode.getToken().getTokenText().equals("Double")){
            if(symbols.containsKey(id) && symbols.get(id).getType()== Symbol.variableType.DOUBLE){
                symbols.get(id).changeValue(d_expr(exprNode));
            }

            //TODO ERROR
        }

        // A String asignment statement
        else{
            if(symbols.containsKey(id) && symbols.get(id).getType()== Symbol.variableType.STRING){
                symbols.get(id).changeValue(s_expr(exprNode));
            }

            //TODO ERROR
        }
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
            // TODO Error
        }

        // If it is an integer
        else if(childNode.getState().getState()== State.stateType.INT ||
                childNode.getState().getState()== State.stateType.SIGN){
            int operand;

            // If the first symbol is a sign
            if(childNode.getState().getState()== State.stateType.SIGN){
                operand=Integer.parseInt(node.getChildren().get(childNumber+1).getToken().getTokenText());

                if(childNode.getToken().getTokenType()==TokenType.Minus){
                    operand*=-1;
                }
                return operand;
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
            int secondOp=parseIntOperand(node, sign+1);

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

    private static boolean verifyDoubleID(TreeNode node){
        if(symbols.containsKey(node.getToken().getTokenText())){
            if(symbols.get(node.getToken().getTokenText()) != null &&
                    symbols.get(node.getToken().getTokenText()).getType()== Symbol.variableType.DOUBLE){
                return true;
            }
        }
        return false;
    }

    private static double parseDoubleOperand(TreeNode node, int childNumber){

        TreeNode childNode=node.getChildren().get(childNumber);

        // If the child node is an ID
        if(childNode.getToken().getTokenType()==TokenType.ID){
            if(verifyDoubleID(childNode)){
                return (Double)symbols.get(childNode.getToken().getTokenText()).getValue();
            }

            //TODO error
        }

        // If the child node is a double
        else if(childNode.getState().getState()== State.stateType.DBL||
                childNode.getState().getState()== State.stateType.SIGN){
            if(childNode.getState().getState()== State.stateType.SIGN){
                double operand=Double.parseDouble(node.getChildren().get(childNumber+1).getToken().getTokenText());

                if(childNode.getToken().getTokenType()==TokenType.Minus){
                    operand*=-1;
                }

                return operand;
            }
        }

        // If the child node is a d_expr
        else{
            return d_expr(childNode);
        }

        // Should never get to this point
        return -1;
    }

    private static double d_expr(TreeNode node){
        double firstOp=parseDoubleOperand(node, 0);

        // If there is more than one operand
        if(node.getChildren().size()>2){

            // Where the +, -, /, * sign is
            int sign=1;

            // If there was a sign at the beginning
            if(node.getChildren().get(0).getState().getState()== State.stateType.SIGN){
                sign=2;
            }

            // The value of the second operand
            double secondOp=parseDoubleOperand(node, sign+1);

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

        }

        // If there is only one operand
        else{
            return firstOp;
        }

        // Should never get to this point
        return -1;
    }

    private static boolean verifyStringID(TreeNode node){
        if(symbols.containsKey(node.getToken().getTokenText())){
            if(symbols.get(node.getToken().getTokenText()) != null &&
                    symbols.get(node.getToken().getTokenText()).getType()== Symbol.variableType.STRING){
                return true;
            }
        }
        return false;
    }

    private static String s_expr(TreeNode node){

        TreeNode childNode=node.getChildren().get(0);

        // It is a str_literal
        if(childNode.getToken().getTokenType()==TokenType.String){

            // Return everything but the quotes
            return (String)childNode.getToken().getTokenText().substring(1, -1);
        }

        // It is the concat function
        else if(childNode.getToken().getTokenText().equals("concat")){

            // Concats the strings without quotes
            return s_expr(node.getChildren().get(2))+ s_expr(node.getChildren().get(4));
        }

        // It is the charAt
        else if(childNode.getToken().getTokenText().equals("charAt")){
            return s_expr(node.getChildren().get(2)).charAt(i_expr(node.getChildren().get(4)))+"";
        }

        // It is an id
        else{
            if(verifyStringID(childNode)){
                return (String) symbols.get(childNode.getToken().getTokenText()).getValue();
            }

            // TODO error
        }

        // Should never get to this point
        return "";
    }

    private static void semanticError(TreeNode node, String message){
        System.err.println("Error on line "+node.getToken().getLineNum()+" column "+node.getToken().getColumnStart()+
                ": "+message);
        System.exit(1);
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

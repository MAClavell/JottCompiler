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

    }

}

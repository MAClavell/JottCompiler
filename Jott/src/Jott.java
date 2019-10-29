import java.util.ArrayList;
import java.util.HashMap;

public class Jott {

    public static void main(String[] args) {
        //Get file name
        if(args.length != 1)
        {
           System.out.println(
                   "Usage: java Jott program.j\n" +
                   "Compile and run a Jott program from a file.\n\n" +
                   "Parameters:\n" +
                   "program.j   File name of any program written in Jott.\n"
           );
           return;
        }

        //Read in the file
        String text = FileInput.readFile(args[0]);
        //System.out.println(text + '\n');

        //Setup error handler
        LogError.setupHandler(text.split("\n"), args[0]);

        //Create tokens from file
        ArrayList<Token> tokens = Scanner.tokenize(text, args[0]);
        for (Token t : tokens) {
            System.out.println(t);
        }
        System.out.println("");

//        HashMap<String, Symbol> symbolTable = new HashMap<String, Symbol>();
//        TreeNode root = Parser.parse(tokens, symbolTable);
//        //System.out.println(root);
//        SemanticAnalysis.output(root, symbolTable);
    }
}

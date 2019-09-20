import java.util.ArrayList;

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
        System.out.println(text + '\n');

        //Create tokens from file
        ArrayList<Token> tokens = Scanner.tokenize(text, args[0]);
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
}

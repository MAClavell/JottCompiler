import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileInput {
    /**
     * Reads an entire file and stores the contents in a string
     *
     * @param fileName: the name of the file to read
     *
     * @return the contents of the file
     */
    public static String readFile(String fileName){
        //The entire file to be returned
        String file="";
        try {
            //The buffered reader to read the file
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            //A single line
            int c;
            //Reads the file line-by-line
            while((c=bufferedReader.read())!=-1){
                file+=(char)c;
            }
        } catch (IOException e) {
            System.err.println("Your file was not found");
            return null;
        }
        return file;
    }
}
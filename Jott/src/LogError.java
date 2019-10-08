public class LogError {
    static String[] lines;
    static String fileName;
    enum ErrorType{SYNTAX,RUNTIME}

    public static void setupHandler(String[] ls, String fN)
    {
        lines = ls;
        fileName = fN;
    }

    public static void log(ErrorType eType, String message, Token eToken){
        System.out.println(eType+": "+message+", "+"\""+lines[eToken.getLineNum()]+"\" ("+fileName+":"+eToken.getLineNum()+")");
        System.exit(1);
    }
}

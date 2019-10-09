public class LogError {
    static String[] lines;
    static String fileName;

    enum ErrorType{
        SYNTAX {
            @Override
            public String toString() {
                return "Syntax Error";
            }
        },
        RUNTIME {
            @Override
            public String toString() {
                return "Runtime Error";
            }
        }
    }

    public static void setupHandler(String[] ls, String fN) {
        lines = ls;
        fileName = fN;
    }

    public static void log(ErrorType eType, String message, Token eToken){
        System.out.println(eType+": "+message+", "+"\""+lines[eToken.getLineNum()-1]+"\" ("+fileName+":"+eToken.getLineNum()+
            ","+eToken.getColumnStart()+"-"+eToken.getColumnEnd()+")");
        System.exit(1);
    }

    public static void log(ErrorType eType, String message, int lineNumber, int columnStart, int columnEnd){
        System.out.println(eType+": "+message+", "+"\""+lines[lineNumber-1]+"\" ("+fileName+":"+lineNumber+
            ","+eToken.getColumnStart()+"-"+eToken.getColumnEnd()+")");
        System.exit(1);
    }
}

import lexic.Lexer;
import syntactic.Parser;
import model.PC;

import java.io.*;

public class Main {
    
    public static void main( String[] args ) {
        String buffer = "";
        
        try ( BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) ) ) {
            int code = 0;
            
            while ( ( code = reader.read() ) != -1 ) buffer += String.valueOf( ( char ) code );
            
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        
        Parser parser = Parser.getInstance( Lexer.getInstance( buffer ) );
        parser.Program();
        
        if ( !( parser.getToken().equals( PC.EOF ) ) ) {
            parser.syntaxError( PC.EOF );
        }
        
        System.out.print( "El analisis sintactico ha finalizado exitosamente." );
    }
}

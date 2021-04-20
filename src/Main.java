import lexic.Lexer;
import model.token.Token;

import java.io.*;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    
    public static void main( String[] args ) {
        
        try ( Scanner scanner = new Scanner( new File("36.in") ) ) {
            String line = null;
            
            Lexer lexer = Lexer.getInstance();
            
            Optional<Token> currentToken;
            
            while ( scanner.hasNext() ) {
                line = scanner.nextLine();
                line += String.valueOf( '\n' );
                
                lexer.addToBuffer( line );
                
                do {
                    currentToken = lexer.getNextToken();
                    currentToken.ifPresent( System.out::println );
                } while ( currentToken.isPresent() );
                
                //if (!scanner.hasNext()) break;
            }
            
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}

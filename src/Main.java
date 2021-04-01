import java.io.*;
import java.util.*;

public class Main {
    
    static int currentRow = 1, currentCol;
    
    static Classifier classifier;
    
    public static void main( String[] args ) {
        /*String filename = "test.txt";
        File inputFile = new File( filename );*/
        
        List<Token> tokens = new ArrayList<>();
        
        try ( BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( System.in ) ) ) {
            String currentLine = null, lexeme = "";
            char currentSymbol;
            int state = 0;
            
            boolean readingMultilineComment = false;
            
            while ( ( currentLine = bufferedReader.readLine() ) != null ) {
                currentLine += String.valueOf( '\0' );
                for ( currentCol = 0; currentCol < currentLine.length(); currentCol++ ) {
                    //System.out.println( "col:" + ( currentCol + 1 ) );
                    currentSymbol = currentLine.charAt( currentCol );
                    
                    if ( currentSymbol == '\0' ) continue;
                    
                    if ( currentSymbol == ' ' ) continue;
                    
                    /* Check for multiline comments
                     *  ...
                     * */
                    if ( currentSymbol == '/'
                            && currentCol + 1 < currentLine.length()
                            && currentLine.charAt( currentCol + 1 ) == '*' ) {
                        readingMultilineComment = true;
                    }
                    
                    if ( readingMultilineComment ) {
                        if ( currentSymbol == '*' && currentCol + 1 < currentLine.length()
                                && currentLine.charAt( currentCol + 1 ) == '/' ) {
                            readingMultilineComment = false;
                            currentCol += 1;
                        }
                        continue;
                    }
                    
                    if ( currentSymbol == '/'
                            && currentCol + 1 < currentLine.length()
                            && currentLine.charAt( currentCol + 1 ) == '/' ) break;
                    
                    /* Check for reservedKeywords and custom identifiers
                        ....
                     */
                    classifier = Classifier.getIdentifier();
                    state = classifier.delta( currentSymbol, 0 );
                    
                    if ( state >= 0 ) {
                        lexeme = String.valueOf( currentSymbol );
                        int column = currentCol + 1;
                        char symbol = currentLine.charAt( column );
                        
                        try {
                            while ( ( state = classifier.delta( symbol, state ) ) >= 0 ) {
                                ++column;
                                lexeme += symbol;
                                symbol = currentLine.charAt( column );
                            }
                            
                            System.out.println(
                                    Token.getInstance(
                                            lexeme,
                                            currentRow,
                                            currentCol + 1,
                                            ( TokenTypes.reservedKeywords.contains( lexeme ) )
                                                    ? TokenTypes.KEYWORD : TokenTypes.IDENTIFIER
                                    )
                            );
                            
                            currentCol = column - 1;
                            continue;
                        } catch ( IllegalStateException ise ) {
                            System.out.println( "El numero de estado no pertenece al clasificador de identificadores" );
                            System.exit( -1 );
                        }
                    }
                    
                    // Check for number
                    classifier = Classifier.getNumbers();
                    state = classifier.delta( currentSymbol, 0 );
                    
                    if ( state >= 0 ) {
                        lexeme = String.valueOf( currentSymbol );
                        int column = currentCol + 1;
                        char symbol = currentLine.charAt( column );
                        
                        try {
                            while ( ( state = classifier.delta( symbol, state ) ) >= 0 ) {
                                lexeme += symbol;
                                ++column;
                                
                                if ( state >= 4 && state <= 6 ) {
                                    final int numToSubstract = state == 5 ? 2 : 1;
                                    column -= numToSubstract;
                                    
                                    System.out.println( Token.getInstance(
                                            lexeme.substring( 0, lexeme.length() - numToSubstract ),
                                            currentRow,
                                            currentCol + 1,
                                            state == 6 ? TokenTypes.REAL : TokenTypes.INTEGER
                                    ) );
                                    
                                    currentCol = column - 1;
                                    break;
                                }
                                
                                symbol = currentLine.charAt( column );
                            }
                            
                            continue;
                        } catch ( IllegalStateException ise ) {
                            System.out.println( "El numero de estado no pertenece al clasificador de numeros" );
                            System.exit( -1 );
                        }
                    }
                    
                    //Check for string
                    classifier = Classifier.getString();
                    state = classifier.delta( currentSymbol, 0 );
                    
                    if ( state >= 0 ) {
                        lexeme = String.valueOf( currentSymbol );
                        int column = currentCol + 1;
                        char symbol = currentLine.charAt( column );
                        
                        try {
                            while ( ( state = classifier.delta( symbol, state ) ) >= 0 ) {
                                ++column;
                                lexeme += symbol;
                                symbol = currentLine.charAt( column );
                            }
                            
                            System.out.println( Token.getInstance( lexeme, currentRow, currentCol + 1, TokenTypes.STRING ) );
                            
                            currentCol = column - 1;
                            continue;
                        } catch ( IllegalStateException ise ) {
                            System.out.println( "El numero de estado no pertenece al clasificador de cadenas" );
                            System.exit( -1 );
                        }
                    }
                    
                    //Check for a character
                    classifier = Classifier.getCharacter();
                    state = classifier.delta( currentSymbol, 0 );
                    
                    if ( state >= 0 ) {
                        if ( state == 4 ) {
                            continue;
                        }
                        
                        int column = currentCol + 1;
                        char symbol = currentLine.charAt( column );
                        lexeme = String.valueOf( currentSymbol );
                        
                        try {
                            while ( ( state = classifier.delta( symbol, state ) ) >= 0 ) {
                                ++column;
                                lexeme += symbol;
                                symbol = currentLine.charAt( column );
                            }
                            
                            System.out.println( Token.getInstance( lexeme, currentRow, currentCol + 1, TokenTypes.CHARACTER ) );
                            
                            currentCol = column - 1;
                            continue;
                        } catch ( CharacterException ce ) {
                            System.out.printf( ">>> Error lexico (linea: %d, posicion: %d)\n", currentRow, column );
                            System.exit( 0 );
                        } catch ( IllegalStateException ise ) {
                            System.out.println( "El numero de estado no pertenece al clasificador de caracteres" );
                            System.exit( -1 );
                        }
                    }
                    
                    //Check for operators and special symbols
                    classifier = Classifier.getOperatorAndSpSymbol();
                    state = classifier.delta( currentSymbol, 0 );
                    
                    if ( state >= 0 ) {
                        lexeme = String.valueOf( currentSymbol );
                        int column = currentCol + 1;
                        char symbol = currentLine.charAt( column );
                        
                        try {
                            while ( ( state = classifier.delta( symbol, state ) ) >= 0 ) {
                                if ( state != 5 ) {
                                    ++column;
                                    lexeme += symbol;
                                    symbol = currentLine.charAt( column );
                                } else {
                                    break;
                                }
                            }
                            
                            System.out.println(
                                    Token.getInstance( lexeme, currentRow, currentCol + 1, TokenTypes.typeIds.get( lexeme ) )
                            );
                            
                            currentCol = column - 1;
                            continue;
                        } catch ( IllegalStateException ise ) {
                            System.out.println( "El numero de estado no pertenece al clasificador de operadores y simbolos especiales" );
                            System.exit( -1 );
                        }
                    }
                    
                    System.out.printf( ">>> Error lexico (linea: %d, posicion: %d)\n", currentRow, currentCol + 1 );
                    System.exit( 0 );
                }
                currentRow++;
            }
            
            if ( readingMultilineComment ) {
                System.out.printf( ">>> Error lexico (linea: %d, posicion: %d)\n", currentRow, currentCol + 1 );
                System.exit( 0 );
            }
            
        } catch (
                IOException e ) {
            e.printStackTrace();
        }
    }
}

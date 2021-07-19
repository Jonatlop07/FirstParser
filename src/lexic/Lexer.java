package lexic;

import lexic.classifiers.Classifier;
import lexic.classifiers.exceptions.CharacterException;
import lexic.classifiers.exceptions.IllegalClassifierStateException;
import lexic.classifiers.exceptions.IllegalStringException;
import model.Token;
import model.TokenTypes;

import java.util.Optional;

public class Lexer {
    private final String buffer;
    
    private Classifier classifier;
    
    private int currentBufferPos;
    private int bufferPosAux;
    private int currentRow;
    private int currentCol;
    private int currClassifState;
    
    private String currentLexem = "";
    
    private char currentSymbol;
    
    private boolean readingSingleLineComment = false, readingMultilineComment = false;
    
    private static Lexer uniqueInstance;
    
    private Lexer( String input ) {
        buffer = input;
        currentBufferPos = bufferPosAux = currClassifState = 0;
        currentCol = currentRow = 1;
    }
    
    public static Lexer getInstance( String input ) {
        if ( uniqueInstance == null ) {
            uniqueInstance = new Lexer( input );
        }
        return uniqueInstance;
    }
    
    public Token getNextToken() {
        Optional<Token> result;
        
        for ( ; currentBufferPos < buffer.length(); ++currentBufferPos ) {
            currentSymbol = buffer.charAt( currentBufferPos );
            
            if ( currentSymbol == '\t' || currentSymbol == ' ' ) {
                ++currentCol;
                continue;
            }
            
            if ( currentSymbol == '\n' ) {
                
                if ( readingSingleLineComment ) {
                    readingSingleLineComment = false;
                }
                
                currentCol = 1;
                ++currentRow;
                continue;
            }
            
            if ( currentSymbol == '\r' && buffer.charAt( currentBufferPos + 1 ) == '\n' ) {
                if ( readingSingleLineComment ) {
                    readingSingleLineComment = false;
                }
                
                currentCol = 1;
                ++currentRow;
                ++currentBufferPos;
                continue;
            }
            
            if ( !readingSingleLineComment ) {
                if ( currentSymbol == '/' && buffer.charAt( currentBufferPos + 1 ) == '/' ) {
                    ++currentCol;
                    readingSingleLineComment = true;
                    continue;
                }
            } else {
                continue;
            }
            
            if ( !readingMultilineComment ) {
                if ( currentSymbol == '/' && buffer.charAt( currentBufferPos + 1 ) == '*' ) {
                    readingMultilineComment = true;
                    ++currentCol;
                }
            }
            
            if ( readingMultilineComment ) {
                ++currentCol;
                
                if ( currentSymbol == '*' && buffer.charAt( currentBufferPos + 1 ) == '/' ) {
                    readingMultilineComment = false;
                    ++currentBufferPos;
                }
                continue;
            }
            
            try {
                result = classifyIfReservedKeywordOfIdentifier();
                
                if ( result.isPresent() ) {
                    return result.get();
                }
                
                result = classifyIfNumber();
                
                if ( result.isPresent() ) {
                    return result.get();
                }
                
                result = classifyIfString();
                
                if ( result.isPresent() ) {
                    return result.get();
                }
                
                result = classifyIfCharacter();
                
                if ( result.isPresent() ) {
                    return result.get();
                }
                
                result = classifyIfOperatorOrSpecialSymbol();
                
                if ( result.isPresent() ) {
                    return result.get();
                }
                
            } catch ( CharacterException | IllegalStringException e ) {
                System.out.printf( ">>> Error lexico (linea: %d, posicion: %d)\n", currentRow, currentCol + ( bufferPosAux - currentBufferPos ) );
                System.exit( 0 );
            } catch ( IllegalClassifierStateException ise ) {
                System.out.println( "El numero de estado no pertenece al clasificador de " + ise.getClassifierType() );
                System.exit( -1 );
            }
            
            System.out.printf( ">>> Error lexico (linea: %d, posicion: %d)\n", currentRow, currentCol + 1 );
            System.exit( 0 );
        }
        
        checkForUnclosedMultilineComment();
        
        return Token.getInstance("EOF", currentRow + 1, 1, TokenTypes.EOF);
    }
    
    /*private Optional<model.token.Token> classify() throws RuntimeException {
        currClassifState = classifier.delta( currentSymbol, 0 );
        
        if ( currClassifState >= 0 ) {
            currentLexem = String.valueOf( currentSymbol );
            bufferPosAux = currentBufferPos + 1;
            char symbol = buffer.charAt( bufferPosAux );
            
            //Loop
            
            model.token.Token result = model.token.Token.getInstance( currentLexem, currentRow, currentCol, model.token.model.TokenTypes.STRING );
            
            currentCol += bufferPosAux - currentBufferPos;
            currentBufferPos = bufferPosAux;
            
            return Optional.of( result );
        }
        return Optional.empty();
    }*/
    
    private Optional<Token> classifyIfReservedKeywordOfIdentifier() throws RuntimeException {
        classifier = Classifier.getIdentifier();
        currClassifState = classifier.delta( currentSymbol, 0 );
        
        if ( currClassifState >= 0 ) {
            currentLexem = String.valueOf( currentSymbol );
            bufferPosAux = currentBufferPos + 1;
            char symbol = buffer.charAt( bufferPosAux );
            
            while ( ( currClassifState = classifier.delta( symbol, currClassifState ) ) >= 0 ) {
                ++bufferPosAux;
                currentLexem += symbol;
                if ( bufferPosAux < buffer.length() ) {
                    symbol = buffer.charAt( bufferPosAux );
                } else {
                    break;
                }
            }
            
            Token result = Token.getInstance(
                currentLexem,
                currentRow,
                currentCol,
                ( TokenTypes.reservedKeywords.contains( currentLexem ) ) ? TokenTypes.KEYWORD : TokenTypes.IDENTIFIER
            );
            
            currentCol += bufferPosAux - currentBufferPos;
            currentBufferPos = bufferPosAux;
            
            return Optional.of( result );
        }
        return Optional.empty();
    }
    
    private Optional<Token> classifyIfNumber() throws RuntimeException {
        classifier = Classifier.getNumbers();
        currClassifState = classifier.delta( currentSymbol, 0 );
        
        if ( currClassifState >= 0 ) {
            currentLexem = String.valueOf( currentSymbol );
            bufferPosAux = currentBufferPos + 1;
            char symbol = buffer.charAt( bufferPosAux );
            
            while ( ( currClassifState = classifier.delta( symbol, currClassifState ) ) >= 0 ) {
                currentLexem += symbol;
                ++bufferPosAux;
                
                if ( currClassifState >= 4 && currClassifState <= 6 ) {
                    final int numToSubstract = currClassifState == 5 ? 2 : 1;
                    bufferPosAux -= numToSubstract;
                    
                    Token result = Token.getInstance(
                        currentLexem.substring( 0, currentLexem.length() - numToSubstract ),
                        currentRow,
                        currentCol,
                        currClassifState == 6 ? TokenTypes.REAL : TokenTypes.INTEGER
                    );
                    
                    currentCol += bufferPosAux - currentBufferPos;
                    currentBufferPos = bufferPosAux;
                    
                    return Optional.of( result );
                }
                symbol = buffer.charAt( bufferPosAux );
            }
        }
        return Optional.empty();
    }
    
    private Optional<Token> classifyIfString() throws RuntimeException {
        classifier = Classifier.getString();
        currClassifState = classifier.delta( currentSymbol, 0 );
        
        if ( currClassifState >= 0 ) {
            currentLexem = String.valueOf( currentSymbol );
            bufferPosAux = currentBufferPos + 1;
            char symbol = buffer.charAt( bufferPosAux );
            
            while ( ( currClassifState = classifier.delta( symbol, currClassifState ) ) >= 0 ) {
                ++bufferPosAux;
                currentLexem += symbol;
                symbol = buffer.charAt( bufferPosAux );
            }
            
            Token result = Token.getInstance( currentLexem, currentRow, currentCol, TokenTypes.STRING );
            
            currentCol += bufferPosAux - currentBufferPos;
            currentBufferPos = bufferPosAux;
            
            return Optional.of( result );
        }
        return Optional.empty();
    }
    
    private Optional<Token> classifyIfCharacter() throws RuntimeException {
        classifier = Classifier.getCharacter();
        currClassifState = classifier.delta( currentSymbol, 0 );
        
        if ( currClassifState >= 0 ) {
            currentLexem = String.valueOf( currentSymbol );
            bufferPosAux = currentBufferPos + 1;
            char symbol = buffer.charAt( bufferPosAux );
            
            while ( ( currClassifState = classifier.delta( symbol, currClassifState ) ) >= 0 ) {
                ++bufferPosAux;
                currentLexem += symbol;
                symbol = buffer.charAt( bufferPosAux );
            }
            
            Token result = Token.getInstance( currentLexem, currentRow, currentCol, TokenTypes.CHARACTER );
            
            currentCol += bufferPosAux - currentBufferPos;
            currentBufferPos = bufferPosAux;
            
            return Optional.of( result );
        }
        
        return Optional.empty();
    }
    
    private Optional<Token> classifyIfOperatorOrSpecialSymbol() throws RuntimeException {
        classifier = Classifier.getOperatorAndSpSymbol();
        currClassifState = classifier.delta( currentSymbol, 0 );
        
        if ( currClassifState >= 0 ) {
            currentLexem = String.valueOf( currentSymbol );
            bufferPosAux = currentBufferPos + 1;
            char symbol = buffer.charAt( bufferPosAux );
            
            while ( ( currClassifState = classifier.delta( symbol, currClassifState ) ) >= 0 ) {
                if ( currClassifState != 5 ) {
                    ++bufferPosAux;
                    currentLexem += symbol;
                    symbol = buffer.charAt( bufferPosAux );
                } else break;
            }
            
            Token result = Token.getInstance(
                currentLexem,
                currentRow,
                currentCol,
                TokenTypes.typeIds.get( currentLexem )
            );
            
            currentCol += bufferPosAux - currentBufferPos;
            currentBufferPos = bufferPosAux;
            
            return Optional.of( result );
        }
        return Optional.empty();
    }
    
    private void checkForUnclosedMultilineComment() {
        if ( readingMultilineComment ) {
            System.out.printf( ">>> Error lexico (linea: %d, posicion: %d)\n", currentRow, currentCol );
            System.exit( 0 );
        }
    }
}

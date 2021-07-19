package lexic.classifiers;

import lexic.classifiers.exceptions.IllegalClassifierStateException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class OperatorAndSpSymbolClassifier extends Classifier {
    
    private static Classifier uniqueInstance;
    
    private static Set<Character> unicharacterOperators = new HashSet<>(
        Arrays.asList( '+', '-', '*', '/', '%', ':', ';', ',', '.', '(', ')' )
    );
    
    private OperatorAndSpSymbolClassifier() {}
    
    public static Classifier getInstance() {
        if ( uniqueInstance == null ) {
            return new OperatorAndSpSymbolClassifier();
        }
        return uniqueInstance;
    }
    
    public int delta( char symbol, int state ) throws RuntimeException {
        switch ( state ) {
            case 0:
                if ( unicharacterOperators.contains( symbol ) ) return 4;
                if ( symbol == '=' || symbol == '<' || symbol == '>' || symbol == '!' ) return 1;
                if ( symbol == '&' ) return 2;
                if ( symbol == '|' ) return 3;
                return -1;
            case 1:
                if ( symbol == '=' ) return 6;
                return 5;
            case 2:
                if ( symbol == '&' ) return 7;
                return -2;
            case 3:
                if ( symbol == '|' ) return 8;
                return -2;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return -1;
            default:
                throw new IllegalClassifierStateException( "operadores y simbolos especiales" );
        }
    }
}

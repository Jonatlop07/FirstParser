package lexic.classifiers;

import lexic.classifiers.exceptions.IllegalClassifierStateException;
import lexic.classifiers.exceptions.IllegalStringException;

class StringClassifier extends Classifier {
    
    private static Classifier uniqueInstance;
    
    private StringClassifier() {}
    
    public static Classifier getInstance() {
        if ( uniqueInstance == null ) {
            return new StringClassifier();
        }
        return uniqueInstance;
    }
    
    public int delta( char symbol, int state ) throws RuntimeException {
        switch ( state ) {
            case 0:
                if ( symbol == '"' ) return 1;
                return -1;
            case 1:
                if ( symbol == '"' ) return 2;
                if ( symbol == '\n' || symbol == '\r' || symbol == '\0' )
                    throw new IllegalStringException();
                return 1;
            case 2:
                return -1;
            default:
                throw new IllegalClassifierStateException( "cadenas" );
        }
    }
}

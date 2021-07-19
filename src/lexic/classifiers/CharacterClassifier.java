package lexic.classifiers;

import lexic.classifiers.exceptions.CharacterException;
import lexic.classifiers.exceptions.IllegalClassifierStateException;

class CharacterClassifier extends Classifier {
    
    private static Classifier uniqueInstance;
    
    private CharacterClassifier() {}
    
    public static Classifier getInstance() {
        if ( uniqueInstance == null ) {
            return new CharacterClassifier();
        }
        return uniqueInstance;
    }
    
    public int delta( char symbol, int state ) throws RuntimeException {
        switch ( state ) {
            case 0:
                if ( symbol == '\'' ) return 1;
                return -1;
            case 1:
                if ( Character.isLetter( symbol ) || Character.toLowerCase( symbol ) != 'ñ'
                    || symbol == '_' || symbol == '\n' || symbol == ' ' )
                    return 2;
                if ( symbol == '\'' ) return 3;
                return -2;
            case 2:
                if ( symbol == '\'' ) return 3;
                throw new CharacterException();
            case 3:
                return -1;
            default:
                throw new IllegalClassifierStateException( "caracteres" );
        }
    }
}

package lexic.clasiffiers;

class IdentifiersClassifier extends Classifier {
    
    private static Classifier uniqueInstance;
    
    private IdentifiersClassifier() {}
    
    public static Classifier getInstance() {
        if ( uniqueInstance == null ) {
            return new IdentifiersClassifier();
        }
        return uniqueInstance;
    }
    
    public int delta( char symbol, int state ) throws RuntimeException {
        switch ( state ) {
            case 0:
                if ( Character.isLetter( symbol ) && Character.toLowerCase( symbol ) != 'ñ' ) return 1;
                return -1;
            case 1:
                if ( Character.isLetter( symbol ) || Character.isDigit( symbol )
                    || symbol == '_' && Character.toLowerCase( symbol ) != 'ñ' )
                    return 1;
                return -1;
            default:
                throw new IllegalStateException( "identificadores" );
        }
    }
}

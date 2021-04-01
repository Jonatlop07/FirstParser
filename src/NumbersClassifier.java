class NumbersClassifier extends Classifier {
    
    private static Classifier uniqueInstance;
    
    private NumbersClassifier() {}
    
    public static Classifier getInstance() {
        if ( uniqueInstance == null ) {
            return new NumbersClassifier();
        }
        return uniqueInstance;
    }
    
    @Override
    public int delta( char symbol, int state ) throws RuntimeException {
        switch ( state ) {
            case 0:
                if ( Character.isDigit( symbol ) ) return 1;
                return -1;
            case 1:
                if ( Character.isDigit( symbol ) ) return 1;
                if ( symbol == '.' ) return 2;
                return 4;
            case 2:
                if ( Character.isDigit( symbol ) ) return 3;
                return 5;
            case 3:
                if ( Character.isDigit( symbol ) ) return 3;
                return 6;
            case 4:
            case 5:
            case 6:
                return -1;
            default:
                throw new IllegalStateException();
        }
    }
}

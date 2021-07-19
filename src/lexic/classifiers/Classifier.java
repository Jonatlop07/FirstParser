package lexic.classifiers;

public abstract class Classifier {
    
    public static Classifier getIdentifier() {
        return IdentifiersClassifier.getInstance();
    }
    
    public static Classifier getNumbers() {
        return NumbersClassifier.getInstance();
    }
    
    public static Classifier getString() {
        return StringClassifier.getInstance();
    }
    
    public static Classifier getCharacter() {
        return CharacterClassifier.getInstance();
    }
    
    public static Classifier getOperatorAndSpSymbol() {
        return OperatorAndSpSymbolClassifier.getInstance();
    }
    
    public abstract int delta( char symbol, int state ) throws RuntimeException;
}

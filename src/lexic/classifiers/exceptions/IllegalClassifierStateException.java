package lexic.classifiers.exceptions;

public class IllegalClassifierStateException extends RuntimeException {
    private String classifierType;
    
    public IllegalClassifierStateException( String classifierType ) {
        super();
        this.classifierType = classifierType;
    }
    
    public String getClassifierType() {
        return classifierType;
    }
}

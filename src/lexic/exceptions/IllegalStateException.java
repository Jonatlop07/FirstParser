package lexic.exceptions;

public class IllegalStateException extends RuntimeException {
    private String classifierType;
    
    public IllegalStateException( String classifierType ) {
        super();
        this.classifierType = classifierType;
    }
    
    public String getClassifierType() {
        return classifierType;
    }
}

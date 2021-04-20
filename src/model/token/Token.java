package model.token;

public class Token {
    
    private final String lexem;
    private final int row;
    private final int column;
    private final String type;
    
    private Token( String lexem, int row, int column, String type ) {
        this.lexem = lexem;
        this.row = row;
        this.column = column;
        this.type = type;
    }
    
    public static Token getInstance( String lexem, int row, int column, String type ) {
        return new Token( lexem, row, column, type );
    }
    
    public String getLexem() { return lexem; }
    
    public String getType() { return type; }
    
    @Override
    public String toString() {
        String identifier;
        
        if ( type.equals( TokenTypes.KEYWORD ) ) {
            identifier = lexem;
        } else if ( TokenTypes.typeIds.containsKey( lexem ) ) {
            identifier = TokenTypes.typeIds.get( lexem );
        } else {
            identifier = type + "," + lexem;
        }
        
        return new StringBuilder()
            .append( "<" )
            .append( identifier )
            .append( "," )
            .append( row )
            .append( "," )
            .append( column )
            .append( ">" )
            .toString();
    }
}

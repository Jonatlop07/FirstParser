package model;

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
    
    private Token( String lexem, String type ) {
        this.lexem = lexem;
        this.row = 0;
        this.column = 0;
        this.type = type;
    }
    
    public static Token getInstance( String lexem, int row, int column, String type ) {
        return new Token( lexem, row, column, type );
    }
    
    public static Token getInstance( String lexem, String type ) {
        return new Token( lexem, type );
    }
    
    public String getLexem() { return lexem; }
    
    public int getRow() { return row; }
    
    public int getColumn() { return column; }
    
    public String getType() { return type; }
    
    public boolean equals( Token token ) {
        if ( ( this.getType() ).equals( token.getType() ) ) {
            if ( ( this.getType() ).equals( TokenTypes.KEYWORD ) ) {
                return ( this.getLexem() ).equals( token.getLexem() );
            }
            return true;
        }
        return false;
    }
    
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

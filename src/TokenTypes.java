import java.util.*;

final class TokenTypes {
    
    private TokenTypes() {}
    
    public static final String IDENTIFIER = "id";
    public static final String INTEGER = "tk_entero";
    public static final String REAL = "tk_real";
    public static final String STRING = "tk_cadena";
    public static final String CHARACTER = "tk_caracter";
    public static final String KEYWORD = "keyword";
    
    public static final HashSet<String> reservedKeywords = new HashSet<>( Arrays.asList(
            "funcion_principal", "fin_principal",
            "booleano", "verdadero", "falso",
            "caracter", "entero", "real", "cadena",
            "imprimir", "leer",
            "si", "si_no", "entonces", "fin_si",
            "mientras", "para", "hacer", "fin_mientras", "fin_para",
            "seleccionar", "entre", "caso", "romper", "defecto", "fin_seleccionar",
            "estructura", "fin_estructura",
            "funcion", "retornar", "fin_funcion"
    ) );
    
    public static final HashMap<String, String> typeIds;
    
    static {
        typeIds = new HashMap<String, String>();
        typeIds.put( "+", "tk_mas" );
        typeIds.put( "-", "tk_menos" );
        typeIds.put( "*", "tk_mult" );
        typeIds.put( "/", "tk_div" );
        typeIds.put( "%", "tk_mod" );
        typeIds.put( "=", "tk_asig" );
        typeIds.put( "<", "tk_menor" );
        typeIds.put( "<=", "tk_menor_igual" );
        typeIds.put( ">", "tk_mayor" );
        typeIds.put( ">=", "tk_mayor_igual" );
        typeIds.put( "==", "tk_igual" );
        typeIds.put( "&&", "tk_y" );
        typeIds.put( "||", "tk_o" );
        typeIds.put( "!=", "tk_dif" );
        typeIds.put( "!", "tk_neg" );
        typeIds.put( ":", "tk_dosp" );
        typeIds.put( ";", "tk_pyc" );
        typeIds.put( ",", "tk_coma" );
        typeIds.put( ".", "tk_punto" );
        typeIds.put( "(", "tk_par_izq" );
        typeIds.put( ")", "tk_par_der" );
    }
}

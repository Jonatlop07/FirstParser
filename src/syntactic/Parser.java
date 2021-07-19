package syntactic;

import lexic.Lexer;
import model.PC;
import model.Token;

public class Parser {
    
    private static Parser uniqueInstance;
    
    private final Lexer lexer;
    
    private Token token;
    
    private Parser( Lexer lexer ) {
        this.lexer = lexer;
        token = lexer.getNextToken();
    }
    
    public static Parser getInstance( Lexer lexer ) {
        if ( uniqueInstance == null ) {
            uniqueInstance = new Parser( lexer );
        }
        return uniqueInstance;
    }
    
    public Token getToken() {
        return token;
    }
    
    private void mainFunctionSyntaxError() {
        System.out.print( "Error sintactico: falta funcion_principal" );
        System.exit( 0 );
    }
    
    public void syntaxError( Token... tokens ) {
        String message = "<" + ( token.getRow() ) + "," + ( token.getColumn() )
            + "> Error sintactico: se encontro: " + "\"" + token.getLexem() + "\"; "
            + "se esperaba: ";
        
        for ( Token currToken : tokens ) {
            message += "\"" + currToken.getLexem() + "\", ";
        }
        
        message = message.substring( 0, message.length() - 2 ) + ".";
        
        System.out.print( message );
        
        System.exit( 0 );
    }
    
    private void match( Token expectedToken ) {
        if ( token.equals( expectedToken ) ) {
            token = lexer.getNextToken();
        } else syntaxError( expectedToken );
    }
    
    public void Program() {
        if ( token.equals( PC.MAIN_START ) || token.equals( PC.ID ) || token.equals( PC.BOOL )
            || token.equals( PC.CHAR ) || token.equals( PC.INT ) || token.equals( PC.REAL )
            || token.equals( PC.STRING ) || token.equals( PC.STRUCT ) || token.equals( PC.FUNCT ) ) {
            DeclarationBeforeMain();
            Main();
            DeclarationAfterMain();
        } else mainFunctionSyntaxError();
    }
    
    private void DeclarationBeforeMain() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.STRUCT ) || token.equals( PC.FUNCT ) ) {
            Declaration();
            DeclarationBeforeMain();
        } else if ( token.equals( PC.MAIN_START ) || token.equals( PC.EOF ) ) {
        } else syntaxError( PC.ID, PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING, PC.STRUCT, PC.FUNCT );
    }
    
    private void DeclarationAfterMain() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL )
            || token.equals( PC.CHAR ) || token.equals( PC.INT ) || token.equals( PC.REAL )
            || token.equals( PC.STRING ) || token.equals( PC.STRUCT ) || token.equals( PC.FUNCT ) ) {
            Declaration();
            DeclarationAfterMain();
        } else if ( token.equals( PC.EOF ) ) {
        } else syntaxError( PC.ID, PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING, PC.STRUCT, PC.FUNCT, PC.EOF );
    }
    
    private void Main() {
        if ( token.equals( PC.MAIN_START ) ) {
            match( PC.MAIN_START );
            MainBody();
        } else mainFunctionSyntaxError();
    }
    
    private void MainBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            MainBody();
        } else if ( token.equals( PC.MAIN_END ) ) {
            match( PC.MAIN_END );
        } else syntaxError( PC.ID, PC.MAIN_END, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT,
            PC.REAL, PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.BREAK );
    }
    
    private void BodyFeature() {
        if ( token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING ) ) {
            VariableDeclaration();
        } else if ( token.equals( PC.ID ) ) {
            match( PC.ID );
            Aux();
        } else if ( token.equals( PC.WHILE ) ) {
            While();
        } else if ( token.equals( PC.DO ) ) {
            DoWhile();
        } else if ( token.equals( PC.FOR ) ) {
            For();
        } else if ( token.equals( PC.SELECT ) ) {
            MultSelection();
        } else if ( token.equals( PC.IF ) ) {
            Conditional();
        } else if ( token.equals( PC.READ ) ) {
            Read();
        } else if ( token.equals( PC.PRINT ) ) {
            Print();
        } else if ( token.equals( PC.BREAK ) ) {
            match( PC.BREAK );
            match( PC.SEMI_COLON );
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT,
            PC.REAL, PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.BREAK );
    }
    
    private void DataType() {
        if ( token.equals( PC.BOOL ) || token.equals( PC.CHAR ) || token.equals( PC.INT )
            || token.equals( PC.REAL ) || token.equals( PC.STRING ) ) {
            PrimitiveDataType();
        } else if ( token.equals( PC.ID ) ) {
            match( PC.ID );
        } else syntaxError( PC.ID, PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING );
    }
    
    private void Aux() {
        if ( token.equals( PC.ID ) ) {
            match( PC.ID );
            VariableFirstAssignment();
        } else if ( token.equals( PC.DOT ) || token.equals( PC.ASIG ) ) {
            StructMember();
            VariableAssignment();
        } else if ( token.equals( PC.LEFT_PAR ) ) {
            FunctionCall();
            match( PC.SEMI_COLON );
        } else syntaxError( PC.ASIG, PC.DOT, PC.LEFT_PAR, PC.ID );
    }
    
    private void VariableAssignment() {
        if ( token.equals( PC.ASIG ) ) {
            match( PC.ASIG );
            Expression();
            match( PC.SEMI_COLON );
        } else syntaxError( PC.ASIG );
    }
    
    private void Declaration() {
        if ( token.equals( PC.STRUCT ) ) {
            StructDeclaration();
        } else if ( token.equals( PC.FUNCT ) ) {
            FunctionDeclaration();
        } else syntaxError( PC.STRUCT, PC.FUNCT );
    }
    
    private void VariableDeclaration() {
        if ( token.equals( PC.BOOL ) || token.equals( PC.CHAR ) || token.equals( PC.INT )
            || token.equals( PC.REAL ) || token.equals( PC.STRING ) ) {
            PrimitiveDataType();
            match( PC.ID );
            VariableFirstAssignment();
        } else syntaxError( PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING );
    }
    
    private void PrimitiveDataType() {
        if ( token.equals( PC.BOOL ) ) {
            match( PC.BOOL );
        } else if ( token.equals( PC.CHAR ) ) {
            match( PC.CHAR );
        } else if ( token.equals( PC.INT ) ) {
            match( PC.INT );
        } else if ( token.equals( PC.REAL ) ) {
            match( PC.REAL );
        } else if ( token.equals( PC.STRING ) ) {
            match( PC.STRING );
        } else syntaxError( PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING );
    }
    
    private void StructMember() {
        if ( token.equals( PC.DOT ) ) {
            match( PC.DOT );
            match( PC.ID );
            StructMember();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.ASIG )
            || token.equals( PC.COMMA ) || token.equals( PC.RIGHT_PAR )
            || token.equals( PC.OR ) || token.equals( PC.AND )
            || token.equals( PC.EQUALS ) || token.equals( PC.DIFF )
            || token.equals( PC.LESSER ) || token.equals( PC.GREATER )
            || token.equals( PC.LESSER_EQUAL ) || token.equals( PC.GREATER_EQUAL )
            || token.equals( PC.PLUS ) || token.equals( PC.MINUS )
            || token.equals( PC.MULT ) || token.equals( PC.DIV )
            || token.equals( PC.MOD ) ) {
        } else syntaxError( PC.PLUS, PC.MINUS, PC.MULT, PC.DIV, PC.MOD, PC.ASIG, PC.LESSER,
            PC.GREATER, PC.LESSER_EQUAL, PC.GREATER_EQUAL, PC.EQUALS, PC.AND, PC.OR,
            PC.DIFF, PC.SEMI_COLON, PC.DOT, PC.RIGHT_PAR );
    }
    
    private void VariableFirstAssignment() {
        if ( token.equals( PC.DO ) || token.equals( PC.ASIG ) ) {
            StructMember();
            match( PC.ASIG );
            Expression();
            NextVariableFirstAssignment();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.COMMA ) ) {
            NextVariableFirstAssignment();
        } else syntaxError( PC.ASIG, PC.SEMI_COLON, PC.COMMA, PC.DO );
    }
    
    private void NextVariableFirstAssignment() {
        if ( token.equals( PC.COMMA ) ) {
            match( PC.COMMA );
            match( PC.ID );
            VariableFirstAssignment();
        } else if ( token.equals( PC.SEMI_COLON ) ) {
            match( PC.SEMI_COLON );
        } else syntaxError( PC.SEMI_COLON, PC.COMMA );
    }
    
    private void StructDeclaration() {
        if ( token.equals( PC.STRUCT ) ) {
            match( PC.STRUCT );
            match( PC.ID );
            StructVariableDeclaration();
            match( PC.STRUCT_END );
        } else syntaxError( PC.STRUCT );
    }
    
    private void StructVariableDeclaration() {
        if ( token.equals( PC.ID ) ) {
            match( PC.ID );
            match( PC.ID );
            VariableFirstAssignment();
            NextVariableDeclaration();
        } else if ( token.equals( PC.BOOL ) || token.equals( PC.CHAR ) || token.equals( PC.INT )
            || token.equals( PC.REAL ) || token.equals( PC.STRING ) ) {
            VariableDeclaration();
            NextVariableDeclaration();
        } else if ( token.equals( PC.STRUCT_END ) ) {
        } else syntaxError( PC.ID, PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING, PC.STRUCT_END );
    }
    
    private void NextVariableDeclaration() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR ) || token.equals( PC.INT )
            || token.equals( PC.REAL ) || token.equals( PC.STRING ) ) {
            StructVariableDeclaration();
        } else if ( token.equals( PC.STRUCT_END ) ) {
        } else syntaxError( PC.ID, PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING, PC.STRUCT_END );
    }
    
    private void FunctionDeclaration() {
        if ( token.equals( PC.FUNCT ) ) {
            match( PC.FUNCT );
            DataType();
            match( PC.ID );
            match( PC.LEFT_PAR );
            Parameter();
            match( PC.RIGHT_PAR );
            match( PC.DO );
            FunctionBody();
        } else syntaxError( PC.FUNCT );
    }
    
    private void FunctionBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            FunctionBody();
        } else if ( token.equals( PC.RETURN ) ) {
            match( PC.RETURN );
            Expression();
            match( PC.SEMI_COLON );
            match( PC.FUNCT_END );
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT,
            PC.REAL, PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.BREAK, PC.RETURN );
    }
    
    private void Parameter() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING ) ) {
            DataType();
            match( PC.ID );
            NextParameter();
        } else if ( token.equals( PC.RIGHT_PAR ) ) {
        } else syntaxError( PC.RIGHT_PAR, PC.ID, PC.BOOL, PC.CHAR, PC.INT, PC.REAL, PC.STRING );
    }
    
    private void NextParameter() {
        if ( token.equals( PC.COMMA ) ) {
            match( PC.COMMA );
            Parameter();
        } else if ( token.equals( PC.RIGHT_PAR ) ) {
        } else syntaxError( PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void Read() {
        if ( token.equals( PC.READ ) ) {
            match( PC.READ );
            match( PC.LEFT_PAR );
            match( PC.ID );
            FollowRead();
        } else syntaxError( PC.READ );
    }
    
    private void FollowRead() {
        if ( token.equals( PC.DOT ) ) {
            match( PC.DOT );
            match( PC.ID );
            FollowRead();
        } else if ( token.equals( PC.RIGHT_PAR ) ) {
            match( PC.RIGHT_PAR );
            match( PC.SEMI_COLON );
        } else syntaxError( PC.DOT, PC.RIGHT_PAR );
    }
    
    private void Print() {
        if ( token.equals( PC.PRINT ) ) {
            match( PC.PRINT );
            match( PC.LEFT_PAR );
            FirstExpression();
            match( PC.RIGHT_PAR );
            match( PC.SEMI_COLON );
        } else syntaxError( PC.PRINT );
    }
    
    private void FirstExpression() {
        if ( token.equals( PC.ID ) || token.equals( PC.LEFT_PAR ) || token.equals( PC.INT_VAL )
            || token.equals( PC.REAL_VAL ) || token.equals( PC.STRING_VAL )
            || token.equals( PC.CHAR_VAL ) || token.equals( PC.MINUS ) || token.equals( PC.TRUE )
            || token.equals( PC.FALSE ) || token.equals( PC.NEG ) ) {
            Expression();
            NextExpression();
        } else syntaxError( PC.MINUS, PC.NEG, PC.LEFT_PAR, PC.ID, PC.INT_VAL, PC.REAL_VAL,
            PC.CHAR_VAL, PC.STRING_VAL, PC.FALSE, PC.TRUE );
    }
    
    private void NextExpression() {
        if ( token.equals( PC.COMMA ) ) {
            match( PC.COMMA );
            FirstExpression();
        } else if ( token.equals( PC.RIGHT_PAR ) ) {
        } else syntaxError( PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void Conditional() {
        if ( token.equals( PC.IF ) ) {
            match( PC.IF );
            FlagExpression();
            match( PC.THEN );
            ConditionalBody();
        } else syntaxError( PC.IF );
    }
    
    private void FlagExpression() {
        if ( token.equals( PC.LEFT_PAR ) ) {
            match( PC.LEFT_PAR );
            Expression();
            match( PC.RIGHT_PAR );
        } else syntaxError( PC.LEFT_PAR );
    }
    
    private void ConditionalBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            ConditionalBody();
        } else if ( token.equals( PC.ELSE ) || token.equals( PC.IF_END ) ) {
            RemainingConditional();
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT, PC.REAL,
            PC.STRING, PC.IF, PC.IF_END, PC.ELSE, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.BREAK );
    }
    
    private void RemainingConditional() {
        if ( token.equals( PC.ELSE ) ) {
            match( PC.ELSE );
            ElseConditionalBody();
        } else if ( token.equals( PC.IF_END ) ) {
            match( PC.IF_END );
        } else syntaxError( PC.IF_END, PC.ELSE );
    }
    
    private void ElseConditionalBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            ElseConditionalBody();
        } else if ( token.equals( PC.IF_END ) ) {
            match( PC.IF_END );
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT, PC.REAL,
            PC.STRING, PC.IF, PC.IF_END, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.BREAK );
    }
    
    private void While() {
        if ( token.equals( PC.WHILE ) ) {
            match( PC.WHILE );
            FlagExpression();
            match( PC.DO );
            WhileBody();
        } else syntaxError( PC.WHILE );
    }
    
    private void WhileBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            WhileBody();
        } else if ( token.equals( PC.WHILE_END ) ) {
            match( PC.WHILE_END );
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT, PC.REAL,
            PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.WHILE_END, PC.FOR, PC.SELECT, PC.BREAK );
    }
    
    private void DoWhile() {
        if ( token.equals( PC.DO ) ) {
            match( PC.DO );
            DoWhileBody();
        } else syntaxError( PC.DO );
    }
    
    private void DoWhileBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.FOR ) || token.equals( PC.BREAK )
            || token.equals( PC.SELECT ) ) {
            DoWhileBodyFeature();
            DoWhileBody();
        } else if ( token.equals( PC.WHILE ) ) {
            match( PC.WHILE );
            FlagExpression();
            DetermineWhile();
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT, PC.REAL,
            PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.BREAK );
    }
    
    private void DetermineWhile() {
        if ( token.equals( PC.DO ) ) {
            match( PC.DO );
            WhileBody();
            DoWhileBody();
        } else if ( token.equals( PC.SEMI_COLON ) ) {
            match( PC.SEMI_COLON );
        } else syntaxError( PC.SEMI_COLON );
    }
    
    private void DoWhileBodyFeature() {
        if ( token.equals( PC.BOOL ) || token.equals( PC.CHAR ) || token.equals( PC.INT )
            || token.equals( PC.REAL ) || token.equals( PC.STRING ) ) {
            VariableDeclaration();
        } else if ( token.equals( PC.ID ) ) {
            match( PC.ID );
            Aux();
        } else if ( token.equals( PC.FOR ) ) {
            For();
        } else if ( token.equals( PC.SELECT ) ) {
            MultSelection();
        } else if ( token.equals( PC.IF ) ) {
            Conditional();
        } else if ( token.equals( PC.DO ) ) {
            DoWhile();
        } else if ( token.equals( PC.READ ) ) {
            Read();
        } else if ( token.equals( PC.PRINT ) ) {
            Print();
        } else if ( token.equals( PC.BREAK ) ) {
            match( PC.BREAK );
            match( PC.SEMI_COLON );
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT,
            PC.REAL, PC.STRING, PC.IF, PC.DO, PC.FOR, PC.SELECT, PC.BREAK );
    }
    
    private void For() {
        if ( token.equals( PC.FOR ) ) {
            match( PC.FOR );
            ForParams();
            match( PC.DO );
            ForBody();
        } else syntaxError( PC.FOR );
    }
    
    private void ForBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            ForBody();
        } else if ( token.equals( PC.FOR_END ) ) {
            match( PC.FOR_END );
        } else syntaxError( PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT, PC.REAL,
            PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.FOR, PC.FOR_END, PC.SELECT, PC.BREAK );
    }
    
    private void ForParams() {
        if ( token.equals( PC.LEFT_PAR ) ) {
            match( PC.LEFT_PAR );
            ForVariable();
            Expression();
            match( PC.SEMI_COLON );
            ForStep();
            match( PC.RIGHT_PAR );
        } else syntaxError( PC.LEFT_PAR );
    }
    
    private void ForVariable() {
        if ( token.equals( PC.INT ) ) {
            match( PC.INT );
            match( PC.ID );
            VariableFirstAssignment();
        } else if ( token.equals( PC.ID ) ) {
            match( PC.ID );
            VariableFirstAssignment();
        } else syntaxError( PC.ID, PC.INT );
    }
    
    private void ForStep() {
        if ( token.equals( PC.ID ) ) {
            match( PC.ID );
        } else if ( token.equals( PC.INT_VAL ) ) {
            match( PC.INT_VAL );
        } else syntaxError( PC.ID, PC.INT_VAL );
    }
    
    private void MultSelection() {
        if ( token.equals( PC.SELECT ) ) {
            match( PC.SELECT );
            match( PC.LEFT_PAR );
            Expression();
            match( PC.RIGHT_PAR );
            match( PC.BETWEEN );
            Case();
        } else syntaxError( PC.SELECT );
    }
    
    private void Case() {
        if ( token.equals( PC.CASE ) ) {
            match( PC.CASE );
            Value();
            match( PC.COLON );
            CaseBody();
        } else if ( token.equals( PC.DEFAULT ) ) {
            match( PC.DEFAULT );
            match( PC.COLON );
            DefaultCaseBody();
        } else syntaxError( PC.CASE, PC.DEFAULT );
    }
    
    private void CaseBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            CaseBody();
        } else if ( token.equals( PC.CASE ) || token.equals( PC.DEFAULT ) ) {
            Case();
        } else if ( token.equals( PC.SELECT_END ) ) {
            match( PC.SELECT_END );
        } else syntaxError( PC.SELECT_END, PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT, PC.REAL,
            PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.CASE, PC.DEFAULT, PC.BREAK,
            PC.SELECT_END );
    }
    
    private void DefaultCaseBody() {
        if ( token.equals( PC.ID ) || token.equals( PC.BOOL ) || token.equals( PC.CHAR )
            || token.equals( PC.INT ) || token.equals( PC.REAL ) || token.equals( PC.STRING )
            || token.equals( PC.DO ) || token.equals( PC.READ ) || token.equals( PC.PRINT )
            || token.equals( PC.IF ) || token.equals( PC.WHILE ) || token.equals( PC.FOR )
            || token.equals( PC.SELECT ) || token.equals( PC.BREAK ) ) {
            BodyFeature();
            DefaultCaseBody();
        } else if ( token.equals( PC.SELECT_END ) ) {
            match( PC.SELECT_END );
        } else syntaxError( PC.SELECT_END, PC.ID, PC.READ, PC.PRINT, PC.BOOL, PC.CHAR, PC.INT, PC.REAL,
            PC.STRING, PC.IF, PC.WHILE, PC.DO, PC.FOR, PC.SELECT, PC.BREAK, PC.SELECT_END );
    }
    
    private void FunctionCall() {
        if ( token.equals( PC.LEFT_PAR ) ) {
            match( PC.LEFT_PAR );
            FunctionCallParameter();
            match( PC.RIGHT_PAR );
        } else syntaxError( PC.LEFT_PAR );
    }
    
    private void FunctionCallParameter() {
        if ( token.equals( PC.ID ) || token.equals( PC.LEFT_PAR ) || token.equals( PC.INT_VAL )
            || token.equals( PC.REAL_VAL ) || token.equals( PC.STRING_VAL ) || token.equals( PC.CHAR_VAL )
            || token.equals( PC.MINUS ) || token.equals( PC.TRUE ) || token.equals( PC.FALSE )
            || token.equals( PC.NEG ) ) {
            Expression();
            NextFunctionCallParameter();
        } else if ( token.equals( PC.COMMA ) || token.equals( PC.RIGHT_PAR ) ) {
            NextFunctionCallParameter();
        } else syntaxError( PC.MINUS, PC.NEG, PC.COMMA, PC.LEFT_PAR, PC.RIGHT_PAR, PC.ID, PC.INT_VAL,
            PC.REAL_VAL, PC.CHAR_VAL, PC.STRING_VAL, PC.FALSE, PC.TRUE );
    }
    
    private void NextFunctionCallParameter() {
        if ( token.equals( PC.COMMA ) ) {
            match( PC.COMMA );
            FunctionCallParameter();
        } else if ( token.equals( PC.RIGHT_PAR ) ) {
        } else syntaxError( PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void Value() {
        if ( token.equals( PC.ID ) ) {
            match( PC.ID );
        } else if ( token.equals( PC.INT_VAL ) ) {
            match( PC.INT_VAL );
        } else if ( token.equals( PC.REAL_VAL ) ) {
            match( PC.REAL_VAL );
        } else if ( token.equals( PC.STRING_VAL ) ) {
            match( PC.STRING_VAL );
        } else if ( token.equals( PC.CHAR_VAL ) ) {
            match( PC.CHAR_VAL );
        } else if ( token.equals( PC.FALSE ) ) {
            match( PC.FALSE );
        } else if ( token.equals( PC.TRUE ) ) {
            match( PC.TRUE );
        } else syntaxError( PC.ID, PC.INT_VAL, PC.REAL_VAL, PC.CHAR_VAL, PC.STRING_VAL, PC.FALSE, PC.TRUE );
    }
    
    private void Expression() {
        if ( token.equals( PC.ID ) || token.equals( PC.LEFT_PAR ) || token.equals( PC.INT_VAL )
            || token.equals( PC.REAL_VAL ) || token.equals( PC.STRING_VAL )
            || token.equals( PC.CHAR_VAL ) || token.equals( PC.MINUS ) || token.equals( PC.TRUE )
            || token.equals( PC.FALSE ) || token.equals( PC.NEG ) ) {
            Factor();
            MultExpression();
            AditionExpression();
            ComparisonExpression();
            EqualityExpression();
            BooleanAndExpression();
            BooleanOrExpression();
        } else syntaxError( PC.MINUS, PC.NEG, PC.LEFT_PAR, PC.ID, PC.INT_VAL, PC.REAL_VAL,
            PC.CHAR_VAL, PC.STRING_VAL, PC.FALSE, PC.TRUE );
    }
    
    private void BooleanOrExpression() {
        if ( token.equals( PC.OR ) ) {
            match( PC.OR );
            Expression();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.COMMA ) ||
            token.equals( PC.RIGHT_PAR ) ) {
        } else syntaxError( PC.OR, PC.SEMI_COLON, PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void BooleanAndExpression() {
        if ( token.equals( PC.AND ) ) {
            match( PC.AND );
            Factor();
            MultExpression();
            AditionExpression();
            ComparisonExpression();
            EqualityExpression();
            BooleanAndExpression();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.COMMA ) ||
            token.equals( PC.RIGHT_PAR ) || token.equals( PC.OR ) ) {
        } else syntaxError( PC.AND, PC.OR, PC.SEMI_COLON, PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void EqualityExpression() {
        if ( token.equals( PC.EQUALS ) || token.equals( PC.DIFF ) ) {
            EqualityOperator();
            Factor();
            MultExpression();
            AditionExpression();
            ComparisonExpression();
            EqualityExpression();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.COMMA ) ||
            token.equals( PC.RIGHT_PAR ) || token.equals( PC.OR ) || token.equals( PC.AND ) ) {
        } else syntaxError( PC.EQUALS, PC.AND, PC.OR, PC.DIFF, PC.SEMI_COLON, PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void ComparisonExpression() {
        if ( token.equals( PC.LESSER )
            || token.equals( PC.GREATER ) || token.equals( PC.LESSER_EQUAL )
            || token.equals( PC.GREATER_EQUAL ) ) {
            ComparisonOperator();
            Factor();
            MultExpression();
            AditionExpression();
            ComparisonExpression();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.COMMA ) ||
            token.equals( PC.RIGHT_PAR ) || token.equals( PC.OR ) || token.equals( PC.AND )
            || token.equals( PC.EQUALS ) || token.equals( PC.DIFF ) ) {
        } else syntaxError( PC.LESSER, PC.GREATER, PC.LESSER_EQUAL, PC.GREATER_EQUAL,
            PC.EQUALS, PC.AND, PC.OR, PC.DIFF, PC.SEMI_COLON, PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void AditionExpression() {
        if ( token.equals( PC.PLUS ) || token.equals( PC.MINUS ) ) {
            AditionOperator();
            Factor();
            MultExpression();
            AditionExpression();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.COMMA ) ||
            token.equals( PC.RIGHT_PAR ) || token.equals( PC.OR ) || token.equals( PC.AND )
            || token.equals( PC.EQUALS ) || token.equals( PC.DIFF ) || token.equals( PC.LESSER )
            || token.equals( PC.GREATER ) || token.equals( PC.LESSER_EQUAL )
            || token.equals( PC.GREATER_EQUAL ) ) {
        } else syntaxError( PC.PLUS, PC.MINUS, PC.LESSER,
            PC.GREATER, PC.LESSER_EQUAL, PC.GREATER_EQUAL, PC.EQUALS, PC.AND, PC.OR,
            PC.DIFF, PC.SEMI_COLON, PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void MultExpression() {
        if ( token.equals( PC.MULT ) || token.equals( PC.DIV ) || token.equals( PC.MOD ) ) {
            MultOperator();
            Factor();
            MultExpression();
        } else if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.COMMA ) ||
            token.equals( PC.RIGHT_PAR ) || token.equals( PC.OR ) || token.equals( PC.AND )
            || token.equals( PC.EQUALS ) || token.equals( PC.DIFF ) || token.equals( PC.LESSER )
            || token.equals( PC.GREATER ) || token.equals( PC.LESSER_EQUAL )
            || token.equals( PC.GREATER_EQUAL ) || token.equals( PC.PLUS ) || token.equals( PC.MINUS ) ) {
        } else syntaxError( PC.PLUS, PC.MINUS, PC.MULT, PC.DIV, PC.MOD, PC.LESSER,
            PC.GREATER, PC.LESSER_EQUAL, PC.GREATER_EQUAL, PC.EQUALS, PC.AND, PC.OR,
            PC.DIFF, PC.SEMI_COLON, PC.COMMA, PC.RIGHT_PAR );
    }
    
    private void EqualityOperator() {
        if ( token.equals( PC.EQUALS ) ) {
            match( PC.EQUALS );
        } else if ( token.equals( PC.DIFF ) ) {
            match( PC.DIFF );
        } else syntaxError();
    }
    
    private void ComparisonOperator() {
        if ( token.equals( PC.LESSER ) ) {
            match( PC.LESSER );
        } else if ( token.equals( PC.GREATER ) ) {
            match( PC.GREATER );
        } else if ( token.equals( PC.LESSER_EQUAL ) ) {
            match( PC.LESSER_EQUAL );
        } else if ( token.equals( PC.GREATER_EQUAL ) ) {
            match( PC.GREATER_EQUAL );
        } else syntaxError( PC.LESSER, PC.GREATER, PC.LESSER_EQUAL, PC.GREATER_EQUAL );
    }
    
    private void AditionOperator() {
        if ( token.equals( PC.PLUS ) ) {
            match( PC.PLUS );
        } else if ( token.equals( PC.MINUS ) ) {
            match( PC.MINUS );
        } else syntaxError( PC.PLUS, PC.MINUS );
    }
    
    private void MultOperator() {
        if ( token.equals( PC.MULT ) ) {
            match( PC.MULT );
        } else if ( token.equals( PC.DIV ) ) {
            match( PC.DIV );
        } else if ( token.equals( PC.MOD ) ) {
            match( PC.MOD );
        } else syntaxError( PC.MULT, PC.DIV, PC.MOD );
    }
    
    private void Factor() {
        if ( token.equals( PC.TRUE ) ) {
            match( PC.TRUE );
        } else if ( token.equals( PC.FALSE ) ) {
            match( PC.FALSE );
        } else if ( token.equals( PC.MINUS ) ) {
            match( PC.MINUS );
            Number();
        } else if ( token.equals( PC.INT_VAL ) || token.equals( PC.REAL_VAL )
            || token.equals( PC.LEFT_PAR ) || token.equals( PC.ID ) ) {
            Number();
        } else if ( token.equals( PC.CHAR_VAL ) ) {
            match( PC.CHAR_VAL );
        } else if ( token.equals( PC.STRING_VAL ) ) {
            match( PC.STRING_VAL );
        } else if ( token.equals( PC.NEG ) ) {
            match( PC.NEG );
            Factor();
        } else syntaxError( PC.MINUS, PC.NEG, PC.CHAR_VAL, PC.STRING_VAL, PC.FALSE, PC.TRUE );
    }
    
    private void Number() {
        if ( token.equals( PC.INT_VAL ) ) {
            match( PC.INT_VAL );
        } else if ( token.equals( PC.REAL_VAL ) ) {
            match( PC.REAL_VAL );
        } else if ( token.equals( PC.LEFT_PAR ) ) {
            match( PC.LEFT_PAR );
            Expression();
            match( PC.RIGHT_PAR );
        } else if ( token.equals( PC.ID ) ) {
            match( PC.ID );
            Identifier();
        } else syntaxError( PC.ID, PC.LEFT_PAR, PC.INT_VAL, PC.REAL_VAL );
    }
    
    private void Identifier() {
        if ( token.equals( PC.SEMI_COLON ) || token.equals( PC.DOT ) || token.equals( PC.COMMA )
            || token.equals( PC.RIGHT_PAR ) || token.equals( PC.OR ) || token.equals( PC.AND )
            || token.equals( PC.EQUALS ) || token.equals( PC.DIFF ) || token.equals( PC.LESSER )
            || token.equals( PC.GREATER ) || token.equals( PC.LESSER_EQUAL )
            || token.equals( PC.GREATER_EQUAL ) || token.equals( PC.PLUS ) || token.equals( PC.MINUS )
            || token.equals( PC.MULT ) || token.equals( PC.DIV ) || token.equals( PC.MOD ) ) {
            StructMember();
        } else if ( token.equals( PC.LEFT_PAR ) ) {
            FunctionCall();
        } else syntaxError( PC.PLUS, PC.MINUS, PC.MULT, PC.DIV, PC.MOD, PC.ASIG, PC.LESSER,
            PC.GREATER, PC.LESSER_EQUAL, PC.GREATER_EQUAL, PC.EQUALS, PC.AND, PC.OR,
            PC.DIFF, PC.SEMI_COLON, PC.COMMA, PC.DOT, PC.LEFT_PAR, PC.RIGHT_PAR );
    }
}

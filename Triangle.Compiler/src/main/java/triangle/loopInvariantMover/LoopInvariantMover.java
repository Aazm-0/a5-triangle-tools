package triangle.loopInvariantMover;

import triangle.StdEnvironment;
import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.abstractSyntaxTrees.Program;
import triangle.abstractSyntaxTrees.actuals.ConstActualParameter;
import triangle.abstractSyntaxTrees.actuals.EmptyActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.FuncActualParameter;
import triangle.abstractSyntaxTrees.actuals.MultipleActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.ProcActualParameter;
import triangle.abstractSyntaxTrees.actuals.SingleActualParameterSequence;
import triangle.abstractSyntaxTrees.actuals.VarActualParameter;
import triangle.abstractSyntaxTrees.aggregates.MultipleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.MultipleRecordAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleArrayAggregate;
import triangle.abstractSyntaxTrees.aggregates.SingleRecordAggregate;
import triangle.abstractSyntaxTrees.commands.*;
import triangle.abstractSyntaxTrees.declarations.*;
import triangle.abstractSyntaxTrees.expressions.ArrayExpression;
import triangle.abstractSyntaxTrees.expressions.BinaryExpression;
import triangle.abstractSyntaxTrees.expressions.CallExpression;
import triangle.abstractSyntaxTrees.expressions.CharacterExpression;
import triangle.abstractSyntaxTrees.expressions.EmptyExpression;
import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.expressions.IfExpression;
import triangle.abstractSyntaxTrees.expressions.IntegerExpression;
import triangle.abstractSyntaxTrees.expressions.LetExpression;
import triangle.abstractSyntaxTrees.expressions.RecordExpression;
import triangle.abstractSyntaxTrees.expressions.UnaryExpression;
import triangle.abstractSyntaxTrees.expressions.VnameExpression;
import triangle.abstractSyntaxTrees.formals.ConstFormalParameter;
import triangle.abstractSyntaxTrees.formals.EmptyFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.FuncFormalParameter;
import triangle.abstractSyntaxTrees.formals.MultipleFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.ProcFormalParameter;
import triangle.abstractSyntaxTrees.formals.SingleFormalParameterSequence;
import triangle.abstractSyntaxTrees.formals.VarFormalParameter;
import triangle.abstractSyntaxTrees.terminals.CharacterLiteral;
import triangle.abstractSyntaxTrees.terminals.Identifier;
import triangle.abstractSyntaxTrees.terminals.IntegerLiteral;
import triangle.abstractSyntaxTrees.terminals.Operator;
import triangle.abstractSyntaxTrees.types.AnyTypeDenoter;
import triangle.abstractSyntaxTrees.types.ArrayTypeDenoter;
import triangle.abstractSyntaxTrees.types.BoolTypeDenoter;
import triangle.abstractSyntaxTrees.types.CharTypeDenoter;
import triangle.abstractSyntaxTrees.types.ErrorTypeDenoter;
import triangle.abstractSyntaxTrees.types.IntTypeDenoter;
import triangle.abstractSyntaxTrees.types.MultipleFieldTypeDenoter;
import triangle.abstractSyntaxTrees.types.RecordTypeDenoter;
import triangle.abstractSyntaxTrees.types.SimpleTypeDenoter;
import triangle.abstractSyntaxTrees.types.SingleFieldTypeDenoter;
import triangle.abstractSyntaxTrees.types.TypeDeclaration;
import triangle.abstractSyntaxTrees.visitors.ActualParameterSequenceVisitor;
import triangle.abstractSyntaxTrees.visitors.ActualParameterVisitor;
import triangle.abstractSyntaxTrees.visitors.ArrayAggregateVisitor;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.abstractSyntaxTrees.visitors.DeclarationVisitor;
import triangle.abstractSyntaxTrees.visitors.ExpressionVisitor;
import triangle.abstractSyntaxTrees.visitors.FormalParameterSequenceVisitor;
import triangle.abstractSyntaxTrees.visitors.IdentifierVisitor;
import triangle.abstractSyntaxTrees.visitors.LiteralVisitor;
import triangle.abstractSyntaxTrees.visitors.OperatorVisitor;
import triangle.abstractSyntaxTrees.visitors.ProgramVisitor;
import triangle.abstractSyntaxTrees.visitors.RecordAggregateVisitor;
import triangle.abstractSyntaxTrees.visitors.TypeDenoterVisitor;
import triangle.abstractSyntaxTrees.visitors.VnameVisitor;
import triangle.abstractSyntaxTrees.vnames.DotVname;
import triangle.abstractSyntaxTrees.vnames.SimpleVname;
import triangle.abstractSyntaxTrees.vnames.SubscriptVname;
import triangle.abstractSyntaxTrees.vnames.Vname;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoopInvariantMover implements ActualParameterVisitor<Void, AbstractSyntaxTree>,
        ActualParameterSequenceVisitor<Void, AbstractSyntaxTree>, ArrayAggregateVisitor<Void, AbstractSyntaxTree>,
        CommandVisitor<Void, AbstractSyntaxTree>, DeclarationVisitor<Void, AbstractSyntaxTree>,
        ExpressionVisitor<Void, AbstractSyntaxTree>, FormalParameterSequenceVisitor<Void, AbstractSyntaxTree>,
        IdentifierVisitor<Void, AbstractSyntaxTree>, LiteralVisitor<Void, AbstractSyntaxTree>,
        OperatorVisitor<Void, AbstractSyntaxTree>, ProgramVisitor<Void, AbstractSyntaxTree>,
        RecordAggregateVisitor<Void, AbstractSyntaxTree>, TypeDenoterVisitor<Void, AbstractSyntaxTree>,
        VnameVisitor<Void, AbstractSyntaxTree>{

    private Set<Identifier> loopVariables;

    public  LoopInvariantMover(){
        this.loopVariables = new HashSet<Identifier>();
    }

    // Takes variables that have been assiogned to in the body
    // I understand there could be other modifications like if-commands let-coomands
    // but working for while-hoist.tri example so just sequential and assigment command needed
    public Set<Identifier> extractModifiedVariables(Command cmd) {
        Set<Identifier> vars = new HashSet<>();
        if (cmd instanceof AssignCommand) {
            AssignCommand assignCmd = (AssignCommand) cmd;
            vars.addAll(extractVnameIdenifiers(assignCmd.V));
        } else if (cmd instanceof SequentialCommand) {
            SequentialCommand sequentialCmd = (SequentialCommand) cmd;
            vars.addAll(extractModifiedVariables(sequentialCmd.C1));
            vars.addAll(extractModifiedVariables(sequentialCmd.C2));
        }
        return vars;
    }


    // Again i undersstand the expression could be unary if call etc but in our ex just a binary then a vname expression
    public Set<Identifier> extractReadVariables(Expression exp){
        Set<Identifier> vars = new HashSet<>();

        if (exp instanceof VnameExpression){
            VnameExpression vnameExp = (VnameExpression) exp;
            vars.addAll(extractVnameIdenifiers(vnameExp.V));
        } else if (exp instanceof  BinaryExpression) {
            BinaryExpression binaryExp = (BinaryExpression) exp;
            vars.addAll(extractReadVariables(binaryExp.E1));
            vars.addAll(extractReadVariables(binaryExp.E2));
        }
        return  vars;
    }

    // Again i understand assigment can happen on arrays and record types too but for this scenario its a simpleVname
    public  Set<Identifier> extractVnameIdenifiers(Vname vname){
        Set<Identifier> vars = new HashSet<>();
        if (vname instanceof SimpleVname){
            SimpleVname simpleVname = (SimpleVname) vname;
            vars.add(simpleVname.I);
        }
        return vars;
    }

    // Now to seperate the variant and invariant first ill have to go over sequence command depper deeper into the tree
    // until i find the commands which are for assigment which will be checked using isInvariant command
    public  void  seperateInvariants(
            SequentialCommand body,
            List<Command> invariant,
            List<Command>  variant
    ){
        // Left node of sequential command
        if (body.C1 instanceof SequentialCommand){
            seperateInvariants((SequentialCommand) body.C1,invariant,variant);
        } else {
            if (isInvariantCommand(body.C1)) {
                invariant.add(body.C1);
            }
            else  {
                variant.add(body.C1);
            }
        }

        // Right node of sequential command
        if (body.C2 instanceof SequentialCommand){
            seperateInvariants((SequentialCommand) body.C2,invariant,variant);
        } else {
            if (isInvariantCommand(body.C2)) {
                invariant.add(body.C2);
            }
            else  {
                variant.add(body.C2);
            }
        }
    }

    // This has first check for assigment
    // then two double checks
    //  1: Ff the variable being assigned two exists in loopVariables
    //  2: If the variable being used in the assigment is a loopVariable
    public Boolean isInvariantCommand(Command cmd){
        if(!(cmd instanceof AssignCommand)){
            return false;
        }
        // Get all assigned too variables and check if they are in loopVariables
        AssignCommand assignCmd = (AssignCommand) cmd;
        Set<Identifier> readVars = extractReadVariables(assignCmd.E);
        Set<Identifier> writeVars = extractVnameIdenifiers(assignCmd.V);


        for(Identifier v: readVars){
            for (Identifier loopVar: loopVariables){
                if(v.spelling.equals(loopVar.spelling)){
                    return false;
                }
            }
        }

        for(Identifier v: writeVars){
            for (Identifier loopVar: loopVariables){
                if(v.spelling.equals(loopVar.spelling)){
                    return false;
                }
            }
        }
        return true;
    }

    public Command reconstructCommands(List<Command> commands) {
        if (commands.isEmpty()) {
            return new EmptyCommand(null);
        }
        Command result = commands.get(0);
        for (int i = 1; i < commands.size(); i++) {
            result = new SequentialCommand(result, commands.get(i), result.getPosition());
        }
        return result;
    }

    @Override
    public AbstractSyntaxTree visitWhileCommand(WhileCommand ast, Void arg) {
        //System.out.println("=== VISITING WHILE COMMAND ===");
        // Get the modified variables in the body
        Set<Identifier> modifiedVariables = extractModifiedVariables(ast.C);

        // Variables used in loop condition
        Set<Identifier> conditionVariables = extractReadVariables(ast.E);

        // Take the common from both to get the variables dependent on loop thus non-invariant
        this.loopVariables = new HashSet<>();
        for(Identifier modified : modifiedVariables){
            for(Identifier condition : conditionVariables){
                if(modified.spelling.equals(condition.spelling)){
                    this.loopVariables.add(modified);
                }
            }
        }

        // Now here i understand there could be race condition or not
        // if there is a singualr assigment command so no sequential block my seperation of invariants methopd wouldnt work
        // fix would be to add an empty command and transform even singular expresssion to sequence ones or maybe the compuiiler config already does that and i didnt read it

        List<Command> invariantCommands = new ArrayList<>();
        List<Command> variantCommands = new ArrayList<>();

        seperateInvariants((SequentialCommand) ast.C,invariantCommands,variantCommands);

        if (!invariantCommands.isEmpty()){
            //System.out.println("=== INVARIANT ===");
            // loop recreated with thew hoisted commands removed
            Command newBodyReconstructed = reconstructCommands(variantCommands);
            WhileCommand optimizedLoop = new WhileCommand(ast.E,newBodyReconstructed,ast.getPosition());

            //Reconstruct the hoisted commands
            Command hoistedCommands = reconstructCommands(invariantCommands);

            // place it in an if statement so it preserves semantics with empty else
//            IfCommand conditionalHoist = new IfCommand(
//                    ast.E,
//                    hoistedCommands,
//                    new EmptyCommand(ast.getPosition()),
//                    ast.getPosition()
//            );

            System.out.println("Hoisting " + invariantCommands.size() + " commands before loop");
            // Return it as a sequence
            return new SequentialCommand(hoistedCommands, optimizedLoop, ast.getPosition());
        }

        //System.out.println("Null returned");

        return null;
    }


    @Override
    public AbstractSyntaxTree visitConstFormalParameter(ConstFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitFuncFormalParameter(FuncFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProcFormalParameter(ProcFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.FPS.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVarFormalParameter(VarFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Void arg) {
        ast.FT.visit(this);
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitDotVname(triangle.abstractSyntaxTrees.vnames.DotVname ast, Void arg) {
        ast.I.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSimpleVname(triangle.abstractSyntaxTrees.vnames.SimpleVname ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSubscriptVname(triangle.abstractSyntaxTrees.vnames.SubscriptVname ast, Void arg) {
        ast.E.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitAnyTypeDenoter(AnyTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitArrayTypeDenoter(ArrayTypeDenoter ast, Void arg) {
        ast.IL.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitBoolTypeDenoter(BoolTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCharTypeDenoter(CharTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitErrorTypeDenoter(ErrorTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSimpleTypeDenoter(SimpleTypeDenoter ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIntTypeDenoter(IntTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitRecordTypeDenoter(RecordTypeDenoter ast, Void arg) {
        ast.FT.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleRecordAggregate(MultipleRecordAggregate ast, Void arg) {
        ast.E.visit(this);
        ast.I.visit(this);
        ast.RA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleRecordAggregate(SingleRecordAggregate ast, Void arg) {
        ast.E.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProgram(Program ast, Void arg) {
        AbstractSyntaxTree newCommand = ast.C.visit(this);
        if (newCommand != null) {
            ast.C = (Command) newCommand;
        }
        return null;
    }

    @Override
    public AbstractSyntaxTree visitOperator(Operator ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCharacterLiteral(CharacterLiteral ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIntegerLiteral(IntegerLiteral ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIdentifier(Identifier ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Void arg) {
        ast.FP.visit(this);
        ast.FPS.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Void arg) {
        ast.FP.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitArrayExpression(ArrayExpression ast, Void arg) {
        ast.AA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitBinaryExpression(BinaryExpression ast, Void arg) {
        ast.E1.visit(this);
        ast.E2.visit(this);
        ast.O.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCallExpression(CallExpression ast, Void arg) {
        ast.APS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCharacterExpression(CharacterExpression ast, Void arg) {
        ast.CL.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitEmptyExpression(EmptyExpression ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIfExpression(IfExpression ast, Void arg) {
        ast.E1.visit(this);
        ast.E2.visit(this);
        ast.E3.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIntegerExpression(IntegerExpression ast, Void arg) {
        ast.IL.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitLetExpression(LetExpression ast, Void arg) {
        ast.D.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitRecordExpression(RecordExpression ast, Void arg) {
        ast.RA.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitUnaryExpression(UnaryExpression ast, Void arg) {
        ast.E.visit(this);
        ast.O.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVnameExpression(VnameExpression ast, Void arg) {
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Void arg) {
        ast.ARG1.visit(this);
        ast.ARG2.visit(this);
        ast.O.visit(this);
        ast.RES.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitConstDeclaration(ConstDeclaration ast, Void arg) {
        ast.E.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitFuncDeclaration(FuncDeclaration ast, Void arg) {
        ast.E.visit(this);
        ast.FPS.visit(this);
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProcDeclaration(ProcDeclaration ast, Void arg) {
        ast.C.visit(this);
        ast.FPS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSequentialDeclaration(SequentialDeclaration ast, Void arg) {
        ast.D1.visit(this);
        ast.D2.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitTypeDeclaration(TypeDeclaration ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Void arg) {
        ast.ARG.visit(this);
        ast.O.visit(this);
        ast.RES.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVarDeclaration(VarDeclaration ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitAssignCommand(AssignCommand ast, Void arg) {
        ast.E.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitCallCommand(CallCommand ast, Void arg) {
        ast.APS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitEmptyCommand(EmptyCommand ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitIfCommand(IfCommand ast, Void arg) {
        ast.C1.visit(this);
        ast.C2.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitLetCommand(LetCommand ast, Void arg) {
        AbstractSyntaxTree newC = ast.C.visit(this);
        AbstractSyntaxTree newD = ast.D.visit(this);

        if (newC != null) {
            ast.C = (Command) newC;
        }
        if (newD != null) {
            ast.D = (Declaration) newD;
        }
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSequentialCommand(SequentialCommand ast, Void arg) {
        AbstractSyntaxTree newC1 = ast.C1.visit(this);
        AbstractSyntaxTree newC2 = ast.C2.visit(this);

        if (newC1 != null) {
            ast.C1 = (Command) newC1;
        }
        if (newC2 != null) {
            ast.C2 = (Command) newC2;
        }
        return null;
    }



    @Override
    public AbstractSyntaxTree visitPostFixCommand(PostFixCommand command, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitDoWhileCommand(DoWhileCommand command, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleArrayAggregate(MultipleArrayAggregate ast, Void arg) {
        ast.AA.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleArrayAggregate(SingleArrayAggregate ast, Void arg) {
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Void arg) {
        return null;
    }

    @Override
    public AbstractSyntaxTree visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Void arg) {
        ast.AP.visit(this);
        ast.APS.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitSingleActualParameterSequence(SingleActualParameterSequence ast, Void arg) {
        ast.AP.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitConstActualParameter(ConstActualParameter ast, Void arg) {
        ast.E.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitFuncActualParameter(FuncActualParameter ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitProcActualParameter(ProcActualParameter ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public AbstractSyntaxTree visitVarActualParameter(VarActualParameter ast, Void arg) {
        ast.V.visit(this);
        return null;
    }
}

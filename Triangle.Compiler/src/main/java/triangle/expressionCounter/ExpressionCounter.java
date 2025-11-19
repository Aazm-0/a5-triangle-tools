package triangle.expressionCounter;

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
import triangle.abstractSyntaxTrees.commands.AssignCommand;
import triangle.abstractSyntaxTrees.commands.CallCommand;
import triangle.abstractSyntaxTrees.commands.DoWhileCommand;
import triangle.abstractSyntaxTrees.commands.EmptyCommand;
import triangle.abstractSyntaxTrees.commands.IfCommand;
import triangle.abstractSyntaxTrees.commands.LetCommand;
import triangle.abstractSyntaxTrees.commands.PostFixCommand;
import triangle.abstractSyntaxTrees.commands.SequentialCommand;
import triangle.abstractSyntaxTrees.commands.WhileCommand;
import triangle.abstractSyntaxTrees.declarations.BinaryOperatorDeclaration;
import triangle.abstractSyntaxTrees.declarations.ConstDeclaration;
import triangle.abstractSyntaxTrees.declarations.FuncDeclaration;
import triangle.abstractSyntaxTrees.declarations.ProcDeclaration;
import triangle.abstractSyntaxTrees.declarations.SequentialDeclaration;
import triangle.abstractSyntaxTrees.declarations.UnaryOperatorDeclaration;
import triangle.abstractSyntaxTrees.declarations.VarDeclaration;
import triangle.abstractSyntaxTrees.expressions.ArrayExpression;
import triangle.abstractSyntaxTrees.expressions.BinaryExpression;
import triangle.abstractSyntaxTrees.expressions.CallExpression;
import triangle.abstractSyntaxTrees.expressions.CharacterExpression;
import triangle.abstractSyntaxTrees.expressions.EmptyExpression;
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

public class ExpressionCounter implements ActualParameterVisitor<Void, Void>,
        ActualParameterSequenceVisitor<Void, Void>, ArrayAggregateVisitor<Void, Void>,
        CommandVisitor<Void, Void>, DeclarationVisitor<Void, Void>,
        ExpressionVisitor<Void, Void>, FormalParameterSequenceVisitor<Void, Void>,
        IdentifierVisitor<Void, Void>, LiteralVisitor<Void, Void>,
        OperatorVisitor<Void, Void>, ProgramVisitor<Void, Void>,
        RecordAggregateVisitor<Void, Void>, TypeDenoterVisitor<Void, Void>,
        VnameVisitor<Void, Void> {

    private int integerExpressionCount = 0;
    private int characterExpressionCount = 0;

    public int getintegerExpressionCount() {
        return integerExpressionCount;
    }

    public int getcharacterExpressionCount() {
        return characterExpressionCount;
    }

    public void printResults() {
        String charLiterals = String.valueOf(this.getcharacterExpressionCount());
        String integerLiterals = String.valueOf(this.getintegerExpressionCount());
        System.out.println("CharExpression: " + charLiterals + " IntegerExpression: " + integerLiterals);
    }

    @Override
    public Void visitConstFormalParameter(ConstFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitFuncFormalParameter(FuncFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitProcFormalParameter(ProcFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.FPS.visit(this);
        return null;
    }

    @Override
    public Void visitVarFormalParameter(VarFormalParameter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Void arg) {
        ast.FT.visit(this);
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitDotVname(triangle.abstractSyntaxTrees.vnames.DotVname ast, Void arg) {
        ast.I.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public Void visitSimpleVname(triangle.abstractSyntaxTrees.vnames.SimpleVname ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitSubscriptVname(triangle.abstractSyntaxTrees.vnames.SubscriptVname ast, Void arg) {
        ast.E.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public Void visitAnyTypeDenoter(AnyTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public Void visitArrayTypeDenoter(ArrayTypeDenoter ast, Void arg) {
        ast.IL.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitBoolTypeDenoter(BoolTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public Void visitCharTypeDenoter(CharTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public Void visitErrorTypeDenoter(ErrorTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public Void visitSimpleTypeDenoter(SimpleTypeDenoter ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitIntTypeDenoter(IntTypeDenoter ast, Void arg) {
        return null;
    }

    @Override
    public Void visitRecordTypeDenoter(RecordTypeDenoter ast, Void arg) {
        ast.FT.visit(this);
        return null;
    }

    @Override
    public Void visitMultipleRecordAggregate(MultipleRecordAggregate ast, Void arg) {
        ast.E.visit(this);
        ast.I.visit(this);
        ast.RA.visit(this);
        return null;
    }

    @Override
    public Void visitSingleRecordAggregate(SingleRecordAggregate ast, Void arg) {
        ast.E.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitProgram(Program ast, Void arg) {
        ast.C.visit(this);
        return null;
    }

    @Override
    public Void visitOperator(Operator ast, Void arg) {
        return null;
    }

    @Override
    public Void visitCharacterLiteral(CharacterLiteral ast, Void arg) {
        return null;
    }

    @Override
    public Void visitIntegerLiteral(IntegerLiteral ast, Void arg) {
        return null;
    }

    @Override
    public Void visitIdentifier(Identifier ast, Void arg) {
        return null;
    }

    @Override
    public Void visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Void arg) {
        return null;
    }

    @Override
    public Void visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Void arg) {
        ast.FP.visit(this);
        ast.FPS.visit(this);
        return null;
    }

    @Override
    public Void visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Void arg) {
        ast.FP.visit(this);
        return null;
    }

    @Override
    public Void visitArrayExpression(ArrayExpression ast, Void arg) {
        ast.AA.visit(this);
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression ast, Void arg) {
        ast.E1.visit(this);
        ast.E2.visit(this);
        ast.O.visit(this);
        return null;
    }

    @Override
    public Void visitCallExpression(CallExpression ast, Void arg) {
        ast.APS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitCharacterExpression(CharacterExpression ast, Void arg) {
        characterExpressionCount++;
        ast.CL.visit(this);
        return null;
    }

    @Override
    public Void visitEmptyExpression(EmptyExpression ast, Void arg) {
        return null;
    }

    @Override
    public Void visitIfExpression(IfExpression ast, Void arg) {
        ast.E1.visit(this);
        ast.E2.visit(this);
        ast.E3.visit(this);
        return null;
    }

    @Override
    public Void visitIntegerExpression(IntegerExpression ast, Void arg) {
        integerExpressionCount++;
        ast.IL.visit(this);
        return null;
    }

    @Override
    public Void visitLetExpression(LetExpression ast, Void arg) {
        ast.D.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public Void visitRecordExpression(RecordExpression ast, Void arg) {
        ast.RA.visit(this);
        return null;
    }

    @Override
    public Void visitUnaryExpression(UnaryExpression ast, Void arg) {
        ast.E.visit(this);
        ast.O.visit(this);
        return null;
    }

    @Override
    public Void visitVnameExpression(VnameExpression ast, Void arg) {
        ast.V.visit(this);
        return null;
    }

    @Override
    public Void visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Void arg) {
        ast.ARG1.visit(this);
        ast.ARG2.visit(this);
        ast.O.visit(this);
        ast.RES.visit(this);
        return null;
    }

    @Override
    public Void visitConstDeclaration(ConstDeclaration ast, Void arg) {
        ast.E.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitFuncDeclaration(FuncDeclaration ast, Void arg) {
        ast.E.visit(this);
        ast.FPS.visit(this);
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitProcDeclaration(ProcDeclaration ast, Void arg) {
        ast.C.visit(this);
        ast.FPS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitSequentialDeclaration(SequentialDeclaration ast, Void arg) {
        ast.D1.visit(this);
        ast.D2.visit(this);
        return null;
    }

    @Override
    public Void visitTypeDeclaration(TypeDeclaration ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Void arg) {
        ast.ARG.visit(this);
        ast.O.visit(this);
        ast.RES.visit(this);
        return null;
    }

    @Override
    public Void visitVarDeclaration(VarDeclaration ast, Void arg) {
        ast.I.visit(this);
        ast.T.visit(this);
        return null;
    }

    @Override
    public Void visitAssignCommand(AssignCommand ast, Void arg) {
        ast.E.visit(this);
        ast.V.visit(this);
        return null;
    }

    @Override
    public Void visitCallCommand(CallCommand ast, Void arg) {
        ast.APS.visit(this);
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitEmptyCommand(EmptyCommand ast, Void arg) {
        return null;
    }

    @Override
    public Void visitIfCommand(IfCommand ast, Void arg) {
        ast.C1.visit(this);
        ast.C2.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public Void visitLetCommand(LetCommand ast, Void arg) {
        ast.C.visit(this);
        ast.D.visit(this);
        return null;
    }

    @Override
    public Void visitSequentialCommand(SequentialCommand ast, Void arg) {
        ast.C1.visit(this);
        ast.C2.visit(this);
        return null;
    }

    @Override
    public Void visitWhileCommand(WhileCommand ast, Void arg) {
        ast.C.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public Void visitPostFixCommand(PostFixCommand command, Void arg) {
        return null;
    }

    @Override
    public Void visitDoWhileCommand(DoWhileCommand command, Void arg) {
        return null;
    }

    @Override
    public Void visitMultipleArrayAggregate(MultipleArrayAggregate ast, Void arg) {
        ast.AA.visit(this);
        ast.E.visit(this);
        return null;
    }

    @Override
    public Void visitSingleArrayAggregate(SingleArrayAggregate ast, Void arg) {
        ast.E.visit(this);
        return null;
    }

    @Override
    public Void visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Void arg) {
        return null;
    }

    @Override
    public Void visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Void arg) {
        ast.AP.visit(this);
        ast.APS.visit(this);
        return null;
    }

    @Override
    public Void visitSingleActualParameterSequence(SingleActualParameterSequence ast, Void arg) {
        ast.AP.visit(this);
        return null;
    }

    @Override
    public Void visitConstActualParameter(ConstActualParameter ast, Void arg) {
        ast.E.visit(this);
        return null;
    }

    @Override
    public Void visitFuncActualParameter(FuncActualParameter ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitProcActualParameter(ProcActualParameter ast, Void arg) {
        ast.I.visit(this);
        return null;
    }

    @Override
    public Void visitVarActualParameter(VarActualParameter ast, Void arg) {
        ast.V.visit(this);
        return null;
    }

}

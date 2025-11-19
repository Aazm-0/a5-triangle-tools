package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.expressions.Expression;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.syntacticAnalyzer.SourcePosition;

public class DoWhileCommand extends Command {
    public Command C1;
    public Expression E;
    public Command C2;

public DoWhileCommand(Command c1AST, Expression eAST, Command c2AST, SourcePosition position) {
    super(position);
    this.C1 = c1AST;
    this.E = eAST;
    this.C2 = c2AST;
}


    @Override
    public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> visitor, TArg arg) {
        return visitor.visitDoWhileCommand(this,arg);
    }
}

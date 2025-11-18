package triangle.abstractSyntaxTrees.commands;

import triangle.abstractSyntaxTrees.terminals.Operator;
import triangle.abstractSyntaxTrees.visitors.CommandVisitor;
import triangle.abstractSyntaxTrees.vnames.Vname;
import triangle.syntacticAnalyzer.SourcePosition;

public class PostFixCommand extends Command{
    public final Vname v;
    public Operator postfixOp;

    public PostFixCommand(Vname v,Operator postfixOp,SourcePosition position){
        super(position);
        this.v = v;
        this.postfixOp = postfixOp;
    }

    @Override
    public <TArg, TResult> TResult visit(CommandVisitor<TArg, TResult> visitor, TArg tArg) {
        return visitor.visitPostFixCommand(this, tArg);
    }
}

package triangle.abstractSyntaxTrees.visitors;

import triangle.abstractSyntaxTrees.commands.*;

public interface CommandVisitor<TArg, TResult> {

	TResult visitAssignCommand(AssignCommand ast, TArg arg);

	TResult visitCallCommand(CallCommand ast, TArg arg);

	TResult visitEmptyCommand(EmptyCommand ast, TArg arg);

	TResult visitIfCommand(IfCommand ast, TArg arg);

	TResult visitLetCommand(LetCommand ast, TArg arg);

	TResult visitSequentialCommand(SequentialCommand ast, TArg arg);

	TResult visitWhileCommand(WhileCommand ast, TArg arg);

    TResult visitPostFixCommand(PostFixCommand command, TArg arg);

    TResult visitDoWhileCommand(DoWhileCommand command, TArg arg);

}

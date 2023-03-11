package org.iguana.grammar.slot;

import org.iguana.datadependent.env.Environment;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;
import org.iguana.utils.input.Input;

public class ErrorTransition extends AbstractTransition {

    public ErrorTransition(BodyGrammarSlot origin, BodyGrammarSlot dest) {
        super(origin, dest);
    }

    @Override
    public String getLabel() {
        throw new UnsupportedOperationException();
    }

    public <T extends Result> void handleError(
            Input input,
            GSSNode<T> u,
            T result,
            Environment env,
            IguanaRuntime<T> runtime) {
        int rightExtent = result.isDummy() ? u.getInputIndex() : result.getRightExtent();
        int i = rightExtent;
        while (i < input.length() && !dest.testFollow(input.charAt(i))) {
            i++;
        }
        if (i < input.length()) {
            T cr = runtime.getResultOps().error(this.dest, rightExtent, i);
            T n = dest.isFirst() ? cr : runtime.getResultOps().merge(null, result, cr, dest);
            dest.execute(input, u, n, env, runtime);
        } else {
            System.out.println("Warning: could not recover from the parse error: " + origin);
        }
    }

    @Override
    public <T extends Result> void execute(
            Input input,
            GSSNode<T> u,
            T result,
            Environment env,
            IguanaRuntime<T> runtime) {
        dest.execute(input, u, result, env, runtime);
    }
}

package org.iguana.grammar.slot;

import org.iguana.datadependent.env.Environment;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.regex.IguanaTokenizer;
import org.iguana.regex.Token;
import org.iguana.result.Result;
import org.iguana.utils.collections.CollectionsUtil;
import org.iguana.utils.input.Input;

import java.util.ArrayList;
import java.util.List;

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
        IguanaTokenizer tokenizer = runtime.getTokenizer();
        tokenizer.prepare(input, rightExtent);

        List<Token> errorTokens = new ArrayList<>();
        while (tokenizer.nextToken(dest.getFollowTest()) != null) {
            errorTokens.add(tokenizer.nextToken());
        }
        if (errorTokens.isEmpty()) {
            System.out.println("Warning: could not recover from the parse error: " + origin);
            return;
        }
        T cr;
        if (errorTokens.size() == 1) {
            Token token = errorTokens.get(0);
            cr = runtime.getResultOps().error(this.dest, token.getStart(), token.getEnd());
        } else {
            List<T> children = new ArrayList<>();
            for (Token errorToken : errorTokens) {
                children.add(runtime.getResultOps().base(null, errorToken.getStart(), errorToken.getEnd()));
            }
            int start = CollectionsUtil.first(errorTokens).getStart();
            int end = CollectionsUtil.last(errorTokens).getEnd();
            cr = runtime.getResultOps().error(this.dest, start, end, children);
        }
        T n = dest.isFirst() ? cr : runtime.getResultOps().merge(null, result, cr, dest);
        dest.execute(input, u, n, env, runtime);
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

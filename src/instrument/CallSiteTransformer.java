package instrument;

import instrument.analysis.BaseTransformer;
import instrument.analysis.DefUseInterpreter;
import instrument.analysis.Interpreter;

import org.objectweb.asm.tree.MethodNode;

public class CallSiteTransformer extends BaseTransformer {

    private String[] callsiteNames;

    public CallSiteTransformer(Interpreter interpreter,
            String owner, MethodNode mn) {
        super(interpreter, owner, mn);
    }

    public CallSiteTransformer(String owner, MethodNode mn, String[] callsiteNames) {
        super(owner, mn);
        this.callsiteNames = callsiteNames;
        this.interpreter = new DefUseInterpreter();
    }

    @Override
    protected void preTransform() {
        super.preTransform();
    }



}

package instrument;

import org.aspectj.weaver.tools.PointcutParser;

public class Matcher {

    protected PointcutParser parser;
    protected Class<?> target;

    public Matcher parser(PointcutParser p) {
        this.parser = p;
        return this;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

}

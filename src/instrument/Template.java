package instrument;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import sand.SelectMethod;

public class Template {

    private String klass;
    private String method;

    public Template(String klass, String method) {
        this.klass = klass;
        this.method = method;
    }

    public void test() {
        System.out.println("test");
    }

    public static void main(String[] args) {
        PointcutParser p = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution();
        PointcutExpression pe = p.parsePointcutExpression("call(* java.io.PrintStream.println(String)) && withincode(void instrument.Template.test(..))");

        for(Method m : PrintStream.class.getMethods()) {
            for(Method mm: Template.class.getMethods()) {
                ShadowMatch s = pe.matchesMethodCall(m, mm);
                if(s.maybeMatches()) System.out.println("may be matched: " + m);
            }
        }
    }

}

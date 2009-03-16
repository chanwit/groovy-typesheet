package instrument;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;
import org.aspectj.weaver.tools.TypePatternMatcher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

public class CopyOfMatcher {

    /* Example of matching
    *
    * class(*Controller) {
    *   closure(index) && call(render(Object+ s)) { s ~> String }
    * }
    *
    * แปลงเป็น, โดยหา  mapping ["index": i]
    * มาแทนที่ใน withincode
    * withincode(* *Controller._closure${i}.doCall(..)) && call(render(..)) { s ~> String}
    *
    */

    private String classPat = "*Controller";
    private String[] methodPatterns = new String[]{
        "execution(* TestController._closure1.doCall(..))"
    };
    // private String dir = "C:/grails/render/render-0.2/WEB-INF/classes";


    private PointcutParser parser;

    public void evaluate() {

    }

    public boolean matchClass(Class<?> c)  {
        TypePatternMatcher tpm = parser.parseTypePattern(classPat);
        return tpm.matches(c);
    }

    public boolean matchInnerClass(Class<?> c) {
        PointcutExpression pe = parser.parsePointcutExpression(methodPatterns[0]);
        // if(pe.matchesMethodCall("call", withinCode))
    }



    public boolean matches() throws ClassNotFoundException, IOException {
/*
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file://" + dir + "/")});
        PointcutParser p = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingSpecifiedClassloaderForResolution(classLoader);

        TypePatternMatcher tpm = p.parseTypePattern(classPat);
        PointcutExpression exp = p.parsePointcutExpression(methodPatterns[0]);

        String[] classFiles = new ClassUtils().getClasses(dir);
        for(String c: classFiles) {
            String name = c.substring(0, c.lastIndexOf('.'));
            Class<?> cls = Class.forName(name.replace('/', '.'), false, classLoader);

            if(tpm.matches(cls)) {
                Class<?>[] innerClasses = cls.getDeclaredClasses();
                for(Class<?> i: innerClasses) {
                    Method[] mi = i.getMethods();
                    for(Method mm: mi) {
                        ShadowMatch sm = exp.matchesMethodExecution(mm);
                        if(sm.maybeMatches()) {
                            System.out.println("true");
                            System.out.println(mm);
                        }
                    }

                    System.out.println(i.getName());
                    ClassReader cr = new ClassReader(i.getName());
                    TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
                    cr.accept(tcv, 0);
                }
                PointcutExpression pe = p.parsePointcutExpression(methodPat);
                pe.matchesMethodExecution();
                ClassReader cr = new ClassReader(cls.getName());
                TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
                cr.accept(tcv, 0);
            }
        }
*/

        return true;

    }

    public CopyOfMatcher parser(PointcutParser p) {
        this.parser = p;
        return this;
    }

}

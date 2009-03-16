package instrument;

import java.net.URL;
import java.net.URLClassLoader;

import org.aspectj.weaver.tools.PointcutParser;

public class Looper {

    private String dir = "C:/grails/render/render-0.2/WEB-INF/classes";
    private Matcher[] matchers = new Matcher[] {
        new Matcher()
    };

    public void loop() throws Throwable {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file://" + dir + "/")});
        PointcutParser p = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingSpecifiedClassloaderForResolution(classLoader);

        String[] classFiles = new ClassUtils().getClasses(dir);
        for(String classFile: classFiles) {
            String className = classFile.substring(0, classFile.lastIndexOf('.')).replace('/', '.');
            Class<?> cls = Class.forName(className, false, classLoader);

            for(Matcher m: matchers) {
                if(m.parser(p).matchClass(cls)) {
                    // m.match
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        new Looper().loop();
    }

}

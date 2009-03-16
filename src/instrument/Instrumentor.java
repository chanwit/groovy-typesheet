package instrument;

import java.io.File;
import java.io.RandomAccessFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class Instrumentor {

    public static void main(String[] args) throws Throwable {
        ClassReader cr = new ClassReader("org.codehaus.groovy.grails.web.metaclass.RenderDynamicMethod");
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MyClassAdapter ca = new MyClassAdapter(cw);
        cr.accept(ca, 0);
        byte[] bytes = cw.toByteArray();

        new File("RRRR.class").delete();
        RandomAccessFile f = new RandomAccessFile("RRRR.class", "rw");
        f.write(bytes);
        f.close();
    }

}

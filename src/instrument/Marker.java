package instrument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Marker {

    private static File f;
    private static PrintWriter out;
    private static Data data;

    public static void args(String className, Object[] arguments) {
        data = new Data();
        data.setClassName(className);
        String fname = className.replace('/', '_');
        f = new File(fname);
        try {
            out = new PrintWriter(f);
            out.println("class name: " + className);
            out.println("args length: " + arguments.length);
            String[] args_class_name = new String[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                out.println("args[" + i + "]: " + arguments[i].getClass().getName());
                data.setArgClasses(args_class_name);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void touch(int i) {
        out.println("check point:" + i);
        data.checkpoint(i);
    }

    public static void end() {
        out.flush();
        out.close();
        data.end();
        try {
            data.save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

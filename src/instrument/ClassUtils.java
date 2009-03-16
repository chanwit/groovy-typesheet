package instrument;

import java.io.File;
import java.util.ArrayList;

public class ClassUtils {

    private ArrayList<String> results = new ArrayList<String>();

    public String[] getClasses(String dir) {
        results.clear();
        recursiveTraversal(null, new File(dir));
        return results.toArray(new String[results.size()]);
    }

    private void recursiveTraversal(String root, File file){
        if (file.isDirectory()){
            File allFiles[] = file.listFiles();
            for(File aFile : allFiles){
                if(root == null) {
                    recursiveTraversal("", aFile);
                } else if (root.equals("")) {
                    recursiveTraversal(file.getName(), aFile);
                } else {
                    recursiveTraversal(root + "/" + file.getName(), aFile);
                }
            }
        }else if (file.isFile()){
            if (file.getName().endsWith(".class")) {
                if(root == "") {
                    add(file.getName());
                } else {
                    add(root + "/" + file.getName().substring(0, file.getName().lastIndexOf('.')));
                }
            }
        }
    }

    private void add(String filename) {
        this.results.add(filename);
    }

    public static void main(String[] args) {
        String[] r = new ClassUtils().getClasses("C:/groovy-ck1/method_handle/bin");
        for(String s : r) {
            System.out.println(s);
        }
    }

}

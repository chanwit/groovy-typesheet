package instrument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Data implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7384490589082809129L;

    private String className;
    private String[] argClasses;
    private int[] checkpoints;
    private List<Integer> points = new ArrayList<Integer>();

    public Data() {
        super();
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String[] getArgClasses() {
        return argClasses;
    }
    public void setArgClasses(String[] argClasses) {
        this.argClasses = argClasses;
    }

    public int[] getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(int[] checkpoints) {
        this.checkpoints = checkpoints;
    }

    public void checkpoint(int i) {
        points.add(i);
    }

    public void end() {
        this.checkpoints = new int[points.size()];
        int j=0;
        for(Integer i: points) {
            this.checkpoints[j++] = i;
        }
    }

    public void save() throws FileNotFoundException, IOException {
        File f = new File(this.className.replace('/', '_') + ".bin");
        ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
        o.writeObject(this);
    }

}

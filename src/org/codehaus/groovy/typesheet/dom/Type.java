package org.codehaus.groovy.typesheet.dom;

import java.util.ArrayList;

public class Type {

    private Class<?> klass;
    private ArrayList<Field> fields = new ArrayList<Field>();

    public Type(Class<?> klass) {
        super();
        this.klass = klass;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

}

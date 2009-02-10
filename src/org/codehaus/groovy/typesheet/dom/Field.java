package org.codehaus.groovy.typesheet.dom;

public class Field {

    private String name;
    private Class<?> klass;

    public Field(String name, Class<?> fieldType) {
        this.name = name;
        this.klass = fieldType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Class<?> getKlass() {
        return klass;
    }
    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

}

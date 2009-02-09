package org.codehaus.groovy.typesheet.dom;

public class Type {

    private Class<?> klass;

    public Type(Class<?> klass) {
        super();
        this.klass = klass;
    }

    public Class<?> getKlass() {
        return klass;
    }

}

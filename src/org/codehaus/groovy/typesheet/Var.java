package org.codehaus.groovy.typesheet;

public class Var {

    private String name;
    private Class<?> klass;

    public Var(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void rightShift(Object t) {
        this.klass = (Class<?>)t;
    }

    public Class<?> getKlass() {
        return klass;
    }

}

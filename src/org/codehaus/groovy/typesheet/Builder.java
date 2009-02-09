package org.codehaus.groovy.typesheet;

import org.codehaus.groovy.typesheet.dom.Type;

import groovy.lang.*;

public class Builder extends GroovyObjectSupport {

    private Type type;

    public Type type(Object t, Closure c) {
        if(t instanceof Class) {
            this.type = new Type((Class<?>)t);
            c.setDelegate(this);
            c.setResolveStrategy(Closure.DELEGATE_ONLY);
            c.call();
            return this.type;
        } else {
            throw new RuntimeException("error");
        }
    }

    public void field(Object f) {

    }

    public void field(Object f, Closure c) {

    }

    @Override
    public Object getProperty(String property) {
        return super.getProperty(property);
    }

}

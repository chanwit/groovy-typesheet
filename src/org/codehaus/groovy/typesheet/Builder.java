package org.codehaus.groovy.typesheet;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.typesheet.dom.Field;
import org.codehaus.groovy.typesheet.dom.Type;

import groovy.lang.*;

public class Builder extends GroovyObjectSupport {

    private Type type;
    private HashMap<String, Var> vars = new HashMap<String, Var>();

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

    // field with action
    public void field(Object f, Closure c) {
        if(f instanceof Var) {
            Var v = (Var)f;
            c.setDelegate(this);
            c.setResolveStrategy(Closure.DELEGATE_ONLY);
            c.call();
            Field field = new Field(v.getName(), v.getKlass());
            this.type.getFields().add(field);
        } else {
            throw new RuntimeException("error");
        }
    }

    // field for futher matching, act as PCD
    public void field(Object f) {

    }

    public Object propertyMissing(String name) {
        if(vars.containsKey(name)) {
            return vars.get(name);
        } else {
            Var v = new Var(name);
            vars.put(name, v);
            return v;
        }
    }

    public Map retype(Map m) {
        return m;
    }

}

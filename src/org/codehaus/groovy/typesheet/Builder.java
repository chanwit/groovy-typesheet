package org.codehaus.groovy.typesheet;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import java.util.HashMap;
import java.util.List;

import org.codehaus.groovy.typesheet.dom.Field;
import org.codehaus.groovy.typesheet.dom.Type;

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
            if(v.getKlass()!=null) {
                Field field = new Field(v.getName(), v.getKlass());
                this.type.getFields().add(field);
            } else {
                throw new RuntimeException(v.getName() + " is bound but not retyped");
            }
        } else if(f instanceof List) {
            List l = (List)f;
            for(Object o: l) {
                if((o instanceof Var) == false)
                    throw new RuntimeException("Error " + f + " is already bound to something");
            }
            c.setDelegate(this);
            c.setResolveStrategy(Closure.DELEGATE_ONLY);
            c.call();
            for(Object o: l) {
                Var v = (Var)o;
                if(v.getKlass()!=null) {
                    Field field = new Field(v.getName(), v.getKlass());
                    this.type.getFields().add(field);
                } else {
                    throw new RuntimeException(v.getName() + " is bound but not retyped");
                }
            }
        } else {
            throw new RuntimeException("Error " + f + " is already bound to something");
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

}

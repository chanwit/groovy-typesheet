package org.codehaus.groovy.typesheet.dom;

import groovy.lang.Closure;
import org.codehaus.groovy.typesheet.Builder;

public class Method {

    private Builder builder;
	private Type owner;
    
    private String name;
    private Object[] args;
    private Closure action;

    public Method(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object and(Object other) {
        // other must be ARGS
        if(other instanceof Args) {
            this.args = ((Args)other).getVars();
            this.action = ((Args)other).getAction();
            this.action.setDelegate(builder);
            this.action.setResolveStrategy(Closure.DELEGATE_ONLY);
            this.action.call();
        } else if (other instanceof Local) {

        }
        return this;
    }

}

package org.codehaus.groovy.typesheet.dom;

import groovy.lang.Closure;
import org.codehaus.groovy.typesheet.Builder;

public class Method {

    Builder builder;
	Type owner;
    
    String name;
    Object[] args;
    Closure action;

    def and(other) {
        // other must be ARGS
        if(other instanceof Args) {
            this.arg = other.vars
            this.action = other.action            
            this.action.delegate = builder            
            this.action.resolveStrategy = Closure.DELEGATE_ONLY
            this.action.call()
        } else if (other instanceof Local) {
            // TODO 
        }
        return this
    }

}

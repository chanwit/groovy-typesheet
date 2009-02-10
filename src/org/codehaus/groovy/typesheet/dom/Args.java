package org.codehaus.groovy.typesheet.dom;

import groovy.lang.Closure;

import org.codehaus.groovy.typesheet.Var;

public class Args {

    private Var[] vars;
    // this action will be passed to Method, and executed there
    private Closure action;

    public Args(Var[] vars, Closure c) {
        this.vars = vars;
        this.action = c;
    }

    public Var[] getVars() {
        // TODO Auto-generated method stub
        return vars;
    }

    public Closure getAction() {
        return action;
    }



}

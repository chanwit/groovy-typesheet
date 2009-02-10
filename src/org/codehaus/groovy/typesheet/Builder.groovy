package org.codehaus.groovy.typesheet

import java.util.HashMap
import java.util.List
import java.util.Map

import org.codehaus.groovy.typesheet.dom.Args
import org.codehaus.groovy.typesheet.dom.Field
import org.codehaus.groovy.typesheet.dom.Method
import org.codehaus.groovy.typesheet.dom.Type

class Builder {
        
    private type
    private vars = [:]
    
    Builder() {
        vars["_"] = new Var(name:"_")
    }
    
    def type(t, c) {
        if(!(t instanceof Class))  throw new RuntimeException("error")
        this.type = new Type(klass: t)
        c.delegate = this
        c.resolveStrategy = Closure.DELEGATE_ONLY
        c.call()
        return this.type
    }
    
    def field(f, c) {
        if(f instanceof Var) {
            c.delegate = this
            c.resolveStrategy = Closure.DELEGATE_ONLY
            c.call()
            if(!f.klass) throw new RuntimeException("${v.name} is bound but not retyped");
            this.type.fields << new Field(name: f.name, klass: f.klass)
        } else if(f instanceof List) {
            if(f.any{ !(it instanceof Var) }) 
                throw new RuntimeException("Error: $f is already bound to something");
            c.delegate = this
            c.resolveStrategy = Closure.DELEGATE_ONLY
            c.call()                
            f.each { v ->
                if(!v.klass) throw new RuntimeException("${v.name} is bound but not retyped");
                this.type.fields << new Field(name: v.name, klass: v.klass)
            }
        } else throw new RuntimeException("Error: $f is already bound to something");
    }
    
    def propertyMissing(String name) {
        if(this.vars.containsKey(name)) {
            this.vars[name]
        } else {
            this.vars[name] = new Var(name: name)
        }
    }    

}
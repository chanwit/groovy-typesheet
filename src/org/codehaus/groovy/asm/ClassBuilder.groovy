package org.codehaus.groovy.asm;

class ClassBuilder implements Opcodes {

    ClassBuilder() {
        
    }
    
    def newClass(modifier, args) {
        switch(modifier) {
            case "public": flags = ACC_PUBLIC; break;
            case "protected": flags = ACC_PROTECTED; break;
            case "private": flags = ACC_PRIVATE; break;
        }
        
        def name
        def closure = args[-1]
        if(args[0] instanceof Map) {
            name = args[0]["name"]
        } else {
            name = args[0]            
        }
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.call()
    }
    
    def methodMissing(name, args) {
        def names = name.split("_")
        switch(names[-1]) {
            case "class": newClass(names[0], args); break; // support only one param
        }        
    }

}
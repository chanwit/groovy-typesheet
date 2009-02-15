package org.codehaus.groovy.asm;

import org.objectweb.asm.*

class ClassBuilder implements Opcodes {

    ClassWriter cw = new ClassWriter(0)
    
    private FieldVisitor fv
    private MethodVisitor mv
    private AnnotationVisitor av0
    
    private boolean classInitialized = false
    
    // Class Name
    private String className    
    // Class File version
    private int version = V1_3
    // Modifier flags
    private int modifiers
    
    def doClassConstruction() {
        cw.visit(this.version, 
            this.modifiers + ACC_SUPER, 
            this.className, 
            null,               // TODO interfaces
            "java/lang/Object", // TODO super class
            null)               // TODO exception tables
            
        this.classInitialized = true
    }    

    def newClass(modifiers, args) {
        // set modifiers
        this.modifiers = modifiers
        
        // get name parameter
        if(args[0] instanceof Map) {
            this.className = args[0]["name"]
        } else {
            this.className = args[0]
        }

        // this is to invoke class body construction
        // we expect 
        // version to be set
        // at least, one method builder to be called (for triggering #doClassConstruction)
        // {            
        //      version 1.5
        //      public_method(name:"<init>()V") { }
        // }
        def classBody = args[-1]
        classBody.delegate = this
        classBody.resolveStrategy = Closure.DELEGATE_FIRST
        classBody.call()
        
        // no method builder has been called to trigger class construction?
        // so we need to do something
        if(!classInitialized) {
            doClassConstruction()            
        }
        cw.visitEnd()
    }
    
    def newMethod(modifiers, args) {
        if(!classInitialized) {
            doClassConstruction()
        }
        
        // get name parameter
        def name
        if(args[0] instanceof Map) {
            name = args[0]["name"]
        } else {
            name = args[0]
        }        
                
        // get closure from the last argument
        def methodBody = args[-1]            
        
        // create a method builder for each method    
        def mb = new MethodBuilder(
            classBuilder: this,
            name: name, 
            modifiers: modifiers, 
            methodBody: methodBody)
        mb.build()
    }

    def getModifiers(names) {
        def m = 0; // FIXME: ACC_DEFAULT ?
        if(names.any{it == "static"}) m += ACC_STATIC;

        if(names.any{it == "public"}) m += ACC_PUBLIC else
        if(names.any{it == "protected"}) m += ACC_PUBLIC else
        if(names.any{it == "private"}) m += ACC_PUBLIC;

        return m
    }
    
    def version(arg) {
        switch(arg) {
            case 1.1:   this.version = V1_1; break;
            case 1.2:   this.version = V1_2; break;
            case 1.3:   this.version = V1_3; break;
            case 1.4:   this.version = V1_4; break;
            case 1.5:   this.version = V1_5; break;
            case 1.6:   this.version = V1_6; break;
        }
    }

    def methodMissing(String name, args) {
        def names = name.split("_")
        def mods = getModifiers(names)
        
        switch(names[-1]) {
            case "class": newClass(mods, args); break // support only one param
            case "method": newMethod(mods, args); break
        }
    }

}
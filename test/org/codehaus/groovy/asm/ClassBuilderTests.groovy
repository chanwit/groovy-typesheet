package org.codehaus.groovy.asm

import junit.framework.*

class ClassBuilderTests extends TestCase {

    void testSomething() {
        def cb = new ClassBuilder()
        cb.public_class("my/class/A") {
            version 1.6
            
            public_method("<init>()V") {
                aload 0
                invokespecial "java/lang/Object", "<init>", "()V"
                _return
            }
        }
        
        def b = cb.cw.toByteArray()
        assert b instanceof byte[]
        new File("A.class") << b
    }

}
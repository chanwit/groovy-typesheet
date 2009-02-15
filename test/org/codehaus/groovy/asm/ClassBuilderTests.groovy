package org.codehaus.groovy.asm

import junit.framework.*
import static org.codehaus.groovy.asm.Utils.*

class ClassBuilderTests extends TestCase {

    void testBuildClass() {
        def cb = new ClassBuilder()
        cb.public_class("my/class/A") {
            version 1.6

            public_method("<init>()V") {
                aload 0
                invokespecial "java/lang/Object", "<init>", "()V"
                _return
            }
        }
        def bytes = cb.cw.toByteArray()
        assert bytes instanceof byte[]
        Class classA = loadClass("my.class.A", bytes)
        assert classA.name == "my.class.A"
    }

    void testBuildClass_invoke() {
        def cb = new ClassBuilder()
        cb.public_class("my/class/B") {
            version 1.6

            public_method name:"<init>()V", {
                aload 0
                invokespecial Object.class, "<init>()V"
                _return
            }
        }

        def bytes = cb.cw.toByteArray()
        assert bytes instanceof byte[]
        new File("B.class").delete()
        new File("B.class") << bytes
        Class classB = loadClass("my.class.B", bytes)
        assert classB.name == "my.class.B"
   }
   
    void testBuildClass_2() {
        def cb = new ClassBuilder()
        cb.public_class("my/class/C") {
            version 1.6

            public_method void, "<init>", {
                aload 0
                invokespecial Object, "<init>()V"
                _return
            }
            
            public_method (void, "me", int) {
                _return
            }
        }

        def bytes = cb.cw.toByteArray()
        assert bytes instanceof byte[]
        new File("C.class").delete()
        new File("C.class") << bytes
        Class classC = loadClass("my.class.C", bytes)
        assert classC.name == "my.class.C"
   }   

}
package org.codehaus.groovy.typesheet

import org.codehaus.groovy.typesheet.Builder
import org.codehaus.groovy.typesheet.dom.Type

class MethodTests extends GroovyTestCase {

/**
Syntax discussion

// (1)
// This syntax matches method test, and retypes its return type to be C.class
// It fails when transformation conflict
method(name:'test') { ret >> C.class }

// (2)
// This syntax matches 
// - method named test
// - with a single argument of any type
// and retypes the only argument to be Integer.class
// It fails when transformation conflict
method('test') & args(a) { a >> Integer.class }
method('test') & args(a:_) { a >> Integer.class }

(2.3)
This matches by the default name:
method('test') & args(_) { it >> Integer.class }

// (3)
// This syntax matches method test(int), and retypes the only argument to be Integer.class
// It fails when transformation conflict
method('test') & args(a: int) { a >> Integer.class }

**/
    void testMethod_with_args() {
        def b = new Builder()
        def r = b.type(int) {
            method(name:'test') && args([a,b,c]) {
                a >> int
                b >> int
                c >> int
            }
        }
        assert r instanceof Type
        assert r.klass == int
        
        assert r.methods.size == 1
        assert r.methods[0].name == 'test'
        assert r.methods[0].args[0].name == 'a'
        assert r.methods[0].args[0].klass == int

        assert r.methods[0].args[1].name == 'b'
        assert r.methods[0].args[1].klass == int

        assert r.methods[0].args[2].name == 'c'
        assert r.methods[0].args[2].klass == int
    }
    
    void testMethod_with_args_2() {
        def b = new Builder()
        def r = b.type(int) {
            method(name:'test') && args(a:_,b:_,c:int) {
            	
            }
        }
        
        assert r.methods.size == 1
        assert r.methods[0].name == 'test'
        assert r.methods[0].args[0].name == 'a'
        assert r.methods[0].args[0].klass == int
    }    

}

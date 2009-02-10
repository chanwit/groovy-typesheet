package org.codehaus.groovy.typesheet

import org.codehaus.groovy.typesheet.Builder
import org.codehaus.groovy.typesheet.dom.Type

class MethodTests extends GroovyTestCase {

    void testMethod_with_args() {
        def b = new Builder()
        def r = b.type(int) {
            field(f) { f >> int }
            method(name:'test') && args([a,b,c]) {
                a >> int
                b >> int
                c >> int
            }
            method(name:'test') && args(a:_,b:_,c:int) {
            	
            }
        }
        assert r instanceof Type
        assert r.klass == int
        assert r.fields.size == 1
        assert r.fields[0].name == 'f'
        assert r.fields[0].klass == int
        
        assert r.methods.size == 1
        assert r.methods[0].name == 'test'
        assert r.methods[0].args[0].name == 'a'
        assert r.methods[0].args[0].klass == int

        assert r.methods[0].args[1].name == 'b'
        assert r.methods[0].args[1].klass == int

        assert r.methods[0].args[2].name == 'c'
        assert r.methods[0].args[2].klass == int
    }

}

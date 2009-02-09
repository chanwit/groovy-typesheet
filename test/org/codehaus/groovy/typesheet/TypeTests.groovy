package org.codehaus.groovy.typesheet

import org.codehaus.groovy.typesheet.Builder
import org.codehaus.groovy.typesheet.dom.Type

class TypeTests extends GroovyTestCase {

    void testSimple() {
        def b = new Builder()
        def r = b.type(int) {

        }
        assert r instanceof Type
        assert r.klass == int
    }

}

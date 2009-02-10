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

    void testSimple_with_field_retype() {
        def b = new Builder()
        def r = b.type(int) {
            field(f) { f >> int.class }
        }
        assert r instanceof Type
        assert r.klass == int
        assert r.fields.size == 1
        assert r.fields[0].name == 'f'
        assert r.fields[0].klass == int
    }

    void test_bind_list_of_fields() {
        def b = new Builder()
        def r = b.type(int) {
            field([f1, f2]) { f1 >> int.class; f2 >> String.class }
        }
        assert r instanceof Type
        assert r.klass == int
        assert r.fields.size == 2
        assert r.fields[0].name == 'f1'
        assert r.fields[0].klass == int

        assert r.fields[1].name == 'f2'
        assert r.fields[1].klass == String

    }

    void testSimple_field_as_pcd() {
        assert 1 == 1
    }

}

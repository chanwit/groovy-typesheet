package org.codehaus.groovy.typesheet

import org.codehaus.groovy.typesheet.Builder
import org.codehaus.groovy.typesheet.dom.Type

class CallTests extends GroovyTestCase {

/**

Idea is a call replacement, by forcing type
This reads:
- for every 'call' in MyProgram.main(String[]), enforcing type of println to be "s"

type(MyProgram) {
    method(main) && args(String[]) {
        pcall(println(s)) { s >> String }
    }
}

**/
    def testCall() {
        
    }

}
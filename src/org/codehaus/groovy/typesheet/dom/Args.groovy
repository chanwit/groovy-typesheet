package org.codehaus.groovy.typesheet.dom

import groovy.lang.Closure
import org.codehaus.groovy.typesheet.Var

class Args {

    Var[] vars
    // this action will be passed to Method, and executed there
    Closure action

}

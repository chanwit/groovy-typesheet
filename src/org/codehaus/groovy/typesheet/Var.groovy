package org.codehaus.groovy.typesheet

class Var {

    String name
    Class klass

    def rightShift(t) {
        this.klass = t
    }

}

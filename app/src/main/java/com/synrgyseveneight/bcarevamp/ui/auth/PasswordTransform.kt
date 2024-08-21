package com.synrgyseveneight.bcarevamp.ui.auth

import android.text.method.PasswordTransformationMethod
import android.view.View

class PasswordTransform : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return object : CharSequence {
            override val length: Int
                get() = source.length

            override fun get(index: Int): Char {
                return '*'
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return source.subSequence(startIndex, endIndex)
            }

            override fun toString(): String {
                return source.toString()
            }
        }
    }
}
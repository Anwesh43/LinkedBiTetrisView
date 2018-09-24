package com.anwesh.uiprojects.linkedbitetrisview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.bitetrisview.BiTetrisView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BiTetrisView.create(this)
    }
}

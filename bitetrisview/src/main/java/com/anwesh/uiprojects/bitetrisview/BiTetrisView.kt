package com.anwesh.uiprojects.bitetrisview

/**
 * Created by anweshmishra on 24/09/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.content.Context

val nodes : Int = 5

fun Canvas.drawBTNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = (h) / (nodes + 1)
    val size : Float = 2 * gap / 3
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#4527A0")
    save()
    translate(w/2, gap)
    for (j in 0..1) {
        val sc : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f * j)) * 2
        val sc1 : Float = Math.min(0.5f, sc) * 2
        val sc2 : Float = Math.min(0.5f, sc) * 2
        val sf : Float = 1f - 2 * (j)
        save()
        translate((w/2 - size/2) * sf * sc2, 0f)
        drawLine(0f, -size/2, 0f, size/2, paint)
        drawLine(0f, size/2, (size/2) * sc1 * sf, size/2, paint)
        restore()
    }
    restore()
}

class BiTetrisView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += 0.025f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BTNode(var i : Int, val state : State = State()) {

        var prev : BTNode? = null
        var next : BTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BTNode(i + 1)
                next?.prev = this
            }
        }

        fun getNext(dir : Int, cb : () -> Unit) : BTNode {
            var curr : BTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBTNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }
    }

    data class BiTetris(var i : Int) {
        private var root : BTNode = BTNode(0)
        private var curr : BTNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }
}
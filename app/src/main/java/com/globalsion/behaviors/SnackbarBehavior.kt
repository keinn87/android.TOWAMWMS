package com.globalsion.behaviors

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.view.View

/** Fix when [Snackbar] overlapped other component. */
class SnackbarBehavior : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        var totalHeight = 0
        for (i in 0 until parent.childCount) {
            if (parent.getChildAt(i) == child) {
                continue
            }
            totalHeight += parent.getChildAt(i).height
        }

        val layoutParams = child.layoutParams
        layoutParams.height = parent.height - totalHeight
        child.layoutParams = layoutParams

        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        var totalHeight = 0
        for (i in 0 until parent.childCount) {
            if (parent.getChildAt(i) == child) {
                continue
            }
            totalHeight += parent.getChildAt(i).height
        }

        val layoutParams = child.layoutParams
        layoutParams.height = parent.height - totalHeight
        child.layoutParams = layoutParams
    }
}
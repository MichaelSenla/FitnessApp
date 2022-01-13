package com.senla.fitnessapp.presentation.main.coordinatorLayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabButtonBehavior(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<FloatingActionButton>() {

    companion object {
        private const val MIDDLE_VALUE = 0
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {

        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
            coordinatorLayout, child, directTargetChild, target, axes, type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout, child, target,
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed
        )

        if (dyConsumed > MIDDLE_VALUE && child.visibility != View.VISIBLE) {
            child.show()
        } else if (dyConsumed < MIDDLE_VALUE && child.visibility == View.VISIBLE) {
            child.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    super.onHidden(fab)
                    fab?.visibility = View.INVISIBLE
                }
            })
        }
    }
}
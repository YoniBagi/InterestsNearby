package com.interestsnearby.customUI

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout


/*
    Without these settings the coordinator listen to all touch in appBar include Map,
    these settings define that coordinator listen just to recyclerView.
    The DragCallback interface allows to choose whether the sibling scrolling
     view should be controlled by scrolls onto the AppBarLayout.
     Now we can to use it in attribute of Appbar like:
     app:layout_behavior=".customUI.FixedDragAppBarLayoutBehavior"
*/
class FixedDragAppBarLayoutBehavior(context: Context?, attrs: AttributeSet?) :
    AppBarLayout.Behavior(context, attrs) {
    init {
        setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
    }
}
package ca.uoit.quickdraw.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import android.view.View


class ObservableScrollView(ctx: Context, attrSet: AttributeSet) : ScrollView(ctx, attrSet) {

    var scrollListener: ScrollViewListener? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val view = getChildAt(childCount - 1) as View
        val diff = view.bottom - (height + scrollY)

        if (diff == 0) {
            scrollListener?.onScrollToBottom(this)
        }

        super.onScrollChanged(l, t, oldl, oldt)
    }

    interface ScrollViewListener {
        fun onScrollToBottom(view: ScrollView)
    }
}

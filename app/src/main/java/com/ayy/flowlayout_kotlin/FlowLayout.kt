package com.ayy.flowlayout_kotlin

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import kotlin.math.max

class FlowLayout(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {
    private val allLineViews = mutableListOf<List<View>>()
    private val allLineHeight = mutableListOf<Int>()
    var maxLine = -1
    var horizontalSpace = 0
    var verticalSpace = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout)
        maxLine = typedArray.getInt(R.styleable.FlowLayout_max_line, -1)
        horizontalSpace =
            typedArray.getDimensionPixelSize(R.styleable.FlowLayout_horizontal_space, 0)
        verticalSpace = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_vertical_space, 0)
        typedArray.recycle()
    }

    private fun clearData() {
        allLineViews.clear()
        allLineHeight.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        clearData()
        val selfWidth = MeasureSpec.getSize(widthMeasureSpec)
        val selfFreeWith = selfWidth - paddingLeft - paddingRight
        var selfHeight = MeasureSpec.getSize(heightMeasureSpec)
        var selfNeedWidth = paddingLeft + paddingRight
        var selfNeedHeight = paddingTop + paddingBottom

        var lineViews = mutableListOf<View>()
        var lineWidth = 0
        var lineHeight = 0
        var lineNumber = 0
        for (i in 0 until childCount) {
            //测量子view
            val childView = getChildAt(i)
            val layoutParams = childView.layoutParams
            val childWidthMeasureSpec = getChildMeasureSpec(
                widthMeasureSpec,
                paddingLeft + paddingRight,
                layoutParams.width
            )
            val childHeightMeasureSpec = getChildMeasureSpec(
                heightMeasureSpec,
                paddingTop + paddingBottom,
                layoutParams.height
            )
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            val childWidth = childView.measuredWidth + childView.marginLeft + childView.marginRight
            val childHeight =
                childView.measuredHeight + childView.marginTop + childView.marginBottom
            //判断是否需要换行
            if (lineWidth + childWidth > selfFreeWith) {
                allLineViews.add(lineViews)
                allLineHeight.add(lineHeight + verticalSpace)
                selfNeedWidth = max(selfNeedWidth, lineWidth)
                lineNumber++
                if (maxLine in 1..lineNumber) {
                    selfNeedHeight += lineHeight
                    break
                }
                selfNeedHeight += lineHeight + verticalSpace

                //重置行数据
                lineViews = mutableListOf()
                lineWidth = 0
                lineHeight = 0
            }
            //累加数据
            lineWidth += childWidth + horizontalSpace
            lineHeight = max(lineHeight, childHeight)
            lineViews.add(childView)

            //处理最后一行
            if (i == childCount - 1) {
                allLineViews.add(lineViews)
                allLineHeight.add(lineHeight)
                selfNeedWidth = max(selfNeedWidth, lineWidth)
                selfNeedHeight += lineHeight
            }
        }
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.EXACTLY) {
            selfNeedWidth = selfWidth
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            selfNeedHeight = selfHeight
        }
        Log.e("==ayy", "width:$selfNeedWidth,height:$selfNeedHeight")
        setMeasuredDimension(selfNeedWidth, selfNeedHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = paddingTop
        for (i in allLineViews.indices) {
            val lineViews = allLineViews[i]
            val lineHeight = allLineHeight[i]
            var left = paddingLeft
            for ((index, childView) in lineViews.withIndex()) {
                left += childView.marginLeft
                if (index != 0) {
                    left += horizontalSpace
                }
                top += childView.marginTop
                val right = left + childView.measuredWidth
                val bottom = top + childView.measuredHeight
                childView.layout(left, top, right, bottom)
                left = right + childView.marginRight
            }
            top += lineHeight
        }
    }
}
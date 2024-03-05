package xyz.teamgravity.swipetoaction

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

class SwipeTouchAdapter(
    private val leftAction: SwipeTouchAction,
    private val rightAction: SwipeTouchAction
) : ItemTouchHelper.Callback() {

    var listener: SwipeTouchListener? = null

    private var swipeBack: Boolean = false
    private var buttonState: ButtonState = ButtonState.Gone
    private var currentButton: RectF? = null
    private var currentViewHolder: RecyclerView.ViewHolder? = null

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
        makeMovementFlags(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonState != ButtonState.Gone
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder.bindingAdapterPosition == RecyclerView.NO_POSITION) return
        (viewHolder as? SwipeLifecycle)?.apply { if (dX == 0F && !swipeBack) onSwipeStop() else onSwipeStart() }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            when (buttonState) {
                ButtonState.Gone -> setTouchListener(
                    c = c,
                    recyclerView = recyclerView,
                    viewHolder = viewHolder,
                    dX = dX,
                    dY = dY,
                    actionState = actionState,
                    isCurrentlyActive = isCurrentlyActive
                )

                ButtonState.LeftVisible -> super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    max(dX, viewHolder.itemView.height.toFloat()),
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                ButtonState.RightVisible -> super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    min(dX, -viewHolder.itemView.height.toFloat()),
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        if (buttonState == ButtonState.Gone) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        currentViewHolder = viewHolder
    }

    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                when {
                    dX < -viewHolder.itemView.height -> buttonState = ButtonState.RightVisible
                    dX > viewHolder.itemView.height -> buttonState = ButtonState.LeftVisible
                    else -> Unit
                }

                if (buttonState != ButtonState.Gone) {
                    setTouchDownListener(
                        c = c,
                        recyclerView = recyclerView,
                        viewHolder = viewHolder,
                        dY = dY,
                        actionState = actionState,
                        isCurrentlyActive = isCurrentlyActive
                    )
                    setItemsClickable(
                        recyclerView = recyclerView,
                        isClickable = false
                    )
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c = c,
                    recyclerView = recyclerView,
                    viewHolder = viewHolder,
                    dY = dY,
                    actionState = actionState,
                    isCurrentlyActive = isCurrentlyActive
                )
            }
            return@setOnTouchListener false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive)
                recyclerView.setOnTouchListener innerOnTouchListener@{ _, _ ->
                    return@innerOnTouchListener false
                }
                setItemsClickable(
                    recyclerView = recyclerView,
                    isClickable = true
                )
                swipeBack = false

                if (listener != null && currentButton != null && currentButton?.contains(event.x, event.y) == true) {
                    when (buttonState) {
                        ButtonState.Gone -> Unit
                        ButtonState.LeftVisible -> listener?.onSwipeClickLeft(viewHolder.bindingAdapterPosition)
                        ButtonState.RightVisible -> listener?.onSwipeClickRight(viewHolder.bindingAdapterPosition)
                    }
                }

                buttonState = ButtonState.Gone
                (viewHolder as? SwipeLifecycle)?.onSwipeStop()
                currentViewHolder = null
            }

            return@setOnTouchListener false
        }
    }

    private fun setItemsClickable(
        recyclerView: RecyclerView,
        isClickable: Boolean
    ) {
        recyclerView.children.forEach { view ->
            view.isClickable = isClickable
        }
    }

    private fun drawActions(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val leftPosition = viewHolder.itemView.left
        val topPosition = viewHolder.itemView.top
        val rightPosition = viewHolder.itemView.right
        val bottomPosition = viewHolder.itemView.bottom
        val middlePosition = rightPosition / 2
        val buttonWidth = viewHolder.itemView.height
        val difference = bottomPosition - topPosition

        val leftPaint = Paint()
        leftPaint.color = leftAction.background
        val leftBackground = RectF(leftPosition.toFloat(), topPosition.toFloat(), middlePosition.toFloat(), bottomPosition.toFloat())
        c.drawRect(leftBackground, leftPaint)
        val leftButton =
            RectF(leftPosition.toFloat(), topPosition.toFloat(), (leftPosition + buttonWidth).toFloat(), bottomPosition.toFloat())
        c.drawRect(leftButton, leftPaint)

        val leftImageWidth = leftAction.icon.intrinsicWidth
        val leftImageHeight = leftAction.icon.intrinsicHeight
        val leftImageMargin = (difference - leftImageHeight) / 2
        val leftImageTop = topPosition + (difference - leftImageHeight) / 2
        val leftImageBottom = leftImageTop + leftImageHeight
        val leftImageLeft = leftPosition + leftImageMargin
        val leftImageRight = leftImageLeft + leftImageWidth
        leftAction.icon.setBounds(leftImageLeft, leftImageTop, leftImageRight, leftImageBottom)
        leftAction.icon.draw(c)

        val rightPaint = Paint()
        rightPaint.color = rightAction.background
        val rightBackground = RectF(middlePosition.toFloat(), topPosition.toFloat(), rightPosition.toFloat(), bottomPosition.toFloat())
        c.drawRect(rightBackground, rightPaint)
        val rightButton =
            RectF((rightPosition - buttonWidth).toFloat(), topPosition.toFloat(), rightPosition.toFloat(), bottomPosition.toFloat())
        c.drawRect(rightButton, rightPaint)

        val rightImageWidth = rightAction.icon.intrinsicWidth
        val rightImageHeight = rightAction.icon.intrinsicHeight
        val rightImageMargin = (difference - rightImageHeight) / 2
        val rightImageTop = topPosition + (difference - rightImageHeight) / 2
        val rightImageBottom = rightImageTop + rightImageHeight
        val rightImageRight = rightPosition - rightImageMargin
        val rightImageLeft = rightImageRight - rightImageWidth
        rightAction.icon.setBounds(rightImageLeft, rightImageTop, rightImageRight, rightImageBottom)
        rightAction.icon.draw(c)

        currentButton = when (buttonState) {
            ButtonState.Gone -> null
            ButtonState.LeftVisible -> leftButton
            ButtonState.RightVisible -> rightButton
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    fun attachToRecyclerView(view: RecyclerView) {
        ItemTouchHelper(this).attachToRecyclerView(view)
        view.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    drawActions(
                        c = c,
                        viewHolder = currentViewHolder ?: return
                    )
                }
            }
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////

    interface SwipeTouchListener {
        fun onSwipeClickLeft(position: Int)
        fun onSwipeClickRight(position: Int)
    }

    interface SwipeLifecycle {
        fun onSwipeStart()
        fun onSwipeStop()
    }

    data class SwipeTouchAction(
        val icon: Drawable,
        @ColorInt val background: Int
    ) {
        companion object {
            fun fromResource(
                context: Context,
                @DrawableRes icon: Int,
                @ColorRes background: Int
            ): SwipeTouchAction {
                return SwipeTouchAction(
                    icon = ContextCompat.getDrawable(context, icon)!!,
                    background = ContextCompat.getColor(context, background)
                )
            }
        }
    }

    private enum class ButtonState {
        Gone,
        LeftVisible,
        RightVisible
    }
}

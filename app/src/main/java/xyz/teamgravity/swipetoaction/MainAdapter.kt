package xyz.teamgravity.swipetoaction

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.swipetoaction.databinding.CardNameBinding

class MainAdapter : ListAdapter<NameModel, MainAdapter.MainViewHolder>(MainDiff) {

    class MainViewHolder(private val binding: CardNameBinding) : RecyclerView.ViewHolder(binding.root), SwipeTouchAdapter.SwipeLifecycle {

        fun bind(model: NameModel) {
            binding.apply {
                root.background = regularBackground
                nameT.text = model.name
            }
        }

        override fun onSwipeStart() {
            binding.apply {
                root.background = curvedBackground
                bottomLine.visibility = View.INVISIBLE
            }
        }

        override fun onSwipeStop() {
            binding.apply {
                root.background = regularBackground
                bottomLine.visibility = View.VISIBLE
            }
        }

        private val regularBackground: GradientDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
        }

        private val curvedBackground: GradientDrawable by lazy {
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = binding.root.context.toPx(20).toFloat()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(CardNameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////

    private object MainDiff : DiffUtil.ItemCallback<NameModel>() {
        override fun areItemsTheSame(oldItem: NameModel, newItem: NameModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NameModel, newItem: NameModel): Boolean {
            return oldItem == newItem
        }
    }
}
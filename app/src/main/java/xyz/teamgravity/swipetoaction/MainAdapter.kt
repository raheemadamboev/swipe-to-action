package xyz.teamgravity.swipetoaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.swipetoaction.databinding.CardNameBinding

class MainAdapter : ListAdapter<NameModel, MainAdapter.MainViewHolder>(MainDiff) {

    class MainViewHolder(private val binding: CardNameBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: NameModel) {
            binding.apply {
                nameT.text = model.name
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
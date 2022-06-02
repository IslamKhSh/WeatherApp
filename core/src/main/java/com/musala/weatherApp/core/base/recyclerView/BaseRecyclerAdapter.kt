package com.musala.weatherApp.core.base.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param M the list item model
 * */
abstract class BaseRecyclerAdapter<M, V : ViewDataBinding>(
    areItemsTheSame: (M, M) -> Boolean = { _, _ -> false },
    areItemsContentsTheSame: (M, M) -> Boolean = { oldItem, newItem -> oldItem == newItem }
) : ListAdapter<M, BaseViewHolder<V>>(getDiffUtil(areItemsTheSame, areItemsContentsTheSame)) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<V> {
        val binding = DataBindingUtil.inflate<V>(
            LayoutInflater.from(parent.context), itemLayoutRes, parent, false
        )

        return BaseViewHolder(binding)
    }

    /**
     * the layout of the item
     */
    abstract val itemLayoutRes: Int

    override fun onBindViewHolder(holder: BaseViewHolder<V>, position: Int) {
        bind(holder.binding, getItem(position), position)
        holder.binding.executePendingBindings()
    }

    /**
     * Bind the item of type [M] to the itemView
     */
    protected abstract fun bind(binding: V, item: M, position: Int)

    /**
     * append items to the current list
     */
    fun addToList(newList: List<M>) {
        submitList(ArrayList(currentList).apply { addAll(newList) })
    }

    /**
     * clear all items
     */
    fun clear() {
        submitList(emptyList())
    }
}

private fun <M> getDiffUtil(
    areItemsTheSame: (M, M) -> Boolean,
    areItemsContentsTheSame: (M, M) -> Boolean
) = AsyncDifferConfig
    .Builder(object : DiffUtil.ItemCallback<M>() {
        override fun areItemsTheSame(oldItem: M, newItem: M) = areItemsTheSame(oldItem, newItem)
        override fun areContentsTheSame(oldItem: M, newItem: M) =
            areItemsContentsTheSame(oldItem, newItem)
    })
    .setBackgroundThreadExecutor(Executors.newFixedThreadPool(DIFF_UTIL_THREADS_NUM))
    .build()

class BaseViewHolder<out T : ViewDataBinding> constructor(val binding: T) :
    RecyclerView.ViewHolder(binding.root)

private const val DIFF_UTIL_THREADS_NUM = 3

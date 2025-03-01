package com.example.currencyconverter.util

import android.opengl.Visibility
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.RateItem
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlin.math.round

class MyAdapter(val recycler: RecyclerView) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    val viewHolderMap = hashMapOf<String, ViewHolder>()
    var count: Double = 1.0
    var items: MutableList<RateItem> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
            .run {
                input.setText(count.toString())
                ViewHolder(this)
            }


    override fun getItemCount(): Int = items.size

    fun updateItems(items: MutableList<RateItem>) {
        this.items = items
    }

    fun update(item: RateItem) {
        viewHolderMap[item.code]?.bind(item)
    }
    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.code?.let { viewHolderMap.remove(it) }
    }
    override fun getItemViewType(position: Int): Int {
        return when{
            position ==0 -> 0
            else -> 1
        }

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            if (getItemViewType(position)==0) {
                holder.container.visibility = View.GONE
                holder.inputContainer.visibility = View.VISIBLE
            } else {
                holder.inputContainer.visibility = View.GONE
                holder.container.visibility = View.VISIBLE
                           }
            bind(items[position])
            viewHolderMap[items[position].code] = holder
        }

    }

    inner class ViewHolder(
        itemView: View,
        val container: ConstraintLayout = itemView.findViewById(R.id.container),
        val inputContainer: ConstraintLayout = itemView.findViewById(R.id.input_container),
        val input: EditText = itemView.findViewById(R.id.input),
        val text: TextView = container.findViewById(R.id.name),
        val money: TextView = inputContainer.findViewById(R.id.money),
        val amount: TextView = container.findViewById(R.id.amount_of_money),
        val date: TextView = container.findViewById(R.id.date),
        private val recycler: RecyclerView = this@MyAdapter.recycler,
        item: View = itemView.findViewById(R.id.item)
    ) : RecyclerView.ViewHolder(itemView) {

        var code: String? = null

        init {
            item.setOnClickListener(moveToTop())
        }

        private fun moveToTop(): (View) -> Unit = {

            layoutPosition.takeIf { it > 0 }?.also { currentPosition ->
                items.removeAt(currentPosition).also {
                    items.add(0, it)
                }
                notifyItemMoved(currentPosition, 0)
            }
            recycler.scrollToPosition(0)
        }


        fun bind(item: RateItem) {

            code = item.code
            money.text = item.alphaCode
            "${item.name}".also { text.text = it }
            date.text = item.date
            "${
                (count * items.first().inverseRate * item.rate) //.round(4)  //Yuvarlaqlaşdırmaq üçün
            } ${item.alphaCode}".also { amount.text = it }
            input.setHint(item.name)
            input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    //getting new rates
                    if (input.text.isNullOrEmpty())
                        this@MyAdapter.count = 0.0
                    else
                        this@MyAdapter.count = input.text.toString().toDouble()
                    notifyItemRangeChanged(1, items.size)
                }
            })
        }
    }


}


fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}
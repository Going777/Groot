package com.chocobi.groot.data

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

class CustomAutoCompleteAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items), Filterable {

    private val filterItems: MutableList<String> = mutableListOf()

    override fun getCount(): Int {
        return filterItems.size
    }

    override fun getItem(position: Int): String? {
        return filterItems[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                constraint?.let {
                    filterItems.clear()
                    for (item in items) {
                        if (item.contains(constraint, ignoreCase = true)) {
                            filterItems.add(item)
                        }
                    }
                    filterResults.values = filterItems
                    filterResults.count = filterItems.size
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}

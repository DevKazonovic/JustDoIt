package com.devkazonovic.projects.mytasks.presentation.common

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class MaterialSpinnerAdapter<String>(context: Context, layout: Int, var values: Array<String>) :
    ArrayAdapter<String>(context, layout, values) {
    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = values
            results.count = values.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return filter
    }
}
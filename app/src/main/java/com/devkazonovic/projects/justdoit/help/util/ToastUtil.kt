package com.devkazonovic.projects.justdoit.help.util

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showToast(message : String){
    Toast.makeText(this.requireContext(),message,Toast.LENGTH_LONG).show()
}
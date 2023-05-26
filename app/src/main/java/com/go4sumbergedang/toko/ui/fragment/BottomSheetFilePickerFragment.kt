package com.go4sumbergedang.toko.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.go4sumbergedang.toko.R
import com.go4sumbergedang.toko.databinding.BottomSheetFilePickBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.anko.AnkoLogger

class BottomSheetFilePickerFragment : BottomSheetDialogFragment(), AnkoLogger {
    lateinit var binding: BottomSheetFilePickBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.bottom_sheet_file_pick,container,false)
        binding.lifecycleOwner = this
        return binding.root
    }
}
package com.lucascabral.qrcodeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lucascabral.qrcodeapp.databinding.BottomDialogBinding
import java.util.concurrent.Executors

class BottomDialog : BottomSheetDialogFragment() {
    private lateinit var binding: BottomDialogBinding
    private lateinit var fetchUrl: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogTitleTextView.text = fetchUrl
        binding.dialogVisitLinkTextView.setOnClickListener {
            val intent = Intent("android.intent.action.VIEW")
            intent.setData(Uri.parse(fetchUrl))
            startActivity(intent)
        }
        binding.dialogCloseImageView.setOnClickListener {
            dismiss()
        }
    }

    fun fetchUrl(url: String) {
        val executorService = Executors.newSingleThreadExecutor()
        Handler(Looper.getMainLooper())
        executorService.execute {
            fetchUrl = url
            }
    }
}
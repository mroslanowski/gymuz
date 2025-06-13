package com.example.gymuz.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gymuz.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class Plan : Fragment() {

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            Toast.makeText(requireContext(), "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        generateQRCode(url, view)

        view.findViewById<Button>(R.id.scanQrButton).setOnClickListener {
            val options = ScanOptions()
            options.setPrompt("Scan a QR code")
            options.setBeepEnabled(true)
            options.setBarcodeImageEnabled(true)
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scanLauncher.launch(options)
        }
    }

    private fun generateQRCode(content: String, view: View) {
        val writer = MultiFormatWriter()
        try {
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            view.findViewById<ImageView>(R.id.qrCodeImageView)?.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}
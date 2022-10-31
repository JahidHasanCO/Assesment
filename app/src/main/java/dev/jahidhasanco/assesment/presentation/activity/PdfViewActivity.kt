package dev.jahidhasanco.assesment.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import dev.jahidhasanco.assesment.R
import dev.jahidhasanco.assesment.databinding.ActivityPdfViewBinding
import dev.jahidhasanco.assesment.utils.temp.PdfTempData
import java.io.File

@AndroidEntryPoint
class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_view)

        if (PdfTempData.getTempPdf() != null) {
            showPdfFromFile(PdfTempData.getTempPdf()!!)
        }

        binding.toolbar.setNavigationOnClickListener {
            PdfTempData.clear()
            onBackPressed()
        }
    }

    private fun showPdfFromFile(file: File) {
        binding.pdfView.fromFile(file)
            .password(null)
            .defaultPage(0)
            .enableSwipe(true)
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .onPageError { page, _ ->
                Toast.makeText(
                    this@PdfViewActivity,
                    "Error at page: $page", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

    override fun onDestroy() {
        super.onDestroy()
        PdfTempData.clear()
    }
}
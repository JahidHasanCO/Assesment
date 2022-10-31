package dev.jahidhasanco.assesment.utils.temp


import java.io.File

object PdfTempData {
    private var pdf: File? = null

    fun clear() {
        pdf = null
    }

    fun addPdf(pdf: File) {
        this.pdf = pdf
    }

    fun getTempPdf() = pdf
}
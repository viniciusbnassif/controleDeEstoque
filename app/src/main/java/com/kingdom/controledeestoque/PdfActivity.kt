package com.kingdom.controledeestoque

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.ScrollBar
//import com.github.barteksc.pdfviewer.PDFView




class PdfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_pdf)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun isLightTheme(): Boolean {
            return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
        }
        // Verifique se está no modo claro ou escuro
        if (isLightTheme()) {
            // Ícones escuros (modo claro)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            // Ícones claros (modo escuro)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        fun showCustomToast(context: Context, message: String, duration: Long) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT) // Usando LENGTH_SHORT para começar
            toast.show()

            // Use Handler para definir um tempo customizado
            Handler().postDelayed({
                toast.cancel() // Cancela o Toast após o tempo desejado
            }, duration) // Passa a duração personalizada em milissegundos (por exemplo, 5000ms = 5 segundos)
        }
        supportActionBar?.apply {

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }

        var  pdfView: PDFView = findViewById(R.id.pdfView)
        pdfView.fromAsset("guiace.pdf") // Ou use `.fromFile(File)`
            .defaultPage(0)
            .enableSwipe(true)
            .enableAnnotationRendering(true)
            .load()

        var scrollBar: ScrollBar = findViewById(R.id.scrollBar);
        pdfView.setScrollBar(scrollBar);

        var totalPages = pdfView.pageCount
        var currentPage = pdfView.currentPage
        var voltarpg = findViewById<ImageButton>(R.id.voltarpg)
        var avancarpg = findViewById<ImageButton>(R.id.avancarpg)
        var textView = findViewById<TextView>(R.id.textView6)
        textView.text = "$currentPage/$totalPages"


        voltarpg.setOnClickListener {
            if (pdfView.currentPage > 0) {
                var valor = pdfView.currentPage
                var totalPages = pdfView.pageCount
                pdfView.jumpTo(valor)
                textView.text = "$valor/$totalPages"
                showCustomToast(this, "$valor/$totalPages", 800)  // 5000ms = 5 segundos
            }
        }
        avancarpg.setOnClickListener {
            var valor = pdfView.currentPage +2
            var totalPages = pdfView.pageCount
            if (pdfView.currentPage < pdfView.pageCount-1) {
                pdfView.jumpTo(valor)
                textView.text = "$valor/$totalPages"
                showCustomToast(this, "$valor/$totalPages", 800)  // 5000ms = 5 segundos

            }
        }
    }
}
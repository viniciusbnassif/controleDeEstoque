package com.kingdom.controledeestoque

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.appbar.MaterialToolbar

class PdfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        setSupportActionBar(toolbar)

        // calling the action bar
        var actionBar = supportActionBar
        supportActionBar?.apply {

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        var pdfView = findViewById<PDFView>(R.id.pdfView)
        pdfView.fromAsset("guia.pdf").load()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}
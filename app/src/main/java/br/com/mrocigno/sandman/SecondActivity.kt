package br.com.mrocigno.sandman

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class SecondActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        findViewById<AppCompatButton>(R.id.test).apply {
            text = "Forçar crash"
            setOnClickListener {
                startActivity(Intent(this@SecondActivity, MainActivity::class.java))
            }
            setOnLongClickListener {

//                globalTracker.generateReport()
////                    .filterByType(ReportModelType.LOG)
//                    .generate(this@SecondActivity)

                throw Exception("asdsa")
                false
            }
        }
    }
}
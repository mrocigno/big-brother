package br.com.mrocigno.sandman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.mrocigno.sandman.json.JsonViewerActivity

class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(JsonViewerActivity.intent(this, """
            {
                "aa": {"topper": "teste"},
                "bb": {"topper": "aaaa"},
                "cc": { "mds": {
                        "testeee": "aaaa"
                    }
                }
            }
        """.trimIndent()))
    }
}
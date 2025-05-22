package br.com.mrocigno.bigbrother.ui.automator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.common.helpers.SimpleSpinnerAdapter
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.common.utils.toast
import br.com.mrocigno.bigbrother.proxy.R as CR

class PlaygroundActivity : AppCompatActivity(R.layout.playground_activity) {

    private val simulateLoading: AppCompatButton by id(R.id.simulate_loading)
    private val disabledButton: AppCompatButton by id(R.id.disabled_button)
    private val autoCompleteTxt: AppCompatAutoCompleteTextView by id(R.id.auto_complete_txt)
    private val composeView: ComposeView by id(R.id.compose_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        simulateLoading.setOnClickListener {
            toast("Não era pra clicar aqui")
        }

        disabledButton.setOnClickListener {
            toast("Não era pra clicar aqui")
        }

        autoCompleteTxt.setAdapter(
            SimpleSpinnerAdapter(this, resources.getStringArray(CR.array.bigbrother_names))
        )

        setupCompose()
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun setupCompose() = composeView.setContent {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    toast("Não era pra clicar aqui")
                }
            ) {
                Text("Simulate loading")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                onClick = {
                    toast("Não era pra clicar aqui")
                }
            ) {
                Text("Disabled button")
            }

            var text1 by remember { mutableStateOf("") }
            TextField(
                value = text1,
                onValueChange = {
                    text1 = it
                }
            )
            val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(options[0]) }

            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = { },
                    label = { Text("Label") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            }
                        ){
                            Text(text = selectionOption)
                        }
                    }
                }
            }
            Text("TESTEE")
        }
    }

}
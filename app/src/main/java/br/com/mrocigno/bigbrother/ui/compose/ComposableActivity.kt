package br.com.mrocigno.bigbrother.ui.compose

import android.os.Bundle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.mrocigno.bigbrother.R
import br.com.mrocigno.bigbrother.common.R as CR

class ComposableActivity : AppCompatActivity() {

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Toolbar()
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Button(
                            modifier = Modifier.testTag("testButton1"),
                            onClick = {
                                println("aaa")
                            }
                        ) {
                            Text("Test Button Click")
                        }
                        Text(
                            text = "Click me",
                            modifier = Modifier
                                .testTag("testButton2")
                                .combinedClickable(
                                    onClick = {
                                        println("aaa")
                                    },
                                    onLongClick = {
                                        println("bbb")
                                    }
                                ),
                        )
                        Text(
                            text = "aaa",
                            modifier = Modifier
                                .testTag("testButton3")
                                .height(1000.dp)
                                .combinedClickable(
                                    onClick = {
                                        println("aaa")
                                    },
                                    onLongClick = {
                                        println("bbb")
                                    }
                                ),
                        )

                        Text(
                            text = "asdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\nasdasdasda\n",
                            color = Color.White,
                            modifier = Modifier
                                .testTag("testButton3")
                                .height(1000.dp)
                                .combinedClickable(
                                    onClick = {
                                        println("aaa")
                                    },
                                    onLongClick = {
                                        println("bbb")
                                    }
                                ),
                        )
                    }
                }
            }
        }
        val teste = 1
    }
}

@Preview
@Composable
private fun Toolbar() {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    TopAppBar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        title = {
            Text(
                text = stringResource(R.string.compose_activity_button),
                color = colorResource(CR.color.bb_text_title)
            )
        },
        navigationIcon = {
            IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = colorResource(CR.color.bb_text_title)
                )
            }
        }
    )
}
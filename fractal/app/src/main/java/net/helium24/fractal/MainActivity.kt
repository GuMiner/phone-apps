package net.helium24.fractal

import android.os.Bundle
import android.widget.ToggleButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import net.helium24.fractal.ui.theme.FractalTheme

// https://developer.android.com/jetpack/compose/tutorial
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FractalTheme {
                // A surface container using the 'background' color from the theme
                var selectedFractal = remember { mutableStateOf(FractalType.Julia) }
                var animateState = true;
                var animate = remember { mutableStateOf(animateState) }
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column() {
                        InfoText()
                        Row() {
                            FractalChoice(selectedFractal)
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = animate.value,
                                enabled = true,
                                onCheckedChange = { isAnimate -> animate.value = isAnimate },
                                modifier = Modifier.scale(0.5f).padding(0.dp).height(28.dp))
                            Text(
                                text = "Animate",
                                modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
                            )
                        }

                        // https://developer.android.com/jetpack/compose/migrate/interoperability-apis/views-in-compose
                        AndroidView(
                            factory = { FractalSurfaceView(it) },
                            update = { fsv -> fsv.updateFractalType(selectedFractal.value, animate.value) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoText() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        About()
        Spacer(Modifier.weight(1f))
        Credits()
    }
}

@Composable
fun About() {
    Text(
        text = "Demo Fractal Viewer",
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
    )
}

@Composable
fun Credits() {
    Text(
        text = "05/23 Gustave",

        fontStyle = FontStyle.Italic,
        style = TextStyle( // Theoretically this could move to 'Type.kt'
            fontSize = 12.sp,
        ),
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
    )
}

/**
 * Creates a radio button for each FractalType,
 * linking each so button changes apply to chosenFractal
 */
@Composable
fun FractalChoice(chosenFractal: MutableState<FractalType>) {
    Row(Modifier.selectableGroup(), // As per Android docs, recommended for screenreaders
        verticalAlignment = Alignment.CenterVertically) {
        FractalType.values().forEach { text ->
            Column(
                Modifier
                    .selectable(
                        selected = (text == chosenFractal.value),
                        onClick = { chosenFractal.value = text },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Row() {
                    RadioButton(
                        selected = (text == chosenFractal.value),
                        onClick = null // As per Android docs, recommended for screenreaders
                    )
                    Text(
                        text = text.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    FractalTheme {
        Column() {
            InfoText()
            Row() {
                FractalChoice(remember { mutableStateOf(FractalType.Julia) })
                Spacer(Modifier.weight(1f))
                Switch(checked = true, onCheckedChange = { isAnimate -> })
            }
        }
    }
}
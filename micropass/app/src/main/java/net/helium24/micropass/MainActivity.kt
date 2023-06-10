package net.helium24.micropass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.helium24.micropass.ui.theme.MicropassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val assetRetriever = AssetRetriever()
        val assets = assetRetriever.ListAssets(this.applicationContext)

        setContent {
            MicropassTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        Greeting()
                        CredentialList(
                            assets.map { Credential(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    Row() {
        Text(
            text = "MicroPass",
            modifier = Modifier
        )
    }
}

@Composable
fun CredentialList(credentials: List<Credential>) {
    Column {
        credentials.forEach { credential ->
            CredentialRow(credential)
        }
    }
}

@Composable
fun CredentialRow(credential: Credential) {
    Row() {
        Text(
            text = credential.Name.replace(".txt", ""),
            style = TextStyle( // Theoretically this could move to 'Type.kt'
                fontSize = 26.sp,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MicropassTheme {
        Column() {
            Greeting()
            CredentialList(
                listOf(
                    Credential("A"),
                    Credential("B"),
                    Credential("C"),
                    Credential("D")
                )
            )
        }
    }
}
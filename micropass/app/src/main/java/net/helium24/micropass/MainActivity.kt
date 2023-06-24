package net.helium24.micropass

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.helium24.micropass.ui.theme.MicropassTheme

class DialogSettings(
    val ShouldOpenDialog: MutableState<Boolean>,
    val DialogTitle: MutableState<String>,
    val DialogContent: MutableState<String>
)
{ }
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContext = this.applicationContext;
        val assetRetriever = AssetRetriever()
        val assets = assetRetriever.ListAssets(appContext)

        setContent {
            MicropassTheme {
                var aesKey = remember { mutableStateOf("") }

                val dialogSettings = DialogSettings(
                    remember { mutableStateOf(false) },
                    remember { mutableStateOf("") },
                    remember { mutableStateOf("") })

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column() {
                        Row()
                        {
                            TextField(
                                value = aesKey.value,
                                onValueChange = {
                                    aesKey.value = it
                                },
                                label = { Text("Key") }
                            )
                            Greeting()
                        }
                        CredentialList(
                            assets.map { Credential(it) },
                            appContext,
                            aesKey,
                            dialogSettings
                        )
                        TogglableAlertDialog(dialogSettings)
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
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
fun CredentialList(credentials: List<Credential>, appContext: Context, aesKey: MutableState<String>, dialogSettings: DialogSettings) {
    Column {
        credentials.forEach { credential ->
            CredentialRow(credential, appContext, aesKey, dialogSettings)
        }
    }
}
@Composable
fun CredentialRow(credential: Credential, appContext: Context, aesKey: MutableState<String>, dialogSettings: DialogSettings) {
    Row() {
        val simplifiedName = credential.Name.replace(".txt", "")
        val assetRetriever = AssetRetriever()

        Text(
            text = simplifiedName,
            style = TextStyle( // Theoretically this could move to 'Type.kt'
                fontSize = 26.sp,
            ),
            modifier = Modifier.clickable(onClick = {
                dialogSettings.ShouldOpenDialog.value = true
                dialogSettings.DialogTitle.value = simplifiedName

                val credentialContent = assetRetriever.GetAsset(appContext, credential.Name)
                try {
                    dialogSettings.DialogContent.value =
                        credential.DecryptCredential(aesKey.value, credentialContent)
                } catch (e: Exception)
                {
                    dialogSettings.DialogContent.value = e.toString()
                }
            })
        )
    }
}

@Composable
fun TogglableAlertDialog(dialogSettings: DialogSettings) {
    if (dialogSettings.ShouldOpenDialog.value) {
        AlertDialog(
            onDismissRequest = {
                dialogSettings.ShouldOpenDialog.value = false
            },
            title = {
                Text(text = dialogSettings.DialogTitle.value)
            },
            text = {
                Text(text = dialogSettings.DialogContent.value)
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}
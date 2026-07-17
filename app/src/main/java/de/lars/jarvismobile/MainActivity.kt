package de.lars.jarvismobile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                JarvisHome(
                    onAction = { action -> executeAction(this, action) }
                )
            }
        }
    }
}

enum class JarvisAction {
    CAMPER_MODE,
    FOCUS_MODE,
    OPEN_MAPS,
    OPEN_SPOTIFY,
    APP_SETTINGS
}

data class ActionCard(
    val title: String,
    val description: String,
    val action: JarvisAction
)

@Composable
private fun JarvisHome(onAction: (JarvisAction) -> Unit) {
    val actions = listOf(
        ActionCard(
            title = "Wohnmobilmodus",
            description = "Navigation und Musik für die Fahrt vorbereiten.",
            action = JarvisAction.CAMPER_MODE
        ),
        ActionCard(
            title = "Fokusmodus",
            description = "Öffnet die Android-Einstellungen für störungsfreies Arbeiten.",
            action = JarvisAction.FOCUS_MODE
        ),
        ActionCard(
            title = "Navigation",
            description = "Öffnet eine installierte Karten-App.",
            action = JarvisAction.OPEN_MAPS
        ),
        ActionCard(
            title = "Spotify",
            description = "Startet Musik für Fahrt oder Konzentration.",
            action = JarvisAction.OPEN_SPOTIFY
        )
    )

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Jarvis Mobile", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Dein persönliches Aktionszentrum. Version 0.1",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(12.dp))
            }

            items(actions) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text(item.description)
                        Spacer(Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { onAction(item.action) }) {
                                Text("Starten")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun executeAction(context: Context, action: JarvisAction) {
    when (action) {
        JarvisAction.CAMPER_MODE -> {
            openFirstInstalledApp(
                context,
                listOf("com.here.app.maps", "com.google.android.apps.maps")
            )
        }
        JarvisAction.FOCUS_MODE -> {
            context.startActivity(Intent(Settings.ACTION_ZEN_MODE_SETTINGS))
        }
        JarvisAction.OPEN_MAPS -> {
            val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Tankstelle"))
            context.startActivity(geoIntent)
        }
        JarvisAction.OPEN_SPOTIFY -> {
            openFirstInstalledApp(context, listOf("com.spotify.music"))
        }
        JarvisAction.APP_SETTINGS -> {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    }
}

private fun openFirstInstalledApp(context: Context, packageNames: List<String>) {
    val launchIntent = packageNames
        .firstNotNullOfOrNull { context.packageManager.getLaunchIntentForPackage(it) }

    if (launchIntent != null) {
        context.startActivity(launchIntent)
    } else {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
    }
}

package com.cooper.wordle.app.ui.start

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cooper.wordle.app.R
import com.cooper.wordle.app.data.WordStore
import com.cooper.wordle.app.ui.components.WordleAppBar
import com.cooper.wordle.app.ui.theme.WordleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    onLettersPicked: (letters: WordStore.Letters) -> Unit
) {
    Scaffold(
        topBar = {
            WordleAppBar(onActionClicked = {})
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .scrollable(scrollState, orientation = Orientation.Vertical)
                .padding(vertical = 32.dp, horizontal = 16.dp)
        ) {

            Text(
                text = stringResource(id = R.string.welcome_text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(64.dp))

            WordStore.Letters.values().forEach { letters ->
                Button(onClick = { onLettersPicked(letters) }) {
                    Text(text = stringResource(id = R.string.letter_select_button, letters.size))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview("light")
@Preview("dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewStartScreen() {
    WordleTheme {
        StartScreen {

        }
    }
}
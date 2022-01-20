package com.cooper.wordle.app.ui.components

import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cooper.wordle.app.ui.home.TileState

@Composable
fun LetterTile(
    tileState: TileState,
    modifier: Modifier = Modifier
) {
    val landscape = LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(tileState.tileBackground)
            .aspectRatio(1f, landscape)
            .border(2.dp, tileState.tileBorder)
    ) {
        if (tileState.char != null) {
            val fontSize = with(LocalDensity.current) {
                maxWidth.toSp() * .8
            }
            Text(
                text = tileState.char.toString(),
                fontSize = fontSize,
                color = Color.White,
                lineHeight = fontSize,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(name = "empty tile")
@Preview(name = "empty tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewEmptyTile() {
    LetterTile(
        tileState = TileState.Empty,
        modifier = Modifier.size(128.dp)
    )
}

@Preview(name = "foo tile")
@Preview(name = "foo tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFooTile() {
    LetterTile(
        tileState = TileState.Foo('A'),
        modifier = Modifier.size(128.dp)
    )
}

@Preview(name = "absent tile")
@Preview(name = "absent tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAbsentTile() {
    LetterTile(
        tileState = TileState.Absent('A'),
        modifier = Modifier.size(128.dp)
    )
}

@Preview(name = "present tile")
@Preview(name = "present tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPresentTile() {
    LetterTile(
        tileState = TileState.Present('A'),
        modifier = Modifier.size(128.dp)
    )
}

@Preview(name = "correct tile")
@Preview(name = "correct tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCorrectTile() {
    LetterTile(
        tileState = TileState.Correct('A'),
        modifier = Modifier.size(128.dp)
    )
}
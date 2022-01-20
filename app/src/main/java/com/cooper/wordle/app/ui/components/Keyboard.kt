package com.cooper.wordle.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cooper.wordle.app.ui.theme.KeyBackground

@Composable
fun Keyboard(
    onKeyClicked: (key: Char) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        KeyboardTopRow()
        KeyboardMiddleRow()
        KeyboardBottomRow()
    }
}

@Composable
private fun KeyboardTopRow(modifier: Modifier = Modifier) {
    KeyboardRow(
        keys = listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'),
        modifier = modifier
    )
}

@Composable
private fun KeyboardMiddleRow(modifier: Modifier = Modifier) {
    KeyboardRow(
        keys = listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'),
        modifier = modifier
    )
}

@Composable
private fun KeyboardBottomRow(modifier: Modifier = Modifier) {
    KeyboardRow(
        keys = listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M'),
        modifier = modifier
    )
}

@Composable
private fun KeyboardRow(
    keys: List<Char>,
    modifier: Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        val keyModifier = Modifier.weight(1f)
        keys.forEach { key ->
            Key(key, keyModifier)
        }
    }
}

@Composable
private fun Key(
    key: Char,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(KeyBackground)
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(16.dp))
    ) {
        val contentColor = contentColorFor(backgroundColor = KeyBackground)
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            val fontSize = with(LocalDensity.current) {
                maxWidth.toSp() * .8
            }
            Text(
                text = key.toString(),
                fontSize = fontSize,
                lineHeight = fontSize,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewKey() {
    Key(key = 'A', modifier = Modifier.height(100.dp))
}

@Preview
@Composable
private fun PreviewKeyboard() {
    Keyboard({

    })
}
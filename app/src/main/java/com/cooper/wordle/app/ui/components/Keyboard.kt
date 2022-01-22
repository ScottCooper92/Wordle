package com.cooper.wordle.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cooper.wordle.app.ui.theme.KeyBackground

private sealed class Key(val aspectRatio: Float) {
    data class IconKey(val icon: ImageVector) : Key(65f / 58)
    data class CharKey(val char: Char) : Key(65f / 58)
    data class StringKey(val string: String) : Key(65f / 58)
}

private val firstRow = listOf(
    Key.CharKey('Q'),
    Key.CharKey('W'),
    Key.CharKey('E'),
    Key.CharKey('R'),
    Key.CharKey('T'),
    Key.CharKey('Y'),
    Key.CharKey('U'),
    Key.CharKey('I'),
    Key.CharKey('O'),
    Key.CharKey('P'),
)

private val secondRow = listOf(
    Key.CharKey('A'),
    Key.CharKey('S'),
    Key.CharKey('D'),
    Key.CharKey('F'),
    Key.CharKey('G'),
    Key.CharKey('H'),
    Key.CharKey('J'),
    Key.CharKey('K'),
    Key.CharKey('L'),
)

private val thirdRow = listOf(
    Key.StringKey("ENTER"),
    Key.CharKey('Z'),
    Key.CharKey('X'),
    Key.CharKey('C'),
    Key.CharKey('V'),
    Key.CharKey('B'),
    Key.CharKey('N'),
    Key.CharKey('M'),
    Key.IconKey(Icons.Default.KeyboardBackspace)
)

@Composable
fun Keyboard(
    onKeyClicked: (key: Char) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        val rowModifier = Modifier.weight(1f)
        KeyboardRow(keys = firstRow, rowModifier)
        KeyboardRow(keys = secondRow, rowModifier)
        KeyboardRow(keys = thirdRow, rowModifier)
    }
}

@Composable
private fun KeyboardRow(
    keys: List<Key>,
    modifier: Modifier = Modifier,
    keyModifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        modifier = modifier.fillMaxWidth()
    ) {
        keys.forEach { key ->
            Key(key, Modifier.weight(1f))
        }
    }
}

@Composable
private fun Key(
    key: Key,
    modifier: Modifier = Modifier
) {
    when (key) {
        is Key.CharKey -> CharKey(key, modifier)
        is Key.IconKey -> IconKey(key, modifier)
        is Key.StringKey -> StringKey(key, modifier)
    }
}

@Composable
private fun CharKey(
    key: Key.CharKey,
    modifier: Modifier = Modifier
) {
    TextKey(text = key.char.toString(), modifier.aspectRatio(key.aspectRatio, true))
}

@Composable
private fun StringKey(
    key: Key.StringKey,
    modifier: Modifier = Modifier
) {
    TextKey(text = key.string, modifier.aspectRatio(key.aspectRatio, true))
}

@Composable
private fun IconKey(
    key: Key.IconKey,
    modifier: Modifier = Modifier
) {
    BaseKey(modifier.aspectRatio(key.aspectRatio,true)) {
        Icon(key.icon, "", tint = Color.White, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun BaseKey(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        color = KeyBackground,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        content = content
    )
}

@Composable
private fun TextKey(text: String, modifier: Modifier = Modifier, scaleFactor: Float = .8f) {
    BaseKey(modifier) {
        BoxWithConstraints(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
            val fontSize = with(LocalDensity.current) {
                val dimension = if (maxHeight > maxWidth) maxWidth else maxHeight
                dimension.toSp() * scaleFactor
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = fontSize,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Preview
@Composable
private fun PreviewCharKey() {
    Key(key = Key.CharKey('A'), modifier = Modifier.height(58.dp))
}

@Preview
@Composable
private fun PreviewStringKey() {
    Key(key = Key.StringKey("ENTER"), modifier = Modifier.height(58.dp))
}

@Preview
@Composable
private fun PreviewIconKey() {
    Key(key = Key.IconKey(Icons.Default.KeyboardBackspace), modifier = Modifier.height(58.dp))
}

@Preview
@Composable
private fun PreviewKeyboard() {
    Keyboard({

    }, modifier = Modifier.height(150.dp))
}

package com.cooper.wordle.app.ui.components

import android.content.res.Configuration
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cooper.wordle.app.data.TileState
import com.cooper.wordle.app.ui.theme.WordleTheme

@Composable
fun LetterTile(
    tileState: TileState,
    modifier: Modifier = Modifier
) {
    Surface(
        color = tileState.tileBackground,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .border(2.dp, tileState.tileBorder)
    ) {

        val color = LocalContentColor.current

        // we're using a TextView with autoSize until similar functionality is present in compose
        //TODO - Replace this when compose has this functionality and reduce minSdk back to 21
        AndroidView(factory = { context ->
            TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setAutoSizeTextTypeWithDefaults(AUTO_SIZE_TEXT_TYPE_UNIFORM)
                text = tileState.char?.toString()
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                typeface = Typeface.MONOSPACE
            }
        }, update = { textView ->
            textView.apply {
                textView.setTextColor(color.toArgb())
                text = tileState.char?.toString()
            }
        })
    }
}

@Preview(name = "empty tile")
@Preview(name = "empty tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewEmptyTile() {
    WordleTheme {
        LetterTile(
            tileState = TileState.Empty,
            modifier = Modifier.size(128.dp)
        )
    }
}

@Preview(name = "foo tile")
@Preview(name = "foo tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewFooTile() {
    WordleTheme {
        LetterTile(
            tileState = TileState.Foo('A'),
            modifier = Modifier.size(128.dp)
        )
    }
}

@Preview(name = "absent tile")
@Preview(name = "absent tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAbsentTile() {
    WordleTheme {
        LetterTile(
            tileState = TileState.Absent('A'),
            modifier = Modifier.size(128.dp)
        )
    }
}

@Preview(name = "present tile")
@Preview(name = "present tile dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPresentTile() {
    WordleTheme {
        LetterTile(
            tileState = TileState.Present('A'),
            modifier = Modifier.size(128.dp)
        )
    }
}

@Preview(name = "correct tile")
@Preview(
    name = "correct tile dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewCorrectTile() {
    WordleTheme {
        LetterTile(
            tileState = TileState.Correct('A'),
            modifier = Modifier.size(128.dp)
        )
    }
}
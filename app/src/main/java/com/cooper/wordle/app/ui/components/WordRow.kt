package com.cooper.wordle.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cooper.wordle.app.ui.game.Row
import com.cooper.wordle.app.ui.game.TileState

@Composable
fun WordRow(row: Row, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
    ) {
        val tileModifier = Modifier.weight(1f)
        row.tileStates.forEach { tileState ->
            LetterTile(tileState, tileModifier)
        }
    }
}

@Preview("empty word")
@Composable
fun PreviewEmptyWordRow() {
    val tileState = TileState.Empty
    val wordState = Row(listOf(tileState, tileState, tileState, tileState, tileState))
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("partial word")
@Composable
fun PreviewPartialWordRow() {
    val wordState = Row(
        listOf(
            TileState.Foo('P'),
            TileState.Foo('A'),
            TileState.Empty,
            TileState.Empty,
            TileState.Empty,
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("full word")
@Composable
fun PreviewFullWordRow() {
    val wordState = Row(
        listOf(
            TileState.Foo('P'),
            TileState.Foo('A'),
            TileState.Foo('R'),
            TileState.Foo('T'),
            TileState.Foo('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("absent word")
@Composable
fun PreviewAbsentWordRow() {
    val wordState = Row(
        listOf(
            TileState.Absent('P'),
            TileState.Absent('A'),
            TileState.Absent('R'),
            TileState.Absent('T'),
            TileState.Absent('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("partially present word")
@Composable
fun PreviewPartiallyPresentWordRow() {
    val wordState = Row(
        listOf(
            TileState.Absent('P'),
            TileState.Present('A'),
            TileState.Absent('R'),
            TileState.Present('T'),
            TileState.Absent('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("partially correct word")
@Composable
fun PreviewPartiallyCorrectWordRow() {
    val wordState = Row(
        listOf(
            TileState.Absent('P'),
            TileState.Present('A'),
            TileState.Correct('R'),
            TileState.Present('T'),
            TileState.Correct('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("correct word")
@Composable
fun PreviewCorrectWordRow() {
    val wordState = Row(
        listOf(
            TileState.Correct('P'),
            TileState.Correct('A'),
            TileState.Correct('R'),
            TileState.Correct('T'),
            TileState.Correct('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview
@Composable
private fun PreviewRowSize() {
    val tileState = TileState.Empty
    val wordState = Row(
        listOf(tileState, tileState, tileState, tileState, tileState)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.size(900.dp, 1600.dp)
    ) {
        WordRow(wordState)
    }
}
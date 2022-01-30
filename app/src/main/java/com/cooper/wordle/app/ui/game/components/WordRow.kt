package com.cooper.wordle.app.ui.game.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cooper.wordle.game.*

@Composable
fun WordRow(row: GridRow, modifier: Modifier = Modifier) {
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
    val tileState = EmptyLetter
    val wordState = GridRow(listOf(tileState, tileState, tileState, tileState, tileState))
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("partial word")
@Composable
fun PreviewPartialWordRow() {
    val wordState = GridRow(
        listOf(
            ProspectiveLetter('P'),
            ProspectiveLetter('A'),
            EmptyLetter,
            EmptyLetter,
            EmptyLetter,
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("full word")
@Composable
fun PreviewFullWordRow() {
    val wordState = GridRow(
        listOf(
            ProspectiveLetter('P'),
            ProspectiveLetter('A'),
            ProspectiveLetter('R'),
            ProspectiveLetter('T'),
            ProspectiveLetter('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("absent word")
@Composable
fun PreviewAbsentWordRow() {
    val wordState = GridRow(
        listOf(
            AbsentLetter('P'),
            AbsentLetter('A'),
            AbsentLetter('R'),
            AbsentLetter('T'),
            AbsentLetter('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("partially present word")
@Composable
fun PreviewPartiallyPresentWordRow() {
    val wordState = GridRow(
        listOf(
            AbsentLetter('P'),
            PresentLetter('A'),
            AbsentLetter('R'),
            PresentLetter('T'),
            AbsentLetter('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("partially correct word")
@Composable
fun PreviewPartiallyCorrectWordRow() {
    val wordState = GridRow(
        listOf(
            AbsentLetter('P'),
            PresentLetter('A'),
            CorrectLetter('R'),
            PresentLetter('T'),
            CorrectLetter('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview("correct word")
@Composable
fun PreviewCorrectWordRow() {
    val wordState = GridRow(
        listOf(
            CorrectLetter('P'),
            CorrectLetter('A'),
            CorrectLetter('R'),
            CorrectLetter('T'),
            CorrectLetter('Y'),
        )
    )
    WordRow(wordState, Modifier.height(100.dp))
}

@Preview
@Composable
private fun PreviewRowSize() {
    val tileState = EmptyLetter
    val wordState = GridRow(
        listOf(tileState, tileState, tileState, tileState, tileState)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.size(900.dp, 1600.dp)
    ) {
        WordRow(wordState)
    }
}
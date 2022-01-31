package com.cooper.wordle.app.ui.game.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Done
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
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope.HorizontalAnchor
import androidx.constraintlayout.compose.Dimension
import com.cooper.wordle.app.ui.theme.KeyBackground
import com.cooper.wordle.game.EvaluatedLetterState

sealed class Key(val aspectRatio: String = "7:12") {
    sealed class IconKey(val icon: ImageVector) : Key(aspectRatio = "1:1.13")
    object SUBMIT : IconKey(Icons.Default.Done)
    object DELETE : IconKey(Icons.Default.Backspace)

    data class CharKey(val char: Char) : Key()
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
    Key.SUBMIT,
    Key.CharKey('Z'),
    Key.CharKey('X'),
    Key.CharKey('C'),
    Key.CharKey('V'),
    Key.CharKey('B'),
    Key.CharKey('N'),
    Key.CharKey('M'),
    Key.DELETE
)

private val rows = listOf(firstRow, secondRow, thirdRow)

@Composable
fun Keyboard(
    usedLetters: Set<EvaluatedLetterState>,
    onKeyClicked: (key: Key) -> Unit,
    modifier: Modifier
) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {

        val guidelines = mutableListOf<HorizontalAnchor>()
        val step = 1f / rows.size
        for (i in 0..(rows.size + 1)) {
            guidelines.add(createGuidelineFromTop(step * i))
        }

        rows.forEachIndexed { index, row ->
            val topAnchor = guidelines[index]
            val bottomAnchor = guidelines[index + 1]

            val refs = arrayListOf<ConstrainedLayoutReference>()

            row.forEach { key ->
                val ref = createRef().also { refs.add(it) }

                // change the color of the background if this letter has been used
                var backgroundColor = KeyBackground
                if (key is Key.CharKey) {
                    val letterState = usedLetters.firstOrNull { it.char == key.char }
                    backgroundColor = LetterStyleHelper.getKeyStyle(letterState).background
                }

                Key(key, backgroundColor, onKeyClicked, Modifier
                    .defaultMinSize(10.dp)
                    .padding(3.dp)
                    .constrainAs(ref) {
                        linkTo(topAnchor, bottomAnchor)
                        horizontalChainWeight = if (key is Key.CharKey) 1f else 1.5f
                        width = Dimension.ratio(key.aspectRatio)
                        height = Dimension.fillToConstraints
                    }
                )
            }

            createHorizontalChain(*refs.toTypedArray(), chainStyle = ChainStyle.Packed)
        }
    }
}

@Composable
private fun Key(
    key: Key,
    color: Color,
    onKeyClicked: (key: Key) -> Unit,
    modifier: Modifier = Modifier
) {
    when (key) {
        is Key.CharKey -> CharKey(key, color, onKeyClicked, modifier)
        is Key.IconKey -> IconKey(key, color, onKeyClicked, modifier)
    }
}

@Composable
private fun CharKey(
    key: Key.CharKey,
    color: Color,
    onKeyClicked: (key: Key) -> Unit,
    modifier: Modifier = Modifier
) {
    TextKey(key, color, onKeyClicked, modifier)
}

@Composable
private fun IconKey(
    key: Key.IconKey,
    color: Color,
    onKeyClicked: (key: Key) -> Unit,
    modifier: Modifier = Modifier
) {
    BaseKey(key, color, onKeyClicked, modifier) {
        Icon(key.icon, "", tint = Color.White, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun BaseKey(
    key: Key,
    color: Color,
    onKeyClicked: (key: Key) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.clickable { onKeyClicked(key) },
        content = content
    )
}

@Composable
private fun TextKey(
    key: Key.CharKey,
    color: Color,
    onKeyClicked: (key: Key) -> Unit,
    modifier: Modifier = Modifier,
    scaleFactor: Float = .8f
) {
    BaseKey(key, color, onKeyClicked, modifier) {
        BoxWithConstraints(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
            val fontSize = with(LocalDensity.current) {
                val dimension = if (maxHeight > maxWidth) maxWidth else maxHeight
                dimension.toSp() * scaleFactor
            }
            Text(
                text = key.char.toString(),
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
    Key(
        key = Key.CharKey('A'),
        color = KeyBackground,
        modifier = Modifier.height(58.dp),
        onKeyClicked = { })
}

@Preview
@Composable
private fun PreviewIconKey() {
    Key(
        key = Key.SUBMIT,
        color = KeyBackground,
        modifier = Modifier.height(58.dp),
        onKeyClicked = { })
}

@Preview
@Composable
private fun PreviewKeyboard() {
    Keyboard(
        usedLetters = emptySet(),
        onKeyClicked = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

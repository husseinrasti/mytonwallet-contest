package com.husseinrasti.app.feature.create.ui.phrase.test

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.husseinrasti.app.core.navigation.NavigateUp
import com.husseinrasti.app.core.navigation.NavigationEvent
import com.husseinrasti.app.component.ui.MyTonWalletButton
import com.husseinrasti.app.component.ui.MyTonWalletLottieAnimation
import com.husseinrasti.app.component.ui.MyTonWalletSurface
import com.husseinrasti.app.component.ui.MyTonWalletTextField
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.feature.create.ui.R
import com.husseinrasti.app.feature.create.ui.phrase.model.EditTextState

@Composable
internal fun TestPhraseRoute(
    modifier: Modifier = Modifier,
    onClickNavigation: (NavigationEvent) -> Unit,
    viewModel: TestPhraseViewModel = hiltViewModel(),
) {
    val randomNumbers by viewModel.randomNumbers.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED
    )

    val autocompleteState by viewModel.autocompletePhrases.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED
    )

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        TestPhraseAlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            onClickSeeWord = {
                showDialog = false
                onClickNavigation(NavigateUp)
            },
            onClickTryAgain = {
                showDialog = false
            }
        )
    }

    if (randomNumbers.size == COUNT_PHRASE_TEST) {
        TestPhraseScaffoldScreen(
            onClickNavigation = onClickNavigation,
            modifier = modifier,
            randomNumbers = randomNumbers,
            editTextStates = viewModel.editTextStates,
            updateEditTextState = viewModel::updateEditTextState,
            autocompleteState = autocompleteState,
            onClickContinue = {
                showDialog = true
            }
        )
    } else {
        onClickNavigation(NavigateUp)
    }
}


@Composable
private fun TestPhraseScaffoldScreen(
    modifier: Modifier = Modifier,
    onClickNavigation: (NavigationEvent) -> Unit,
    randomNumbers: Set<Int>,
    editTextStates: List<EditTextState>,
    updateEditTextState: (Int, String) -> Unit,
    autocompleteState: TestPhraseUiState,
    onClickContinue: () -> Unit,
) {
    Scaffold(
        topBar = {
            MyTonWalletTopAppBar(
                onClickNavigation = { onClickNavigation(NavigateUp) },
                elevation = 0.dp
            )
        },
        content = {
            TestPhraseScreen(
                modifier = modifier.padding(it),
                onClickContinue = onClickContinue,
                randomNumbers = randomNumbers,
                editTextStates = editTextStates,
                updateEditTextState = updateEditTextState,
                autocompleteState = autocompleteState,
            )
        }
    )
}

@Composable
fun TestPhraseScreen(
    modifier: Modifier,
    randomNumbers: Set<Int>,
    editTextStates: List<EditTextState>,
    updateEditTextState: (Int, String) -> Unit,
    autocompleteState: TestPhraseUiState,
    onClickContinue: () -> Unit,
) {

    val num1 = randomNumbers.elementAt(0)
    val num2 = randomNumbers.elementAt(1)
    val num3 = randomNumbers.elementAt(2)

    MyTonWalletSurface(
        modifier = modifier,
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            item {
                MyTonWalletLottieAnimation(
                    lottieCompositionSpec = LottieCompositionSpec.Asset("anim/test_time.json"),
                    modifier = Modifier.size(128.dp),
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(id = R.string.title_test_time),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.body1.copy(fontSize = 24.sp),
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(
                        id = R.string.format_msg_test_time_to_enter_words,
                        num1,
                        num2,
                        num3
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.caption,
                )
            }

            item {
                Spacer(Modifier.height(32.dp))
                AutoCompleteTextField(
                    autocompleteState = autocompleteState,
                    editTextStates = editTextStates,
                    updateEditTextState = updateEditTextState,
                    num1 = num1,
                    num2 = num2,
                    num3 = num3,
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
                MyTonWalletButton(
                    text = stringResource(id = R.string.btn_continue),
                    onClick = onClickContinue,
                )
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AutoCompleteTextField(
    editTextStates: List<EditTextState>,
    updateEditTextState: (Int, String) -> Unit,
    autocompleteState: TestPhraseUiState,
    num1: Int,
    num2: Int,
    num3: Int,
) {
    val (focusNum1, focusNum2, focusNum3) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current

    var autoCompleteState1 by remember { mutableStateOf(false) }
    var autoCompleteState2 by remember { mutableStateOf(false) }
    var autoCompleteState3 by remember { mutableStateOf(false) }

    ConstraintLayout {
        val (textFieldOne, textFieldTwo, textFieldThree,
            autocomplete1, autocomplete2, autocomplete3) = createRefs()

        if (autocompleteState is TestPhraseUiState.Success &&
            autocompleteState.index == INDEX_ZERO &&
            autocompleteState.phrases.isNotEmpty() && autoCompleteState1
        ) {
            AutoCompletePhrases(
                modifier = Modifier
                    .constrainAs(autocomplete1) {
                        bottom.linkTo(textFieldOne.top)
                        end.linkTo(textFieldOne.end)
                        start.linkTo(textFieldOne.start)
                        width = Dimension.fillToConstraints
                    },
                phrases = autocompleteState.phrases,
                updateEditTextState = { index, phrase ->
                    autoCompleteState1 = false
                    updateEditTextState(index, phrase)
                },
                indexTextField = INDEX_ZERO,
            )
        }
        MyTonWalletTextField(
            modifier = Modifier
                .focusRequester(focusNum1)
                .constrainAs(textFieldOne) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            value = editTextStates[INDEX_ZERO].text,
            singleLine = true,
            onValueChange = { newText ->
                autoCompleteState1 = true
                updateEditTextState(INDEX_ZERO, newText)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusNum2.requestFocus()
            }),
            label = { Text(text = "$num1:") },
        )
        Spacer(Modifier.height(8.dp))

        if (autocompleteState is TestPhraseUiState.Success &&
            autocompleteState.index == INDEX_ONE &&
            autocompleteState.phrases.isNotEmpty() && autoCompleteState2
        ) {
            AutoCompletePhrases(
                modifier = Modifier.constrainAs(autocomplete2) {
                    bottom.linkTo(textFieldTwo.top)
                    end.linkTo(textFieldTwo.end)
                    start.linkTo(textFieldTwo.start)
                    width = Dimension.fillToConstraints
                },
                phrases = autocompleteState.phrases,
                updateEditTextState = { index, phrase ->
                    autoCompleteState2 = false
                    updateEditTextState(index, phrase)
                },
                indexTextField = INDEX_ONE,
            )
        }
        MyTonWalletTextField(
            modifier = Modifier
                .focusRequester(focusNum2)
                .constrainAs(textFieldTwo) {
                    top.linkTo(textFieldOne.bottom, margin = 8.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                },
            value = editTextStates[INDEX_ONE].text,
            singleLine = true,
            onValueChange = { newText ->
                autoCompleteState2 = true
                updateEditTextState(INDEX_ONE, newText)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {
                focusNum3.requestFocus()
            }),
            label = { Text(text = "$num2:") },
        )
        Spacer(Modifier.height(8.dp))

        if (autocompleteState is TestPhraseUiState.Success &&
            autocompleteState.index == INDEX_TWO &&
            autocompleteState.phrases.isNotEmpty() && autoCompleteState3
        ) {
            AutoCompletePhrases(
                modifier = Modifier.constrainAs(autocomplete3) {
                    bottom.linkTo(textFieldThree.top)
                    end.linkTo(textFieldThree.end)
                    start.linkTo(textFieldThree.start)
                    width = Dimension.fillToConstraints
                },
                phrases = autocompleteState.phrases,
                updateEditTextState = { index, phrase ->
                    autoCompleteState3 = false
                    updateEditTextState(index, phrase)
                },
                indexTextField = INDEX_TWO,
            )
        }
        MyTonWalletTextField(
            modifier = Modifier
                .focusRequester(focusNum3)
                .constrainAs(textFieldThree) {
                    top.linkTo(textFieldTwo.bottom, margin = 8.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                },
            value = editTextStates[INDEX_TWO].text,
            singleLine = true,
            onValueChange = { newText ->
                autoCompleteState3 = true
                updateEditTextState(INDEX_TWO, newText)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            label = { Text(text = "$num3:") },
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun AutoCompletePhrases(
    modifier: Modifier,
    phrases: List<String>,
    updateEditTextState: (Int, String) -> Unit,
    indexTextField: Int,
) {
    val scrollState: ScrollState = rememberScrollState(0)

    MyTonWalletSurface(
        modifier = modifier
            .wrapContentHeight(),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.horizontalScroll(scrollState)
        ) {
            repeat(phrases.size) { index ->
                TextButton(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    onClick = { updateEditTextState(indexTextField, phrases[index]) }
                ) {
                    Text(
                        text = phrases[index],
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
    }
}


@Composable
private fun TestPhraseAlertDialog(
    onDismissRequest: () -> Unit,
    onClickSeeWord: () -> Unit,
    onClickTryAgain: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClickSeeWord) {
                    Text(
                        text = stringResource(id = R.string.btn_see_words),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colors.secondaryVariant,
                        style = MaterialTheme.typography.button,
                    )
                }
                TextButton(onClick = onClickTryAgain) {
                    Text(
                        text = stringResource(id = R.string.btn_try_again),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colors.secondaryVariant,
                        style = MaterialTheme.typography.button,
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.title_incorrect_words),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.msg_incorrect_words),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface,
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
    )
}


@Preview
@Composable
private fun TestPhrasePreview() {
    MyTonWalletContestTheme {
        TestPhraseScaffoldScreen(
            onClickNavigation = {},
            randomNumbers = setOf(12, 3, 18),
            editTextStates = listOf(
                EditTextState(),
                EditTextState(),
                EditTextState(),
            ),
            updateEditTextState = { _, _ -> },
            autocompleteState = TestPhraseUiState.Success(
                INDEX_ONE, listOf(
                    "abandon",
                    "ability",
                    "able",
                    "about",
                    "above",
                    "absent",
                    "absorb",
                    "abstract",
                    "absurd",
                    "abuse",
                )
            ),
            onClickContinue = {}
        )
    }
}

@Preview
@Composable
private fun TestPhraseAlertDialogPreview() {
    MyTonWalletContestTheme {
        TestPhraseAlertDialog(
            onDismissRequest = {},
            onClickSeeWord = {},
            onClickTryAgain = {}
        )
    }
}
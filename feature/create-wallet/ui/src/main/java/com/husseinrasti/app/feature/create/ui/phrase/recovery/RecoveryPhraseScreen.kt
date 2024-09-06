package com.husseinrasti.app.feature.create.ui.phrase.recovery

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.husseinrasti.app.component.navigation.NavigateToMain
import com.husseinrasti.app.component.navigation.NavigateUp
import com.husseinrasti.app.component.navigation.NavigationEvent
import com.husseinrasti.app.component.theme.MyTonWalletContestTheme
import com.husseinrasti.app.component.ui.MyTonWalletButton
import com.husseinrasti.app.component.ui.MyTonWalletSurface
import com.husseinrasti.app.component.ui.MyTonWalletTextField
import com.husseinrasti.app.component.ui.MyTonWalletTopAppBar
import com.husseinrasti.app.feature.create.ui.R
import com.husseinrasti.app.feature.create.ui.phrase.model.EditTextState

@Composable
internal fun RecoveryPhraseRoute(
    modifier: Modifier = Modifier,
    onClickNavigation: (NavigationEvent) -> Unit,
    viewModel: RecoveryPhraseViewModel = hiltViewModel(),
) {
    var showDialog by remember { mutableStateOf(false) }

    val autocompletePhraseUiState by viewModel.autocompletePhrases.collectAsStateWithLifecycle()

    val recoveryPhraseUiState by viewModel.recoveryUiState.collectAsStateWithLifecycle(
        initialValue = null,
    )

    if (showDialog) {
        RecoveryPhraseAlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            onClickClose = {
                showDialog = false
            }
        )
    }

    when (recoveryPhraseUiState) {
        RecoveryPhraseUiState.Error -> {
            showDialog = true
            viewModel.setRecoveryUiStateNull()
        }

        RecoveryPhraseUiState.Success -> onClickNavigation(NavigateToMain)
        else -> {}
    }

    RecoveryPhraseScaffoldScreen(
        onClickNavigation = onClickNavigation,
        modifier = modifier,
        editTextStates = viewModel.editTextStates,
        updateEditTextState = viewModel::updateEditTextState,
        autocompletePhraseUiState = autocompletePhraseUiState,
        onClickContinue = { viewModel.checkPhrase() }
    )
}


@Composable
private fun RecoveryPhraseScaffoldScreen(
    modifier: Modifier = Modifier,
    onClickNavigation: (NavigationEvent) -> Unit,
    editTextStates: List<EditTextState>,
    updateEditTextState: (Int, String) -> Unit,
    autocompletePhraseUiState: AutocompletePhraseUiState,
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
            RecoveryPhraseScreen(
                modifier = modifier.padding(it),
                onClickContinue = onClickContinue,
                editTextStates = editTextStates,
                updateEditTextState = updateEditTextState,
                autocompletePhraseUiState = autocompletePhraseUiState,
            )
        }
    )
}

@Composable
fun RecoveryPhraseScreen(
    modifier: Modifier,
    editTextStates: List<EditTextState>,
    updateEditTextState: (Int, String) -> Unit,
    autocompletePhraseUiState: AutocompletePhraseUiState,
    onClickContinue: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequesters = mutableListOf<FocusRequester>().apply {
        repeat(COUNT_PHRASE) { add(FocusRequester()) }
    }
    val autoCompleteStates = mutableListOf<Boolean>().apply {
        repeat(COUNT_PHRASE) { add(false) }
    }.toMutableStateList()

    MyTonWalletSurface(
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            item {
                Text(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 16.dp)),
                    text = stringResource(id = R.string.title_import_wallet),
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
                    text = stringResource(R.string.desc_import_wallet),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.caption,
                )
                Spacer(Modifier.height(24.dp))
            }

            items(count = COUNT_PHRASE) { num ->
                ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                    val (autocomplete, input) = createRefs()

                    if (autocompletePhraseUiState is AutocompletePhraseUiState.Success &&
                        autocompletePhraseUiState.index == num &&
                        autocompletePhraseUiState.phrases.isNotEmpty() && autoCompleteStates[num]
                    ) {
                        AutoCompletePhrases(
                            modifier = Modifier.constrainAs(autocomplete) {
                                bottom.linkTo(parent.top)
                                end.linkTo(parent.end)
                                start.linkTo(parent.start)
                                bottom.linkTo(input.top)
                                width = Dimension.fillToConstraints
                            },
                            phrases = autocompletePhraseUiState.phrases,
                            updateEditTextState = { index, phrase ->
                                autoCompleteStates[num] = false
                                updateEditTextState(index, phrase)
                            },
                            indexTextField = num,
                        )
                    }
                    MyTonWalletTextField(
                        modifier = Modifier
                            .focusRequester(focusRequesters[num])
                            .constrainAs(input) {
                                bottom.linkTo(parent.top)
                                end.linkTo(parent.end)
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                            },
                        value = editTextStates[num].text,
                        singleLine = true,
                        onValueChange = { newText ->
                            autoCompleteStates[num] = true
                            updateEditTextState(num, newText)
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = if (num + 1 == COUNT_PHRASE) ImeAction.Done
                            else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            if (num + 1 == COUNT_PHRASE) {
                                keyboardController?.hide()
                            } else {
                                focusRequesters[num + 1].requestFocus()
                            }
                        }),
                        label = { Text(text = "${num + 1}:") }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Spacer(Modifier.height(24.dp))
                MyTonWalletButton(
                    text = stringResource(id = R.string.btn_continue),
                    onClick = onClickContinue,
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RecoveryPhraseAlertDialog(
    onDismissRequest: () -> Unit,
    onClickClose: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClickClose) {
                    Text(
                        text = stringResource(id = R.string.btn_close),
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
                text = stringResource(id = R.string.title_wrong_phrase),
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.body1.copy(fontSize = 20.sp),
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.msg_wrong_phrase),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface,
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
    )
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

@Preview
@Composable
private fun RecoveryPhrasePreview() {
    MyTonWalletContestTheme {
        RecoveryPhraseScaffoldScreen(
            onClickNavigation = {},
            editTextStates = mutableListOf<EditTextState>().apply {
                repeat(COUNT_PHRASE) {
                    add(EditTextState())
                }
            },
            updateEditTextState = { _, _ -> },
            autocompletePhraseUiState = AutocompletePhraseUiState.Success(
                1, listOf(
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
private fun RecoveryPhraseAlertDialogPreview() {
    MyTonWalletContestTheme {
        RecoveryPhraseAlertDialog(
            onDismissRequest = {},
            onClickClose = {}
        )
    }
}
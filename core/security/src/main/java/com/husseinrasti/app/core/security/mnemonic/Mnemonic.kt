package com.husseinrasti.app.core.security.mnemonic

import com.husseinrasti.app.core.security.SecureRandom
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

private val MnemonicGeneratorCoroutineName = CoroutineName("mnemonic-generator")

object Mnemonic {

    private const val DEFAULT_WORD_COUNT: Int = 24

    private fun mnemonicWords(): List<String> = MNEMONIC_WORD_LIST

    suspend fun generate(
        dispatcher: CoroutineContext,
        wordCount: Int = DEFAULT_WORD_COUNT,
        random: Random = SecureRandom
    ): List<String> = withContext(dispatcher + MnemonicGeneratorCoroutineName) {
        val wordlist: List<String> = mnemonicWords()
        val mnemonic = Array(wordCount) { "" }
        val weakRandom = Random(random.nextInt())
        repeat(wordCount) { i ->
            mnemonic[i] = wordlist.random(weakRandom)
        }
        mnemonic.toList()
    }

}
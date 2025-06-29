package com.example.myllmapp

import android.content.Context
import org.json.JSONObject

class SimpleTokenizer(context: Context) {
    private val vocab: Map<String, Int>

    init {
        val jsonString = context.assets.open("vocab.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val tempVocab = mutableMapOf<String, Int>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            tempVocab[key] = jsonObject.getInt(key)
        }
        vocab = tempVocab
    }

    fun tokenize(text: String): List<Int> {
        // Very simple whitespace tokenizer + vocab lookup
        val tokens = mutableListOf<Int>()
        val words = text.lowercase().split(" ")
        for (word in words) {
            val tokenId = vocab[word] ?: vocab["<unk>"] ?: 0
            tokens.add(tokenId)
        }
        return tokens
    }
}

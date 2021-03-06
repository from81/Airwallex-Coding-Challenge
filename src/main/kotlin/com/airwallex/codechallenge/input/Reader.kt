package com.airwallex.codechallenge.input

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.stream.Stream

class cReader {

    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())

    fun read(lines: Stream<String>): Stream<CurrencyConversionRate> =
            lines.map {
                mapper.readValue<CurrencyConversionRate>(it)
            }

}
package br.com.mrocigno.bigbrother.core

import br.com.mrocigno.bigbrother.core.interceptor.BigBrotherOkHttpInterceptor

@Suppress("FunctionName")
@Deprecated("Use BigBrotherOkHttpInterceptor with Ktor HttpClient", ReplaceWith("BigBrotherOkHttpInterceptor"))
fun BigBrotherInterceptor(vararg blockList: String) =
    BigBrotherOkHttpInterceptor(*blockList)
package com.callnote.app.di

import javax.inject.Qualifier

/** 应用级协程作用域 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

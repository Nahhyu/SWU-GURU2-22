package com.example.hobbymate.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Repository는 constructor injection으로 제공됩니다.
 * 인터페이스 구현을 분리할 때 이 모듈에 @Binds 선언을 추가하세요.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule

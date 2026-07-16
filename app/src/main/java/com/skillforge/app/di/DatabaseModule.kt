package com.skillforge.app.di

import android.content.Context
import com.skillforge.app.data.local.SkillForgeDatabase
import com.skillforge.app.data.local.dao.AchievementDao
import com.skillforge.app.data.local.dao.ChallengeDao
import com.skillforge.app.data.local.dao.DailyChallengeDao
import com.skillforge.app.data.local.dao.SkillDao
import com.skillforge.app.data.local.dao.UserDao
import com.skillforge.app.data.local.dao.UserProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SkillForgeDatabase {
        return SkillForgeDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserDao(database: SkillForgeDatabase): UserDao = database.userDao()

    @Provides
    fun provideSkillDao(database: SkillForgeDatabase): SkillDao = database.skillDao()

    @Provides
    fun provideChallengeDao(database: SkillForgeDatabase): ChallengeDao = database.challengeDao()

    @Provides
    fun provideUserProgressDao(database: SkillForgeDatabase): UserProgressDao = database.userProgressDao()

    @Provides
    fun provideAchievementDao(database: SkillForgeDatabase): AchievementDao = database.achievementDao()

    @Provides
    fun provideDailyChallengeDao(database: SkillForgeDatabase): DailyChallengeDao = database.dailyChallengeDao()
}

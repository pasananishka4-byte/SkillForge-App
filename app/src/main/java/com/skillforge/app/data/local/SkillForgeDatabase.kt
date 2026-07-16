package com.skillforge.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skillforge.app.data.local.dao.AchievementDao
import com.skillforge.app.data.local.dao.ChallengeDao
import com.skillforge.app.data.local.dao.DailyChallengeDao
import com.skillforge.app.data.local.dao.SkillDao
import com.skillforge.app.data.local.dao.UserDao
import com.skillforge.app.data.local.dao.UserProgressDao
import com.skillforge.app.data.local.entity.AchievementEntity
import com.skillforge.app.data.local.entity.ChallengeEntity
import com.skillforge.app.data.local.entity.DailyChallengeEntity
import com.skillforge.app.data.local.entity.SkillEntity
import com.skillforge.app.data.local.entity.UserAchievementEntity
import com.skillforge.app.data.local.entity.UserEntity
import com.skillforge.app.data.local.entity.UserProgressEntity
import com.skillforge.app.data.local.seed.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Database(
    entities = [
        UserEntity::class,
        SkillEntity::class,
        ChallengeEntity::class,
        UserProgressEntity::class,
        AchievementEntity::class,
        UserAchievementEntity::class,
        DailyChallengeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SkillForgeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun skillDao(): SkillDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun achievementDao(): AchievementDao
    abstract fun dailyChallengeDao(): DailyChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: SkillForgeDatabase? = null
        private var seedingLatch = CountDownLatch(1)

        fun getDatabase(context: Context): SkillForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val alreadyCreated = INSTANCE != null
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SkillForgeDatabase::class.java,
                    "skillforge_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    SeedData.seed(db)
                                } finally {
                                    seedingLatch.countDown()
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            if (seedingLatch.count > 0) {
                                seedingLatch.countDown()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun awaitSeeding() {
            try {
                seedingLatch.await(5, TimeUnit.SECONDS)
            } catch (_: InterruptedException) {
            }
        }
    }
}

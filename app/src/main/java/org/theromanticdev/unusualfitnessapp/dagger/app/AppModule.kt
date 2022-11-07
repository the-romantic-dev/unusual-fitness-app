package org.theromanticdev.unusualfitnessapp.dagger.app

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import org.theromanticdev.unusualfitnessapp.data.repositories.SQLiteRepository
import org.theromanticdev.unusualfitnessapp.domain.repository.DatabaseRepository
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun provideDatabaseRepository(context: Context): DatabaseRepository = SQLiteRepository(context)


}
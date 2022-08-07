package br.com.dio.app.repositories.data.di

import android.util.Log
import br.com.dio.app.repositories.data.repositories.RepoRepository
import br.com.dio.app.repositories.data.repositories.RepoRepositoryImpl
import br.com.dio.app.repositories.data.services.GitHubService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sin

object DataModule {

    fun load(){
        loadKoinModules(networkModules() + repositoriesModules())
    }

    private const val OK_HTTP= "OkHttp"
    private fun networkModules(): Module{
        return module {
            single {
                val interceptor = HttpLoggingInterceptor{
                    Log.e(OK_HTTP,it )
                }

                interceptor.level = HttpLoggingInterceptor.Level.BODY

                OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()
            }
            single {
                GsonConverterFactory.create(GsonBuilder().create())
            }

            single {
                creatService<GitHubService>(get(),get())
            }
        }
    }

    private fun repositoriesModules(): Module{
        return module {
            single<RepoRepository> {
                RepoRepositoryImpl(get())
            }
        }
    }

    private inline fun <reified T> creatService(cliente : OkHttpClient, factory: GsonConverterFactory): T{
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(cliente)
            .addConverterFactory(factory)
            .build().create(T::class.java)
    }
}
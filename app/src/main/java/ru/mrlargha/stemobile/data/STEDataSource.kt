package ru.mrlargha.stemobile.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mrlargha.stemobile.data.model.SimpleServerReply
import ru.mrlargha.stemobile.data.model.Substitution
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

class STEDataSource {
    private val mSTEApi: STEApi
    fun login(vk_id: String?, password: String?): Result<*> {
        return executeCall(mSTEApi.login(vk_id!!, password!!))
    }

    fun logout(token: String?): Result<*> {
        return executeCall(mSTEApi.logout(token!!))
    }

    fun deleteSubstitution(token: String?, substitution: Substitution): Result<*> {
        return executeCall(mSTEApi.deleteSubstitution(token!!, substitution.substitutionDate.time.toString(), substitution.group.toString(), substitution.pair.toString()))
    }

    fun getUsers(token: String?): Result<*> {
        return executeCall(mSTEApi.getUsers(token!!))
    }

    fun setPermission(token: String?, vk_id: Int, permission: String?): Result<*> {
        return executeCall(mSTEApi.setPermission(token!!, vk_id, permission!!))
    }

    fun register(vk_id: String?, password: String?): Result<*> {
        return executeCall(mSTEApi.register(vk_id!!, password!!))
    }

    val formHints: Result<*>
        get() = executeCall(mSTEApi.getFormHints())

    fun getInfo(token: String?): Result<*> {
        return executeCall(mSTEApi.getInfo(token!!))
    }

    fun sendSubstitution(substitution: Substitution, token: String?): Result<*> {
        return executeCall(mSTEApi.insertSubstitution(token!!, substitution.substitutionDate.time.toString(),
                substitution.cabinet,
                substitution.teacher,
                substitution.subject, substitution.pair.toString(), substitution.group.toString()))
    }

    fun getSubstitutions(token: String?, date: Long): Result<*> {
        return executeCall(mSTEApi.getSubstitutions(token!!, date))
    }

    private fun <T : SimpleServerReply> executeCall(userCall: Call<T>): Result<*> {
        return try {
            val response = userCall.execute()
            if (response.body() != null) {
                if (response.body()!!.status == "ok") {
                    Result.Success(response.body())
                } else {
                    Result.Error(response.body()!!.error_string!!)
                }
            } else {
                throw IOException()
            }
        } catch (e: IOException) {
            Result.Error("Ошибка сети")
        }
    }

    init {
        val builder = GsonBuilder()
        builder.registerTypeAdapter(Date::class.java, JsonDeserializer { json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext? -> Date(json.asJsonPrimitive.asLong) } as JsonDeserializer<Date>)
        val gson = builder.create()
        // Use for debug server
//        Retrofit mRetrofit = new Retrofit.Builder().baseUrl("http://fspovkbot.tmweb.ru/ste/")

        // Use for production server
        val mRetrofit = Retrofit.Builder().baseUrl("https://fspovkbot.tmweb.ru/ste/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        mSTEApi = mRetrofit.create(STEApi::class.java)
    }
}
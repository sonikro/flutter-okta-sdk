package com.sonikro.flutter_okta_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import com.sonikro.flutter_okta_sdk.okta.entities.*
import com.sonikro.flutter_okta_sdk.okta.operations.*
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar


/** FlutterOktaSdkPlugin */
class FlutterOktaSdkPlugin : FlutterPlugin, MethodCallHandler,
        PluginRegistry.ActivityResultListener, ActivityAware{
    private lateinit var channel: MethodChannel

    private var oktaConfig: OktaConfig? = null
    private var applicationContext: Context? = null
    private var mainActivity: Activity? = null

    companion object {
        fun registerWith(registrar: Registrar) {
            val plugin = FlutterOktaSdkPlugin()
            plugin.setActivity(registrar.activity())
            plugin.onAttachedToEngine(registrar.context(), registrar.messenger())
            registrar.addActivityResultListener(plugin)
        }
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.onAttachedToEngine(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        oktaConfig!!.webClient.handleActivityResult(requestCode, resultCode, data)
        return  PendingOperation.hasPendingOperation != null
    }

    override fun onDetachedFromActivity() {
        this.mainActivity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        mainActivity = binding.activity
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        binding.addActivityResultListener(this)
        mainActivity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.mainActivity = null
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val arguments = call.arguments<Map<String, Any>>()
        PendingOperation.init(call.method, result)

//        if(!enumContains<Errors>(call.method) ){
//            finishWithError(Errors.METHOD_NOT_IMPLEMENTED, "Method called: $call.method")
//        }

        if (call.method == AvailableMethods.CREATE_CONFIG.methodName){
            try{
                if (applicationContext == null )
                    PendingOperation.error(Errors.NO_CONTEXT)

                oktaConfig = createConfig(arguments, applicationContext!!)
                PendingOperation.success(true)
            }catch (ex:  java.lang.Exception){
                PendingOperation.error(Errors.OKTA_OIDC_ERROR)
            }
        }else{
            try{
                if (this.mainActivity == null) {
                    PendingOperation.error(Errors.NO_VIEW)
                }

                if (oktaConfig!!.webClient == null) {
                    PendingOperation.error(Errors.NOT_CONFIGURED)
                }

                when (call.method){
                    AvailableMethods.SIGN_IN.methodName ->{
                        signIn(oktaConfig!!.webClient, this.mainActivity!!)
                    }
                    AvailableMethods.SIGN_OUT.methodName ->{
                        signOut(oktaConfig!!.webClient, this.mainActivity!!)
                    }
                    AvailableMethods.GET_USER.methodName ->{
                        getUser(oktaConfig!!.webClient)
                    }
                    AvailableMethods.IS_AUTHENTICATED.methodName ->{
                        isAuthenticated(oktaConfig!!.webClient)
                    }
//                    AvailableMethods.GET_ACCESS_TOKEN.methodName ->{
//                        val accessToken = getAccessToken(oktaConfig!!.webClient)
//                        if(accessToken ==null)finishWithError(Errors.NO_ACCESS_TOKEN)
//                        finishWithSuccess(accessToken)
//                    }
//                    AvailableMethods.GET_ID_TOKEN.methodName ->{
//                        val idToken = getIdToken(oktaConfig!!.webClient)
//                        if(idToken ==null)finishWithError(Errors.NO_ID_TOKEN)
//                        finishWithSuccess(idToken)
//                    }
//                    AvailableMethods.REVOKE_ACCESS_TOKEN.methodName ->{
//                        revokeAccessToken(oktaConfig!!.webClient)
//                        finishWithSuccess()
//                    }
//                    AvailableMethods.REVOKE_ID_TOKEN.methodName ->{
//                        revokeIdToken(oktaConfig!!.webClient)
//                        finishWithSuccess()
//                    }
//                    AvailableMethods.REVOKE_REFRESH_TOKEN.methodName ->{
//                        revokeRefreshToken(oktaConfig!!.webClient)
//                        finishWithSuccess()
//                    }
//                    AvailableMethods.CLEAR_TOKENS.methodName ->{
//                        clearTokens(oktaConfig!!.webClient,oktaConfig!!.authClient)
//                        finishWithSuccess()
//                    }
//                    AvailableMethods.INTROSPECT_ACCESS_TOKEN.methodName ->{
//                        val result: IntrospectInfo = introspectAccessToken(oktaConfig!!.webClient)
//                        finishWithSuccess(result)
//                    }
//                    AvailableMethods.INTROSPECT_ID_TOKEN.methodName ->{
//                        val result: IntrospectInfo = introspectIdToken(oktaConfig!!.webClient)
//                        finishWithSuccess(result)
//                    }
//                    AvailableMethods.INTROSPECT_REFRESH_TOKEN.methodName ->{
//                        val result: IntrospectInfo = introspectRefreshToken(oktaConfig!!.webClient)
//                        finishWithSuccess(result)
//                    }
//                    AvailableMethods.REFRESH_TOKENS.methodName ->{
//                        refreshTokens(oktaConfig!!.webClient)
//                        finishWithSuccess()
//                    }
                }
            }catch (ex: java.lang.Exception) {
                PendingOperation.error(Errors.GENERIC_ERROR,ex.localizedMessage)
            }
        }
    }

    fun setActivity(activity: Activity) {
        this.mainActivity = activity
    }

    private fun onAttachedToEngine(context: Context, binaryMessenger: BinaryMessenger) {
        applicationContext = context
        channel = MethodChannel(binaryMessenger, "com.sonikro.flutter_okta_sdk")
        channel.setMethodCallHandler(this)
    }

}

package co.intecdl.platewatch.logging
import android.util.Log
class SafeLogger(private val enabled:Boolean){ fun info(event:String){if(enabled)Log.i("PlateWatch",event)};fun error(event:String,t:Throwable?=null){if(enabled)Log.e("PlateWatch",event,t)} }

package co.intecdl.platewatch.notifications
import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
class AlertManager(private val context:Context){
 fun createChannel(){ if(Build.VERSION.SDK_INT>=26){ val c=NotificationChannel(CHANNEL,"Coincidencias",NotificationManager.IMPORTANCE_HIGH).apply{enableVibration(true);description="Alertas locales de placas vigiladas"}; context.getSystemService(NotificationManager::class.java).createNotificationChannel(c) } }
 fun notifyMatch(plate:String,confidence:Float,time:String,pendingIntent:PendingIntent?=null){ val b=NotificationCompat.Builder(context,CHANNEL).setSmallIcon(android.R.drawable.ic_dialog_alert).setContentTitle("Placa detectada").setContentText("$plate | ${(confidence*100).toInt()}% | $time").setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).setVibrate(longArrayOf(0,350,150,350)); pendingIntent?.let{b.setContentIntent(it)}; context.getSystemService(NotificationManager::class.java).notify(plate.hashCode(),b.build()) }
 companion object{const val CHANNEL="plate_matches"}
}

package co.intecdl.platewatch.watch
class MatchCooldown(private val cooldownMillis:Long){ private val last=mutableMapOf<String,Long>(); @Synchronized fun permit(plate:String,now:Long):Boolean{ val p=last[plate]; if(p!=null&&now-p<cooldownMillis)return false; last[plate]=now; return true } }

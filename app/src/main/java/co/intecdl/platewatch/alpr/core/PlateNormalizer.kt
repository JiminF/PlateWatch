package co.intecdl.platewatch.alpr.core
class PlateNormalizer { fun normalize(raw:String)=raw.uppercase().filter { it in 'A'..'Z'||it in '0'..'9' } }

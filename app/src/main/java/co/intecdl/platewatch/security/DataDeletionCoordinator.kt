package co.intecdl.platewatch.security
import co.intecdl.platewatch.data.local.dao.DetectionDao
import co.intecdl.platewatch.storage.EvidencePaths
import co.intecdl.platewatch.storage.EvidenceRepository
class DataDeletionCoordinator(private val dao:DetectionDao,private val evidence:EvidenceRepository){ suspend fun deleteDetection(id:Long,paths:EvidencePaths){evidence.delete(paths);dao.deleteById(id)};suspend fun deleteHistory(){dao.deleteAll()} }

package org.sagebionetworks.research.mpower.room

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import org.sagebionetworks.bridge.rest.model.ActivityType
import org.sagebionetworks.bridge.rest.model.ScheduleStatus

import java.util.ArrayList

@Entity
data class RoomScheduledActivity(@SerializedName("guid") @PrimaryKey var guid: String) {
    @SerializedName("schedulePlanGuid")
    var schedulePlanGuid: String? = null

    @SerializedName("startedOn")
    var startedOn: DateTime? = null

    @SerializedName("finishedOn")
    var finishedOn: DateTime? = null

    @SerializedName("scheduledOn")
    var scheduledOn: DateTime? = null

    @SerializedName("expiresOn")
    var expiresOn: DateTime? = null

    @SerializedName("activity")
    @Embedded(prefix = "activity_")
    var activity: RoomActivity? = null

    @SerializedName("persistent")
    var persistent: Boolean? = null

    /// TODO: how do we store a weak type
//    @SerializedName("clientData")
//    var clientData: Any? = null

    @SerializedName("status")
    var status: ScheduleStatus? = null

    @SerializedName("type")
    var type: String? = null
}

data class RoomActivity(@SerializedName("guid") var guid: String) {
    @SerializedName("label")
    var label: String? = null

    @SerializedName("labelDetail")
    var labelDetail: String? = null

    @SerializedName("compoundActivity")
    @Embedded(prefix = "compound_")
    var compoundActivity: RoomCompoundActivity? = null

    @SerializedName("task")
    @Embedded(prefix = "task_")
    var task: RoomTaskReference? = null

    @SerializedName("survey")
    @Embedded(prefix = "survey_")
    var survey: RoomSurveyReference? = null

    @SerializedName("activityType")
    var activityType: ActivityType? = null

    @SerializedName("type")
    var type: String? = null
}


data class RoomSchemaReference(@SerializedName("guid") var guid: String) {
    @SerializedName("identifier")
    var identifier: String? = null

    @SerializedName("createdOn")
    var createdOn: DateTime? = null

    @SerializedName("href")
    var href: String? = null

    @SerializedName("type")
    var type: String? = null
}

data class RoomTaskReference(@SerializedName("identifier") var identifier: String) {
    @SerializedName("schema")
    @Embedded(prefix = "schema_")
    var schema: RoomSchemaReference? = null

    @SerializedName("type")
    var type: String? = null
}

data class RoomSurveyReference(@SerializedName("guid") var guid: String) {
    @SerializedName("identifier")
    var identifier: String? = null

    @SerializedName("createdOn")
    var createdOn: DateTime? = null

    @SerializedName("href")
    var href: String? = null

    @SerializedName("type")
    var type: String? = null
}

data class RoomCompoundActivity(@SerializedName("taskIdentifier") var taskIdentifier: String) {
    @SerializedName("schemaList")
    var schemaList: List<RoomSchemaReference> = ArrayList()

    @SerializedName("surveyList")
    var surveyList: List<RoomSurveyReference> = ArrayList()

    @SerializedName("type")
    var type: String? = null
}
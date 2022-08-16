package com.raywenderlich.android.petrealm.realm

import androidx.annotation.DrawableRes
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class PetRealm(
    @LinkingObjects("pets")//Add @LinkingObjects annotation, passing as parameter the name of the field you’re adding the relationship to. The field in OwnerRealm you want to link is pets.
    val owner: RealmResults<OwnerRealm>? = null,//The field should be val and of type RealmResults.
    @PrimaryKey
    var id: String = ObjectId().toHexString(),
    @Required
    var name: String = "",
    @Required//一定要有值
    var petType: String = "",
    var age: Int = 0,
    var isAdopted: Boolean = false,
    @DrawableRes
    var image: Int? = null

): RealmObject()
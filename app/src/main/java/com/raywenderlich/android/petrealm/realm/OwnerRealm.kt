package com.raywenderlich.android.petrealm.realm

import androidx.annotation.DrawableRes
import com.raywenderlich.android.petrealm.pets.models.Pet
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId

open class OwnerRealm(
    var pets: RealmList<PetRealm> = RealmList(),
    @PrimaryKey
    var id: String = ObjectId().toHexString(),
    @Required
    var name: String = "",
    @DrawableRes
    var image: Int? = null

) : RealmObject()
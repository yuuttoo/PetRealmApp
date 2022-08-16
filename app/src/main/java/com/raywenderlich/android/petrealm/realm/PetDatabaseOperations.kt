/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.petrealm.realm

import com.raywenderlich.android.petrealm.pets.models.Pet
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PetDatabaseOperations @Inject constructor(
  private val config: RealmConfiguration
) {

  suspend fun insertPet(
      name: String,
      age: Int,
      type: String,
      image: Int?
  ) {
  //1.
    val realm = Realm.getInstance(config)

    //2.
    realm.executeTransactionAwait(Dispatchers.IO) {
      realmTransaction ->

      val pet = PetRealm(name = name, age = age, petType = type, image = image)

      //4.
      realmTransaction.insert(pet)

    }
  }

  suspend fun retrievePetsToAdopt(): List<Pet> {
    //1.  Get the Realm instance.
    val realm = Realm.getInstance(config)
    val petsToAdopt = mutableListOf<Pet>()

      //2.executeTransactionAwait
    realm.executeTransactionAwait(Dispatchers.IO) {
      realmTransaction ->
        petsToAdopt.addAll(realmTransaction
          .where(PetRealm::class.java)//Use where(PetRealm::class.java) to retrieve PetRealm objects.
          .findAll()//executes the query and returns every PetRealm object.
          .map {
            mapPet(it)//Map PetRealm to Pet objects.
          }
        )
    }

    return petsToAdopt
  }

  suspend fun retrieveAdoptedPets(): List<Pet> {
    val realm = Realm.getInstance(config)
    val adoptedPets = mutableListOf<Pet>()

    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      adoptedPets.addAll(realmTransaction
        .where(PetRealm::class.java)
        .equalTo("isAdopted", true)
        .findAll()
        .map {
          mapPet(it)
        }
      )
    }
    return adoptedPets
  }

  suspend fun removePet(petId: String) {
    val realm = Realm.getInstance(config)
    realm.executeTransactionAwait(Dispatchers.IO) {
      realmTransaction ->
      //1.Query for the object you want to remove. This query should be in a transaction.
      val petToRemove = realmTransaction
        .where(PetRealm::class.java)
        .equalTo("id", petId)
        .findFirst()
      //2.Use deleteFromRealm() in that object to remove it from the database.
      petToRemove?.deleteFromRealm()
    }
  }

  suspend fun retrieveFilteredPets(petType: String): List<Pet> {
    val realm = Realm.getInstance(config)
    val filteredPets = mutableListOf<Pet>()

    realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
      filteredPets.addAll(realmTransaction
        .where(PetRealm::class.java)
        // 1.
        .equalTo("isAdopted", false)
        // 2.
        .and()
        // 3.
        .beginsWith("petType", petType)
        .findAll()
        .map {
          mapPet(it)
        }
      )
    }
    return filteredPets
  }

  private fun mapPet(pet: PetRealm): Pet {
    return Pet(
      name = pet.name,
      age = pet.age,
      image = pet.image,
      petType =  pet.petType,
      isAdopted = pet.isAdopted,
      id = pet.id,
      ownerName = pet.owner?.firstOrNull()?.name
    )
  }
}
package headmade.kotlinplayground

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
/*******************************************************************************
 *    Copyright 2015 Headmade Games
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
class PlaygroundContactlistener(private val playground: KotlinPlayground) : ContactListener {

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
    }

    override fun endContact(contact: Contact) {
    }

    override fun beginContact(contact: Contact) {
        val headContact = contact.fixtureA.body == playground.hippo.head || contact.fixtureB.body == playground.hippo.head
        val bellyContact = contact.fixtureA.body == playground.hippo.belly || contact.fixtureB.body == playground.hippo.belly

        if (bellyContact) {
            playground.hippo.onBellyHit()
        } else if (headContact) {
            playground.hippo.onHeadHit()
        }
    }

    companion object {
        private val TAG = PlaygroundContactlistener::class.java.name
    }
}

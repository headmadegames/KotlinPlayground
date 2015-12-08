package headmade.kotlinplayground

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

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

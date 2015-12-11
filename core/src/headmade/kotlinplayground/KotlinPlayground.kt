package headmade.kotlinplayground

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Peripheral
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array

import box2dLight.ConeLight
import box2dLight.PointLight
import box2dLight.RayHandler
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
class KotlinPlayground : Game() {

    internal lateinit var batch: SpriteBatch
    internal lateinit var shapeRenderer: ShapeRenderer
    internal lateinit var box2dRenderer: Box2DDebugRenderer
    internal lateinit var cam: OrthographicCamera
    internal lateinit var world: World

    internal lateinit var boingSound: Sound
    internal lateinit var hippo: Hippo
    internal lateinit var bgLightLeft: ConeLight
    internal lateinit var bgLightRight: ConeLight

    private var balls: Array<Body>? = null
    private var colors: Array<Color>? = null

    public var force = Vector2(0f, 0f)
    var rayHandler: RayHandler? = null

    override fun create() {
        UNIT_SCALE = 35f / Gdx.graphics.width

        boingSound = Gdx.audio.newSound(Gdx.files.internal("boing.wav"))

        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        box2dRenderer = Box2DDebugRenderer()
        world = World(Vector2(0f, -10f), true)
        world.setContactListener(PlaygroundContactlistener(this))
        cam = OrthographicCamera(Gdx.graphics.width * UNIT_SCALE, Gdx.graphics.height * UNIT_SCALE)
        cam.translate(0f, Gdx.graphics.height * UNIT_SCALE / 2 - 1)
        cam.update()
        colors = Array(arrayOf(Color.FIREBRICK, Color.GOLD, Color.PINK, Color.LIME))

        /** BOX2D LIGHT STUFF BEGIN  */
        rayHandler = RayHandler(world)
        rayHandler!!.setAmbientLight(0.28f, 0.28f, 0.28f, 0.1f)
        rayHandler!!.setBlurNum(3)
        rayHandler!!.diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR)
        // RayHandler.setGammaCorrection(true)
        RayHandler.useDiffuseLight(true)
        // rayHandler.setBlur(false)
        /** BOX2D LIGHT STUFF END  */

        Gdx.input.inputProcessor = PlaygroundInputProcessor(this)

        val bd = BodyDef()
        var ground = world.createBody(bd)
        run { // bounds
            val shape = EdgeShape()
            shape.set(Vector2(-40.0f, 0.0f), Vector2(40.0f, 0.0f))
            ground.createFixture(shape, 0.0f)
            shape.set(Vector2(-20.0f, 0.0f), Vector2(-20.0f, 60.0f))
            ground.createFixture(shape, 0.0f)
            shape.set(Vector2(20.0f, 0.0f), Vector2(20.0f, 60.0f))
            ground.createFixture(shape, 0.0f)
            shape.set(Vector2(-40.0f, 60.0f), Vector2(40.0f, 60.0f))
            ground.createFixture(shape, 0.0f)
        }
        run { // balls
            balls = Array<Body>()
            val ballShape = CircleShape()
            ballShape.radius = 2f

            val bd = BodyDef()
            bd.type = BodyType.DynamicBody

            val fd = FixtureDef()
            fd.density = 0.0001f
            fd.restitution = 1f
            fd.shape = ballShape

            ballShape.radius = 4f
            bd.position.set(0f, 50.0f)
            var body = world.createBody(bd)
            body.createFixture(fd)
            balls!!.add(body)

            ballShape.radius = 5f
            bd.position.set(8f, 44.0f)
            body = world.createBody(bd)
            body.createFixture(fd)
            balls!!.add(body)

            ballShape.radius = 6f
            bd.position.set(-4f, 55.0f)
            body = world.createBody(bd)
            body.createFixture(fd)
            balls!!.add(body)
        }

        hippo = Hippo(this, world, ground)

        for (i in 0..balls!!.size - 1) {
            val light = PointLight(rayHandler, 128, colors!!.get(i), i * 2f + 15f, 0f, 0f)
            light.attachToBody(balls!!.get(i), 0f, 0f, 90f)
            light.ignoreAttachedBody = true
            light.setSoftnessLength(5.5f)
            light.color.a = 0.6f
        }

        val lightColor = Color(1f, 0.9f, 0.7f, 0.5f)
        bgLightLeft = ConeLight(rayHandler, 128, lightColor, 60f, -15f, 1f, 45f, 90f)
        bgLightLeft.setSoftnessLength(15.5f)

        bgLightRight = ConeLight(rayHandler, 128, lightColor, 60f, 15f, 1f, (45 + 90).toFloat(), 90f)
        bgLightRight.setSoftnessLength(15.5f)
    }

    override fun render() {

        val delta = Gdx.graphics.deltaTime
        update(delta)

        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        shapeRenderer.projectionMatrix = cam.combined
        rayHandler!!.setCombinedMatrix(cam)

        // drawWoman();
        hippo.draw(shapeRenderer)

        shapeRenderer.begin(ShapeType.Filled)
        for (i in 0..balls!!.size - 1) {
            shapeRenderer.color = colors!!.get(i)
            shapeRenderer.circle(balls!!.get(i).worldCenter.x, balls!!.get(i).worldCenter.y, (i + 4).toFloat(), 32)
        }
        shapeRenderer.end()

        // box2dRenderer.render(world, cam.combined)
        rayHandler!!.updateAndRender()

        batch.begin()
        batch.end()
    }

    private fun update(delta: Float) {
        world.step(1f / 60f, 6, 2)

        if (Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)) {
            val accelY = Gdx.input.accelerometerY
            val accelZ = Gdx.input.accelerometerX
            force.x = accelZ
            force.y = accelY
        }

        val trueForce = force.cpy().scl(100f, 30f)

        hippo.update(trueForce)
    }

    public fun setForce(v: Vector3) {
        this.force = Vector2(v.x, v.y)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        cam.viewportWidth = width * UNIT_SCALE
        cam.viewportHeight = height * UNIT_SCALE
        cam.update()
    }

    override fun dispose() {
        super.dispose()
        rayHandler!!.dispose()
        batch.dispose()
        shapeRenderer.dispose()
        boingSound.dispose()
        world.dispose()
    }

    companion object {
        var UNIT_SCALE: Float = 0.toFloat()
    }

}

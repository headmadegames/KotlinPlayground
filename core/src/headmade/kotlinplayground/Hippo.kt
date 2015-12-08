package headmade.kotlinplayground

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef

class Hippo(private val playground: KotlinPlayground, world: World, ground: Body) {

    internal var torsoLowLeft: Body
    internal var torsoUpLeft: Body
    internal var torsoLowRight: Body
    internal var torsoUpRight: Body
    internal var armLeft: Body
    internal var armRight: Body
    internal var head: Body
    internal var belly: Body

    private val skinColor: Color
    private val lighterSkinColor: Color

    init {
        skinColor = Color(-1600085761)
        lighterSkinColor = Color(-1061109505)

        val boxShape = PolygonShape()
        boxShape.setAsBox(0.5f, 0.5f)

        val armShape = PolygonShape()
        armShape.setAsBox(1f, 3f)

        val circleShape = CircleShape()
        circleShape.radius = 2f

        val bd = BodyDef()
        bd.type = BodyType.DynamicBody

        bd.position.set(-4.5f, 5.0f)
        torsoLowLeft = world.createBody(bd)
        torsoLowLeft.createFixture(boxShape, 5.0f)

        bd.position.set(4.5f, 5.0f)
        torsoLowRight = world.createBody(bd)
        torsoLowRight.createFixture(boxShape, 5.0f)

        bd.position.set(4.0f, 15.0f)
        torsoUpRight = world.createBody(bd)
        torsoUpRight.createFixture(boxShape, 5.0f)

        bd.position.set(-4.0f, 15.0f)
        torsoUpLeft = world.createBody(bd)
        torsoUpLeft.createFixture(boxShape, 5.0f)

        bd.position.set(0.0f, 19.0f)
        head = world.createBody(bd)
        head.createFixture(circleShape, 0.001f)

        bd.position.set(0.0f, 8.5f)
        belly = world.createBody(bd)
        circleShape.radius = 6.5f
        belly.createFixture(circleShape, 0.001f)

        bd.position.set(6.2f, 19.2f)
        armRight = world.createBody(bd)
        armRight.createFixture(armShape, 0.001f)

        bd.position.set(-6.2f, 19.2f)
        armLeft = world.createBody(bd)
        armLeft.createFixture(armShape, 0.001f)

        val jd = DistanceJointDef()

        jd.frequencyHz = 4.0f
        jd.dampingRatio = 0.25f

        jd.bodyA = ground
        jd.bodyB = torsoLowLeft
        jd.localAnchorA.set(-10.0f, 0.0f)
        jd.localAnchorB.set(-0.5f, -0.5f)
        createJoint(world, jd)

        jd.bodyA = ground
        jd.bodyB = torsoLowRight
        jd.localAnchorA.set(10.0f, 0.0f)
        jd.localAnchorB.set(0.5f, -0.5f)
        createJoint(world, jd)

        jd.bodyA = ground
        jd.bodyB = torsoUpRight
        jd.localAnchorA.set(10.0f, 20.0f)
        jd.localAnchorB.set(0.5f, 0.5f)
        createJoint(world, jd)

        jd.bodyA = ground
        jd.bodyB = torsoUpLeft
        jd.localAnchorA.set(-10.0f, 20.0f)
        jd.localAnchorB.set(-0.5f, 0.5f)
        createJoint(world, jd)

        jd.bodyA = torsoLowLeft
        jd.bodyB = torsoLowRight
        jd.localAnchorA.set(0.5f, 0.0f)
        jd.localAnchorB.set(-0.5f, 0.0f)
        createJoint(world, jd)

        jd.bodyA = torsoLowRight
        jd.bodyB = torsoUpRight
        jd.localAnchorA.set(0.0f, 0.5f)
        jd.localAnchorB.set(0.0f, -0.5f)
        createJoint(world, jd)

        jd.bodyA = torsoUpRight
        jd.bodyB = torsoUpLeft
        jd.localAnchorA.set(-0.5f, 0.0f)
        jd.localAnchorB.set(0.5f, 0.0f)
        createJoint(world, jd)

        jd.bodyA = torsoUpLeft
        jd.bodyB = torsoLowLeft
        jd.localAnchorA.set(0.0f, -0.5f)
        jd.localAnchorB.set(0.0f, 0.5f)
        createJoint(world, jd)

        // head
        setJointDef(jd, torsoUpRight, head)
        createJoint(world, jd)
        setJointDef(jd, torsoUpLeft, head)
        createJoint(world, jd)

        // belly
        setJointDef(jd, torsoLowLeft, belly)
        createJoint(world, jd)
        setJointDef(jd, torsoLowRight, belly)
        createJoint(world, jd)
        setJointDef(jd, torsoUpRight, belly)
        createJoint(world, jd)
        setJointDef(jd, torsoUpLeft, belly)
        createJoint(world, jd)

        // arms
        val wjd = WeldJointDef()
        wjd.frequencyHz = 2f
        wjd.dampingRatio = 0.5f
        wjd.referenceAngle = -1.1f

        wjd.bodyA = torsoUpRight
        wjd.bodyB = armRight
        wjd.localAnchorA.set(0.0f, 0.0f)
        wjd.localAnchorB.set(0.0f, -1.9f)
        world.createJoint(wjd)

        wjd.referenceAngle = 1.1f
        wjd.bodyA = torsoUpLeft
        wjd.bodyB = armLeft
        wjd.localAnchorA.set(0.0f, 0.0f)
        wjd.localAnchorB.set(0.0f, -1.9f)
        world.createJoint(wjd)

        jd.bodyA = armRight
        jd.bodyB = belly
        jd.localAnchorA.set(0.0f, 0.0f)
        jd.localAnchorB.set(0.0f, 0.0f)
        createJoint(world, jd)

        jd.bodyA = armLeft
        jd.bodyB = belly
        jd.localAnchorA.set(0.0f, 0.0f)
        jd.localAnchorB.set(0.0f, 0.0f)
        createJoint(world, jd)

        boxShape.dispose()
        armShape.dispose()
        circleShape.dispose()
    }

    fun draw(shapeRenderer: ShapeRenderer) {

        val crotch = torsoLowLeft.worldCenter.cpy().interpolate(torsoLowRight.worldCenter, 0.5f, Interpolation.linear)
        val headPos = head.worldCenter.cpy()
        val snout = head.worldCenter.cpy().add(0f, -3f)
        val leftEye = head.worldCenter.cpy().add(-0.5f, 0.6f)
        val rightEye = head.worldCenter.cpy().add(+0.5f, 0.6f)
        val leftNostril = snout.cpy().add(-1.6f, 1.7f)
        val rightNostril = snout.cpy().add(+1.6f, 1.7f)
        val leftEar = headPos.cpy().add(-1.5f, 1.5f)
        val rightEar = headPos.cpy().add(1.5f, 1.5f)
        val leftFoot = Vector2(-1f, 0f)
        val rightFoot = Vector2(1f, 0f)

        val headWidth = 6f
        val headHeight = 8f
        val faceFactorX = 1.3f
        val faceFactorY = 0.6f

        run { // ears
            shapeRenderer.begin(ShapeType.Filled)
            shapeRenderer.color = skinColor

            shapeRenderer.circle(leftEar.x, leftEar.y, 0.5f, 8)
            shapeRenderer.circle(rightEar.x, rightEar.y, 0.5f, 8)

            shapeRenderer.end()
        }

        run { // draw outlines
            shapeRenderer.begin(ShapeType.Line)
            shapeRenderer.color = Color.BLACK

            shapeRenderer.circle(headPos.x, headPos.y, 2f, 16)
            shapeRenderer.circle(belly.worldCenter.x, belly.worldCenter.y, 6.5f, 32)

            // torso
            drawTriangle(torsoLowLeft.worldCenter, crotch, leftFoot, Color.BLACK, Color.BLACK, Color.BLACK)
            drawTriangle(torsoLowLeft.worldCenter, leftFoot, leftFoot.cpy().add(-3f, 0f), Color.BLACK, Color.BLACK, Color.BLACK)
            drawTriangle(crotch, torsoLowRight.worldCenter, rightFoot, Color.BLACK, Color.BLACK, Color.BLACK)
            drawTriangle(torsoLowRight.worldCenter, rightFoot, rightFoot.cpy().add(3f, 0f), Color.BLACK, Color.BLACK, Color.BLACK)

            // arms
            shapeRenderer.rectLine(torsoUpRight.worldCenter.cpy().interpolate(armRight.worldCenter, -2f, Interpolation.linear),
                    armRight.worldCenter.cpy().interpolate(torsoUpRight.worldCenter, -2f, Interpolation.linear), 2f)
            shapeRenderer.rectLine(torsoUpLeft.worldCenter.cpy().interpolate(armLeft.worldCenter, -2f, Interpolation.linear),
                    armLeft.worldCenter.cpy().interpolate(torsoUpLeft.worldCenter, -2f, Interpolation.linear), 2f)

            shapeRenderer.end()
        }

        run { // fill Outlines
            shapeRenderer.begin(ShapeType.Filled)
            shapeRenderer.color = skinColor

            // rectagular torso
            drawTriangle(torsoLowLeft.worldCenter, torsoLowRight.worldCenter, torsoUpLeft.worldCenter, skinColor, skinColor,
                    skinColor)
            drawTriangle(torsoLowRight.worldCenter, torsoUpRight.worldCenter, torsoUpLeft.worldCenter, skinColor, skinColor,
                    skinColor)
            shapeRenderer.circle(belly.worldCenter.x, belly.worldCenter.y, 6.5f, 32)

            // legs
            drawTriangle(torsoLowLeft.worldCenter, crotch, leftFoot, skinColor, skinColor, skinColor)
            drawTriangle(torsoLowLeft.worldCenter, leftFoot, leftFoot.cpy().add(-3f, 0f), skinColor, skinColor, skinColor)
            drawTriangle(crotch, torsoLowRight.worldCenter, rightFoot, skinColor, skinColor, skinColor)
            drawTriangle(torsoLowRight.worldCenter, rightFoot, rightFoot.cpy().add(3f, 0f), skinColor, skinColor, skinColor)

            // arms
            shapeRenderer.rectLine(torsoUpRight.worldCenter.cpy().interpolate(armRight.worldCenter, -2f, Interpolation.linear),
                    armRight.worldCenter.cpy().interpolate(torsoUpRight.worldCenter, -2f, Interpolation.linear), 2f)
            shapeRenderer.rectLine(torsoUpLeft.worldCenter.cpy().interpolate(armLeft.worldCenter, -2f, Interpolation.linear),
                    armLeft.worldCenter.cpy().interpolate(torsoUpLeft.worldCenter, -2f, Interpolation.linear), 2f)

            // head
            shapeRenderer.circle(headPos.x, headPos.y, 2f, 16)
            shapeRenderer.ellipse(snout.x - headWidth * faceFactorX / 2f, snout.y - 1.5f, headWidth * faceFactorX,
                    headHeight * faceFactorY)

            // eyes
            shapeRenderer.color = Color.BLACK
            shapeRenderer.circle(leftEye.x, leftEye.y, 0.15f, 32)
            shapeRenderer.circle(rightEye.x, rightEye.y, 0.15f, 32)

            // nostrills
            shapeRenderer.arc(leftNostril.x, leftNostril.y, 0.29f, 10f, 180f, 16)
            shapeRenderer.arc(rightNostril.x, rightNostril.y, 0.29f, -10f, 180f, 16)

            // teeth
            shapeRenderer.color = Color.WHITE
            shapeRenderer.arc(leftNostril.x, leftNostril.y - 1.99f, 0.49f, 160f, 180f, 16)
            shapeRenderer.arc(rightNostril.x, rightNostril.y - 1.99f, 0.59f, 200f, 180f, 16)

            // belly
            shapeRenderer.color = lighterSkinColor
            shapeRenderer.circle(belly.worldCenter.x + crotch.x / 2f, belly.worldCenter.y - 1f, 4.5f, 32)
            shapeRenderer.color = skinColor

            shapeRenderer.end()
        }

        run { // face
            shapeRenderer.begin(ShapeType.Line)
            shapeRenderer.color = Color.BLACK

            shapeRenderer.ellipse(snout.x - headWidth * faceFactorX / 2f, snout.y - 1.5f, headWidth * faceFactorX, headHeight * faceFactorY,
                    32)

            shapeRenderer.end()
        }
    }

    fun update(force: Vector2) {
        torsoLowLeft.applyForceToCenter(force, true)
        torsoLowRight.applyForceToCenter(force, true)
    }

    fun onBellyHit() {
        playground.bgLightRight.color = Color(Math.max(0.3f, RandomUtil.random()), Math.max(0.3f, RandomUtil.random()),
                Math.max(0.3f, RandomUtil.random()), 0.5f)
        playground.bgLightLeft.color = Color(Math.max(0.3f, RandomUtil.random()), Math.max(0.3f, RandomUtil.random()),
                Math.max(0.3f, RandomUtil.random()), 0.5f)
    }

    fun onHeadHit() {
        playground.boingSound.play(0.5f, 1.1f + RandomUtil.random() / 3f, 1f)
    }

    private fun drawTriangle(v1: Vector2, v2: Vector2, v3: Vector2, c1: Color, c2: Color, c3: Color) {
        playground.shapeRenderer.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, c1, c2, c3)
    }

    private fun setJointDef(jd: DistanceJointDef, bodyA: Body, bodyB: Body) {
        jd.bodyA = bodyA
        jd.bodyB = bodyB
        jd.localAnchorA.set(0.0f, 0.0f)
        jd.localAnchorB.set(0.0f, 0.0f)
    }

    private fun createJoint(world: World, jd: DistanceJointDef) {
        val p1 = jd.bodyA.getWorldPoint(jd.localAnchorA)
        val p2 = jd.bodyB.getWorldPoint(jd.localAnchorB)
        val d = p2.sub(p1)
        jd.length = d.len()
        world.createJoint(jd)
    }

    companion object {
        private val TAG = Hippo::class.java.name
    }
}

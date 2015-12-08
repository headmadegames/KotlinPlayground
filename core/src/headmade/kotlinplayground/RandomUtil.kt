/*******************************************************************************
 * Copyright 2015 Headmade Games

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package headmade.kotlinplayground

import java.util.Random

object RandomUtil {
    private val TAG = RandomUtil::class.java.name

    private val random = Random()

    /**
     * Returns a random element of a Set.
     */
    fun <T> random(set: Set<T>): T {
        val size = set.size

        if (size <= 0) {
            throw IllegalStateException("Can't get a random item of an empty Set")
        }

        val randomIndex = random.nextInt(size)

        var i = 0
        for (t in set) {
            if (i == randomIndex) {
                return t
            }
            i++
        }

        throw IllegalStateException("This case should never happen if the Set has elements")
    }

    /**
     * Provides a random element of an array.
     */
    fun <T> random(array: Array<T>): T {
        if (array.size() == 0) {
            throw IllegalStateException("Can't get a random item of an empty array")
        }
        return array[random.nextInt(array.size())]
    }

    fun <T> random(list: List<T>): T {
        if (list.isEmpty()) {
            throw IllegalStateException("Can't get a random item from an empty List<T>")
        }
        return list[random.nextInt(list.size)]
    }

    fun random(): Float {
        return random.nextFloat()
    }

    fun random(max: Int): Int {
        return random.nextInt(max)
    }

}

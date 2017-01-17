package org.lpw.tephra.util

import java.util.UUID

import org.springframework.stereotype.Component

import scala.util.Random

/**
  * @author lpw
  */
@Component("tephra.util.generator")
class GeneratorImpl extends Generator {
    override def random(length: Int): String = {
        val string: StringBuilder = new StringBuilder
        val max: Int = 'z' - '0' + 1
        while (string.length < length) {
            val n: Int = Math.abs(Random.nextInt) % max + '0'
            if (n <= '9' || n >= 'a')
                string += n.toChar
        }

        string.toString
    }

    override def number(length: Int): String = {
        val string: StringBuilder = new StringBuilder
        while (string.length < length) {
            val n: Int = Math.abs(Random.nextInt) % 10 + '0'
            string += n.toChar
        }

        string.toString
    }

    override def chars(length: Int): String = {
        val string: StringBuilder = new StringBuilder
        while (string.length < length) {
            val n: Int = Math.abs(Random.nextInt) % 26 + 'a'
            string += n.toChar
        }

        string.toString
    }

    override def random(min: Int, max: Int): Int = {
        if (min >= max)
            return (min + max) >> 1

        Math.abs(Random.nextInt) % (max - min + 1) + min
    }

    override def uuid(): String = {
        UUID.randomUUID.toString
    }
}

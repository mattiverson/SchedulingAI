import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * Created by Matt on 9/26/2018.
 */

val file = File("inputHard.txt")
val writer = BufferedWriter(FileWriter(file))
val row = 1000
val col = 3
fun main(args: Array<String>) {
    writer.write("$row $col\n")
    val builder = StringBuilder()
    (1..row).forEach {
        builder.delete(0, builder.length)
        (1..col+1).forEach {
            builder.append("${r()} ")
        }
        writer.write(builder.substring(0, builder.length-1))
        writer.newLine()
    }
    writer.flush()
    writer.close()
}

fun r() = 1+(Math.random()*50).toInt()
import java.io.*
import java.util.*

val names = ArrayList<String>()
var fileIdx = 1
/**
 * Created by Matt on 9/25/2018.
 */
fun readInput(): Pair<IntArray, Array<IntArray>> {
    val file = File("all_inputs/inputs/input_group12.txt")
//    val file = File("all_inputs/inputs/input_group$fileIdx.txt")
    val reader = BufferedReader(FileReader(file))
    val (col, row) = reader.readLine().split(" ").map{it.toInt()}
    val delay = IntArray(col)
    val time = matrix(row, col)
    var data: List<Int>
    (0..col-1).forEach { c ->
        data = reader.readLine().trim().split(" ").map { it.toInt() }
        delay[c] = data[0]
        data.drop(1).forEachIndexed { r, t ->
            time[r][c] = t
        }
    }
    return Pair(delay, time)
}

fun readInputFolder(): List<Pair<IntArray, Array<IntArray>>> {
    val dir = File("all_inputs/inputs")
    val inputs = ArrayList<Pair<IntArray, Array<IntArray>>>()
    dir.listFiles().forEach { file ->
        names.add(file.name.drop(6).dropLast(4))
        println(names.last())
        val reader = BufferedReader(FileReader(file))
        val (col, row) = reader.readLine().trim().split(" ").map{it.toInt()}
        val delay = IntArray(col)
        val time = matrix(row, col)
        var data: List<Int>
        (0..col-1).forEach { c ->
            data = reader.readLine().trim().split(" ").take(4).map { it.toInt() }
            delay[c] = data[0]
            data.drop(1).forEachIndexed { r, t ->
                time[r][c] = t
            }
        }
        inputs.add(Pair(delay, time))
    }
    return inputs
}

fun readOutput(): Pair<Array<IntArray>, Int> {
//    val file = File("verification_outputs/output_from_${fileIdx}_to_12'p.txt")
    val file = File("output.txt")
    val reader = BufferedReader(FileReader(file))
    val data = ArrayList<List<Int>>()
    val length = reader.readLine().toInt()
    while(reader.ready()) {
        data.add(reader.readLine().trim().split(" ").map { it.toInt() })
    }
    if (data.last().isEmpty()) data.dropLast(1)
    val ret = matrix(data[0].size, data.size)
    (0..ret.size-1).forEach { i ->
        (0..ret[0].size - 1).forEach { j ->
            ret[i][j] = data[j][i]
        }
    }
//    ret.forEach { it.forEach { print("$it ") };println() }
//    println("returning ${ret.size}x${ret[0].size}")
    return Pair(ret, length)
}

fun writeOutput(sch: Array<IntArray>, time:Int) {
    val file = File("output.txt")
    val writer = BufferedWriter(FileWriter(file))
    writer.write("$time")
    writer.newLine()
    val builder = StringBuilder()
    (0..sch[0].size-1).forEach { task ->
        builder.delete(0, builder.length)
        (0..2).forEach { bench ->
            builder.append("${sch[bench][task]} ")
        }
        writer.write(builder.substring(0, builder.length-1))
        writer.newLine()
    }
    writer.flush()
    writer.close()
}

var outputIdx = 0
val perfect = ArrayList<Int>()

fun writeNextOutput(sch: Array<IntArray>, time:Int, perfectSolution: Boolean) {
    val file = File("output/output_${names[outputIdx]}.txt")
    if (perfectSolution){
        perfect.add(outputIdx)
    }
    outputIdx++
    val writer = BufferedWriter(FileWriter(file))
    writer.write("$time")
    writer.newLine()
    val builder = StringBuilder()
    (0..sch[0].size-1).forEach { task ->
        builder.delete(0, builder.length)
        (0..2).forEach { bench ->
            builder.append("${sch[bench][task]} ")
        }
        writer.write(builder.substring(0, builder.length-1))
        writer.newLine()
    }
    writer.flush()
    writer.close()
}

fun writeNextOutputSlow(sch: Array<IntArray>, time:Int, perfectSolution: Boolean) {
    val file = File("outputSlow/output$outputIdx.txt")
    if (perfectSolution){
        perfect.add(outputIdx)
    }
    outputIdx++
    val writer = BufferedWriter(FileWriter(file))
    writer.write("$time")
    writer.newLine()
    val builder = StringBuilder()
    (0..sch[0].size-1).forEach { task ->
        builder.delete(0, builder.length)
        (0..2).forEach { bench ->
            builder.append("${sch[bench][task]} ")
        }
        writer.write(builder.substring(0, builder.length-1))
        writer.newLine()
    }
    writer.flush()
    writer.close()
}


fun writePerfect() {
    val file = File("output/perfect.txt")
    val writer = BufferedWriter(FileWriter(file))
    perfect.map { names[it] }.forEach { writer.write("$it");writer.newLine() }
    writer.flush()
    writer.close()
    perfect.map{names[it]}.forEach { println("perfect: $it") }
}
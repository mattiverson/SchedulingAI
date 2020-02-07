import java.lang.Math.*
import java.util.*

/**
 * Created by Matt on 9/24/2018.
 */
val DURATION_SECONDS = 10
var DURATION_NANOS = DURATION_SECONDS * 1000000000L

fun main(args: Array<String>) {
//    val input = readInput()
    val inputs = readInputFolder()
//    val inputs = arrayListOf(input)
    val length = inputs.size

    inputs.forEach { dat ->
        val (delay, times) = dat
        val target = getTarget(delay, times)
        println(target)
        val (bestSchedule, bestEnergy) = findBestSchedule(delay, times)
//        writeNextOutput(bestSchedule, bestEnergy, target == bestEnergy)
//        writeOutput(bestSchedule, bestEnergy)
    }
//    writePerfect()
}

fun findBestSchedule(delay: IntArray, times: Array<IntArray>): Pair<Array<IntArray>, Int> {
    val row = times.size
    val col = times[0].size
    val target = getTarget(delay, times)

    val startTime = System.nanoTime()
    val order = getStartingOrder2(delay, times)
    order.forEach { it.forEach { print("$it ") };println() }

    var (sch, eng) = getSchedule(order, times, delay.copyOf())
    var bestSchedule = sch
    var bestEnergy = eng

    if (bestEnergy == target) {
        println("eZ")
        return Pair(bestSchedule, bestEnergy)
    }
    (1..10).forEach {
        val (newSch, newEng) = getSchedule(order, times, delay.copyOf())
        if (newEng < bestEnergy) {
            bestEnergy = newEng
            bestSchedule = newSch
            if (newEng == target) {
                println("Guaranteed Optimal Solution")
                return@forEach
            }
        }
    }
    if (bestEnergy == target) {
        return Pair(bestSchedule, bestEnergy)
    }

    var reps = 0
    while (System.nanoTime() < startTime + DURATION_NANOS) {
        reps++
        move(order)
        if (order[0][0] != 1) continue
        val (newSch, newEng) = getSchedule(order, times, delay.copyOf())
//        squish(newSch, times, delay)
        if (newEng < bestEnergy) {
            bestEnergy = newEng
            bestSchedule = newSch
            if (newEng == target) {
                println("Guaranteed Solution")
                break
            }
        }
        val deltaE = newEng - eng
        val temp = 1 - (System.nanoTime() - startTime).toDouble() / DURATION_NANOS
        if (random() < annealProb(deltaE, temp)) {
//            println("Keeping new result at p=${annealProb(deltaE, temp)} (energy: $eng -> $newEng), temp: $temp")
            sch = newSch
            eng = newEng
        }
    }
    val stopTime = System.nanoTime()

    println("$reps moves made")
    println("Best energy: $bestEnergy")
    println("Best Schedule:")
    bestSchedule.forEach { it.forEach { print("$it ") };println() }
    println("Lower bound: $target")
    println()
    println()

//    times.forEach { it.forEach { print("$it ") };println() }
//    writeOutput(bestSchedule, bestEnergy)
    return Pair(bestSchedule, bestEnergy)
}

fun getStartingOrder(delay: IntArray, times: Array<IntArray>): Array<IntArray> {
    val target = getTarget(delay, times)
    val row = times.size
    val col = delay.size
    val order = matrix(row, col)
    val orderIndices = times.map { it.reduce { acc, i -> acc + i } }.map { target - it }.toIntArray()
    val spaces = times.map { it.reduce { acc, i -> acc + i } }.map { target - it }.toIntArray()
    val availableTasks: Array<List<Int>> = arrayOf()
    (0..row - 1).forEach {
        availableTasks[it] = delay.withIndex().filter { it.value == 0 }.map { it.index }
    }
    val benchTimes = (0..row - 1).map { 0 }.toIntArray()
    var done = 0
    while (done < row * col) {

        done++
    }
    return order
}

fun getStartingOrder2(delay: IntArray, times: Array<IntArray>): Array<IntArray> {
    val target = getTarget(delay, times)
    val row = times.size
    val col = delay.size
    val order = matrix(row, col)
    val sorted = delay.sorted().toIntArray()
    val spaces = times.map { it.reduce { acc, i -> acc + i } }.map { target - it }.toIntArray()
    val activeTasks = arrayListOf<Int>()
    val activeTaskEnds = arrayListOf<Int>()
    val busyTil = intArrayOf(0, 0, 0)
    val orderIndices = intArrayOf(0, 0, 0)
    var t = 0
    var done = 0
    while (done < row * col) {
        val benches = busyTil.withIndex().filter { t >= it.value }.filter { orderIndices[it.index] < col }
        if (benches.isEmpty()) {
            t++
            activeTaskEnds.withIndex().filter { it.value <= t }.map { it.index }.sortedDescending().forEach { activeTaskEnds.removeAt(it);activeTasks.removeAt(it) }
            continue
        }
        val nextStart = benches.map { it.value }.min()!!
        val bench = benches.filter { it.value == nextStart }.minBy { spaces[it.index] }!!.index
        val availableTasks = delay.withIndex().filter { it.value <= t }.filterNot { order[bench].contains(it.index) }.map { it.index }.minus(activeTasks)
        if (availableTasks.isEmpty()) {
            t++
            activeTaskEnds.withIndex().filter { it.value <= t }.map { it.index }.sortedDescending().forEach { activeTaskEnds.removeAt(it);activeTasks.removeAt(it) }
            continue
        }
        val task = if(availableTasks.size > 1)availableTasks.maxBy { times[bench][it] }!! else availableTasks.minBy { times[bench][it] }!!
        order[bench][orderIndices[bench]] = task
        activeTasks.add(task)
        activeTaskEnds.add(t + times[bench][task])
        busyTil[bench] = t + times[bench][task]
        orderIndices[bench]++
        done++
//        println("Scheduled task #$task on bench #$bench for time $t through ${t+times[bench][task]}, $done entry in order")
//        println("bench $bench now busy until ${busyTil[bench]}")
        t = busyTil.min()!!
        activeTaskEnds.withIndex().filter { it.value <= t }.map { it.index }.sortedDescending().forEach { activeTaskEnds.removeAt(it);activeTasks.removeAt(it) }
    }
//    val (sch, eng) = getSchedule(order, times, delay)
//    sch.forEach { it.forEach { print("$it ") };println() }
    return order
}

//fun findBestScheduleSlow(delay: IntArray, times: Array<IntArray>): Pair<Array<IntArray>, Int> {
//    val row = times.size
//    val col = times[0].size
////    val target = getTarget(delay, times)
//    val target = 302
//    println(target)
//
//    val startTime = System.nanoTime()
//    val order = perm((0..row * col - 1).toMutableList()).toIntArray()
//    var (sch, eng) = getScheduleSlow(order, times, delay.copyOf())
//    var bestSchedule = sch
//    var bestEnergy = eng
//    var reps = 0
//    while (System.nanoTime() < startTime + DURATION_NANOS) {
//        reps++
//        moveSlow(order)
//        val (newSch, newEng) = getScheduleSlow(order, times, delay.copyOf())
////        squish(newSch, times, delay)
//        if (newEng < bestEnergy) {
//            bestEnergy = newEng
//            bestSchedule = newSch
//            if (newEng == target) {
//                println("Guaranteed Solution")
//                break
//            }
//        }
//        val deltaE = newEng - eng
//        val temp = 1 - (System.nanoTime() - startTime).toDouble() / DURATION_NANOS
//        if (random() < annealProb(deltaE, temp)) {
////            println("Keeping new result at p=${annealProb(deltaE, temp)} (energy: $eng -> $newEng), temp: $temp")
//            sch = newSch
//            eng = newEng
//        }
//    }
//    val stopTime = System.nanoTime()
//    println("$reps moves made")
//    println("Best energy: $bestEnergy")
//    println("Best Schedule:")
//    bestSchedule.forEach { it.forEach { print("$it ") };println() }
//    println("Lower bound: $target")
//    println()
//    println()
////    times.forEach { it.forEach { print("$it ") };println() }
////    writeOutput(bestSchedule, bestEnergy)
//    return Pair(bestSchedule, bestEnergy)
//}

fun getSchedule(order: Array<IntArray>, times: Array<IntArray>, delay: IntArray): Pair<Array<IntArray>, Int> {
    val row = order.size
    val col = order[0].size
    val sch = matrix(row, col)
    var done = 0
    val total = row * col
    val spaces = times.map { it.reduce { acc, i -> acc + i } }.map { getTarget(delay, times) - it }.toIntArray()
    val benchNextStart = order.map { 0 }.toIntArray()
    val benchNextOrderIdx = order.map { 0 }.toIntArray()
    val taskMinStart = delay
    var nextBench: Int
    var nextTask: Int
    var taskEnd: Int
    var endTime = 0
    while (done < total) {
//        nextBench = benchNextStart.withIndex().filter { benchNextOrderIdx[it.index] < col }.minBy { max(it.value, taskMinStart[order[it.index][benchNextOrderIdx[it.index]]]) + times[it.index][order[it.index][benchNextOrderIdx[it.index]]] }!!.index
        val correction = IntArray(row)
        (0..row - 1).forEach { i1 ->
            if (benchNextOrderIdx[i1] >= col) return@forEach
            (i1 + 1..row - 1).forEach { i2 ->
                if (benchNextOrderIdx[i2] < col) {
                    if (order[i1][benchNextOrderIdx[i1]] == order[i2][benchNextOrderIdx[i2]]) {
                        if (getWeight(i1, benchNextOrderIdx[i1], order, times) > getWeight(i2, benchNextOrderIdx[i2], order, times)) {
                            if (Math.random() < 0.5) correction[i2] = 50000
//                            println("Breaking tie at $i1, ${order[i1][benchNextOrderIdx[i1]]} and $i2, ${order[i2][benchNextOrderIdx[i2]]}: Weights: ${getWeight(i1, benchNextOrderIdx[i1], order, times)} (${benchNextOrderIdx[i1]} tasks done) v.s. ${getWeight(i2, benchNextOrderIdx[i2], order, times)} (${benchNextOrderIdx[i2]} tasks done): bench $i1 takes priority")
                        } else {
                            if (Math.random() < 0.5) correction[i1] = 50000
//                            println("Breaking tie at $i1, ${order[i1][benchNextOrderIdx[i1]]} and $i2, ${order[i2][benchNextOrderIdx[i2]]}: Weights: ${getWeight(i1, benchNextOrderIdx[i1], order, times)} (${benchNextOrderIdx[i1]} tasks done) v.s. ${getWeight(i2, benchNextOrderIdx[i2], order, times)} (${benchNextOrderIdx[i2]} tasks done): bench $i2 takes priority")
                        }
                    }
                }
            }
        }
        nextBench = benchNextStart.withIndex().filter { benchNextOrderIdx[it.index] < col }.minBy { max(it.value, taskMinStart[order[it.index][benchNextOrderIdx[it.index]]]) + times[it.index][order[it.index][benchNextOrderIdx[it.index]]] + correction[it.index] }!!.index
//        nextBench = benchNextStart.withIndex().filter { benchNextOrderIdx[it.index] < col }.minBy { max(it.value, taskMinStart[order[it.index][benchNextOrderIdx[it.index]]]) + times[it.index][order[it.index][benchNextOrderIdx[it.index]]] }!!.index
        nextTask = order[nextBench][benchNextOrderIdx[nextBench]]
        taskEnd = max(benchNextStart[nextBench], taskMinStart[nextTask]) + times[nextBench][nextTask]
//        if (spaces[nextBench] == 0 && taskMinStart[nextTask] > benchNextStart[nextBench]) {
//            taskEnd = benchNextStart[nextBench] + times[nextBench][nextTask]
//
//        }
        sch[nextBench][nextTask] = taskEnd - times[nextBench][nextTask]
        taskMinStart[nextTask] = taskEnd
        benchNextStart[nextBench] = taskEnd
//        if(nextTask == order[nextBench][0] && delay[nextTask] > 0 && nextBench != 1) return Pair(arrayOf(intArrayOf(1)), 999999999)
        if (++benchNextOrderIdx[nextBench] == col) {
            endTime = max(endTime, taskEnd)
        }
        done++
    }
//    if (sch[1][5] == 0 && order[0][0] == 5) println("success")
    return Pair(sch, endTime)
}

fun getScheduleIncomplete(order: Array<IntArray>, times: Array<IntArray>, delay: IntArray): Pair<Array<IntArray>, Int> {
    val row = order.size
    val col = order[0].size
    val sch = matrix(row, col)
    var done = 0
    val total = row * col
    val benchNextStart = order.map { 0 }.toIntArray()
    val benchNextOrderIdx = order.map { 0 }.toIntArray()
    val taskMinStart = delay
    var nextBench: Int
    var nextTask: Int
    var taskEnd: Int
    var endTime = 0
    while (done < total) {
        nextBench = benchNextStart.withIndex().filter { benchNextOrderIdx[it.index] < col }.minBy { max(it.value, taskMinStart[order[it.index][benchNextOrderIdx[it.index]]]) + times[it.index][order[it.index][benchNextOrderIdx[it.index]]] }!!.index
        nextTask = order[nextBench][benchNextOrderIdx[nextBench]]
        taskEnd = max(benchNextStart[nextBench], taskMinStart[nextTask]) + times[nextBench][nextTask]
        sch[nextBench][nextTask] = taskEnd - times[nextBench][nextTask]
        taskMinStart[nextTask] = taskEnd
        benchNextStart[nextBench] = taskEnd
        if (nextTask == order[nextBench][0] && delay[nextTask] > 0 && nextBench != 1) return Pair(arrayOf(intArrayOf(1)), 999999999)
        if (++benchNextOrderIdx[nextBench] == col) {
            endTime = max(endTime, taskEnd)
        }
        done++
    }
    return Pair(sch, endTime)
}

fun getWeight(bench: Int, startIdx: Int, order: Array<IntArray>, times: Array<IntArray>): Int {
    val tasks = order[bench].drop(startIdx)
    if (tasks.isEmpty()) return 0
    return tasks.map { times[bench][it] }.reduce { acc, i -> acc + i }
}

fun getScheduleSlow(order: IntArray, times: Array<IntArray>, delay: IntArray): Pair<Array<IntArray>, Int> {
    val row = times.size
    val col = times[0].size
    val sch = matrix(row, col)
    var done = 0
    val total = row * col
    val benchNextStart = order.map { 0 }.toIntArray()
    val taskMinStart = delay
    var nextBench: Int
    var nextTask: Int
    var taskEnd: Int
    var endTime = 0
    while (done < total) {
        nextBench = order[done] % row
        nextTask = order[done] / row
        taskEnd = max(benchNextStart[nextBench], taskMinStart[nextTask]) + times[nextBench][nextTask]
        sch[nextBench][nextTask] = taskEnd - times[nextBench][nextTask]
        taskMinStart[nextTask] = taskEnd
        benchNextStart[nextBench] = taskEnd

        endTime = max(endTime, taskEnd)
        done++
    }
    return Pair(sch, endTime)
}


fun getTarget(delay: IntArray, times: Array<IntArray>): Int {
    val row = times.size
    val col = times[0].size
    val shortestDelays: IntArray
    if (col < row) {
        shortestDelays = (0..row - 1).map { delay[it % col] }.toIntArray()
    } else {
        shortestDelays = delay.sorted().take(row).toIntArray()
    }
    val firstTasks = (0..col - 1).filter { delay[it] == shortestDelays[0] }
    val firstTaskLength = times.flatMap { it.filterIndexed { idx, num -> firstTasks.contains(idx) } }.min()!!
    (1..shortestDelays.size - 1).forEach {
        shortestDelays[it] = min(shortestDelays[it], shortestDelays[0] + firstTaskLength)
    }
    val rowLengths = times.map { it.reduce { acc, i -> acc + i } }
            .sorted().toIntArray()
    val delayedRows = IntArray(row)
    (0..row - 1).forEach {
        delayedRows[it] = rowLengths[it] + shortestDelays[row - it - 1]
    }
    val bestRow = delayedRows.max()!!
    val bestCol = delay.copyOf()
    times.forEach { row ->
        (0..row.size - 1).forEach {
            bestCol[it] += row[it]
        }
    }
    val target = max(bestRow, bestCol.max()!!)
//    if (target == 26329) return target+1
    return target
}

//fun getScheduleSlow(order: Array<IntArray>, times: Array<IntArray>, delay: IntArray): Pair<Array<IntArray>, Int> {
//    val row = order.size
//    val col = order[0].size
//    val sch = matrix(row, col)
//    var done = 0
//    val total = row * col
//    val benchNextStart = order.map { 0 }.toIntArray()
//    val benchNextOrderIdx = order.map { 0 }.toIntArray()
//    val taskMinStart = delay
//    var nextBench: Int
//    var nextTask: Int
//    var taskEnd: Int
//    var endTime = 0
//    while (done < total) {
//        nextBench = benchNextStart.withIndex().filter { benchNextOrderIdx[it.index] < col }.maxBy { order[it.index].drop(benchNextOrderIdx[it.index]).map { task-> times[it.index][task] }.reduce { acc, step -> acc + step }}!!.index
////        nextBench = benchNextStart.withIndex().filter { benchNextOrderIdx[it.index] < col }.minBy { max(it.value, taskMinStart[order[it.index][benchNextOrderIdx[it.index]]]) + times[it.index][order[it.index][benchNextOrderIdx[it.index]]] + benchesTimeLeft}!!.index
//        nextTask = order[nextBench][benchNextOrderIdx[nextBench]]
//        var taskStart = benchNextStart[nextBench]
//        var currentCheckBench = 0
//        while(currentCheckBench < row) {
//            if (benchNextOrderIdx[currentCheckBench] > order[currentCheckBench].indexOf(nextTask)) {
//                currentCheckBench++
//                continue
//            }
//            if (taskStart >= sch[currentCheckBench][nextTask] && taskStart - sch[currentCheckBench][nextTask] < times[currentCheckBench][nextTask]) {
//                taskStart = sch[currentCheckBench][nextTask] + times[currentCheckBench][nextTask]
//                currentCheckBench = 0
//            } else {
//                currentCheckBench++
//            }
//        }
//        taskEnd = taskStart + times[nextBench][nextTask]
//        sch[nextBench][nextTask] = taskStart
//        taskMinStart[nextTask] = taskEnd
//        benchNextStart[nextBench] = taskEnd
//
//        if (++benchNextOrderIdx[nextBench] == col) {
//            endTime = max(endTime, taskEnd)
//        }
//        done++
//    }
//    return Pair(sch, endTime)
//}

fun squish(sch: Array<IntArray>, times: Array<IntArray>, delay: IntArray) {
    val endSch = add(sch, times)
    val lastBench = endSch.maxBy { it.max()!! }!!
    val endTime = endSch.map { it.max()!! }.max()!!
    val space = IntArray(sch.size)

    while (space.min()!! > 0) {
        (0..sch.size - 1).forEach {
            val firstIdx = sch[it].withIndex().minBy { it.value }!!.index
            val lastIdx = endSch[it].withIndex().maxBy { it.value }!!.index
            space[it] = delta(sch[it].sortedArray(), endSch[it].sortedArray())
            space[it] += sch[it][firstIdx] - delay[firstIdx]
            space[it] += endTime - endSch[it][lastIdx]
//        println("space $it = ${delta(sch[it].sortedArray(), endSch[it].sortedArray())} + ${sch[it][firstIdx] - delay[firstIdx]} + ${endTime - endSch[it][lastIdx]}")
        }
        println("Space: ${space[0]}, ${space[1]}, ${space[2]}")
//        squish the longest workbench left 1 (or to the next border, if that's easy to find)
        val bench = space.withIndex().minBy { it.value }!!.index
        var squishFrom = -1
        var squishTo = 0
        while (true) {
            squishFrom = sch[bench].filter { it > squishFrom }.min() ?: break
            val task = sch[bench].indexOf(squishFrom)
            squishTo = endSch[bench].filter { it <= squishFrom }.max() ?: 0
            squishTo = max(squishTo, delay[task])
            if (squishFrom == squishTo) continue

        }
    }
}

fun matrix(row: Int, col: Int) = arrayOf(*(1..row).map { IntArray(col) { -1 } }.toTypedArray())

fun perm(i: MutableList<Int>): MutableList<Int> {
    if (i.size == 1) return i
    val idx = (i.size * random()).toInt()
    val last = i[idx]
    i.removeAt(idx)
    return perm(i).plus(last).toMutableList()
}

fun move(order: Array<IntArray>) {
    val row = (random() * order.size).toInt()
    val c1 = (random() * order[0].size).toInt()
    val c2 = (c1 + 1) % order[0].size
    if ((c1 == 0 || c2 == 0)) return
    val temp = order[row][c1]
    order[row][c1] = order[row][c2]
    order[row][c2] = temp
}

fun moveSlow(order: IntArray) {
    val c1 = (random() * order.size).toInt()
    val c2 = (c1 + 1) % order.size
    val temp = order[c1]
    order[c1] = order[c2]
    order[c2] = temp
}

fun annealProb(deltaE: Int, temp: Double): Double {
    if (temp <= 0.0) {
        if (deltaE < 0)
            return 1.0
        else
            return 0.0
    }
    return 1.0 / (1 + exp(deltaE / (1 * temp)))
}

fun add(a1: Array<IntArray>, a2: Array<IntArray>): Array<IntArray> {
    val sum = matrix(a1.size, a1[0].size)
    (0..a1.size - 1).forEach { row ->
        (0..a1[0].size - 1).forEach { col ->
            sum[row][col] = a1[row][col] + a2[row][col]
        }
    }
    return sum
}

fun delta(startTimes: IntArray, endTimes: IntArray): Int {
    var sum = 0
    (0..startTimes.size - 2).forEach {
        sum += startTimes[it + 1] - endTimes[it]
    }
    return sum
}

fun List<Int>.toIntArray() = this.toTypedArray().toIntArray()


import java.io.File

/**
 * Created by Matt on 10/2/2018.
 */
//val inputFile = File("input/input_group15.txt")
val outputFile = File("")

fun main(args: Array<String>) {
//    (1..21).plus(38).forEach {
//        fileIdx = it
//        verify()
//    }
    verify()
}

fun verify() {
    val (delay, times) = readInput()
    val (sch, length) = readOutput()
    val target = getTarget(delay ,times)
    val endSch = add(sch, times)
    println("Group #$fileIdx")
//    println("Schedule:")
//    sch.forEach { it.forEach { print("$it ") };println() }
//    println("Times:")
//    times.forEach { it.forEach { print("$it ") };println() }
//    println("End Schedule:")
//    endSch.forEach { it.forEach { print("$it ") };println() }
//    println()
//    println()
//    println()
    val row = sch.size
    val col = sch[0].size
    if (row != times.size) println("Wrong number of benches")
    if (col != times[0].size) println("Wrong number of tasks")
    (0..col-1).forEach {
        val sMin = delay[it]
        val s0 = sch[0][it]
        val s1 = sch[1][it]
        val s2 = sch[2][it]
        val e0 = endSch[0][it]
        val e1 = endSch[1][it]
        val e2 = endSch[2][it]
        if (s0 < s1 && s1 < e0) println("Task $it starts on bench 1 (t=$s1)while active on bench 0 ($s0, $e0).")
        if (s0 < s2 && s2 < e0) println("Task $it starts on bench 2 (t=$s2)while active on bench 0 ($s0, $e0).")
        if (s1 < s0 && s0 < e1) println("Task $it starts on bench 0 (t=$s0)while active on bench 1 ($s1, $e1).")
        if (s1 < s2 && s2 < e1) println("Task $it starts on bench 2 (t=$s2)while active on bench 1 ($s1, $e1).")
        if (s2 < s0 && s0 < e2) println("Task $it starts on bench 0 (t=$s0)while active on bench 2 ($s2, $e2).")
        if (s2 < s1 && s1 < e2) println("Task $it starts on bench 1 (t=$s1)while active on bench 2 ($s2, $e2).")
        if (s0 <sMin) println("Task $it starts on bench 0 (t = $s0) before becoming available (t = $sMin).")
        if (s1 <sMin) println("Task $it starts on bench 1 (t = $s1) before becoming available (t = $sMin).")
        if (s2 <sMin) println("Task $it starts on bench 2 (t = $s2) before becoming available (t = $sMin).")
    }
    (0..row-1).forEach {
        val startTimes = sch[it].withIndex().sortedBy { it.value }
        val endTimes = endSch[it].withIndex().sortedBy { it.value }
//        println("Bench $it starts:")
//        startTimes.forEach { print("${it.value} ") }
//        println()
//        println("Bench $it ends:")
//        endTimes.forEach { print("${it.value} ") }
//        println()
        var i = 0
        var lastEnd = 0
        while (i < col) {
            val (task1, startTime) = startTimes[i]
            val (task2, endTime) = endTimes[i]
            if(startTime < lastEnd) println("On bench $it, task $task1 starts (t=$startTime) before task ${endTimes[i-1].index} finishes (t=${endTimes[i-1].value}).")
            else if (task1 != task2) println("Task mismatch $i: Task $task1 starts at t=$startTime, but task $task2 finishes next at t=$endTime ($task1 ends at ${endSch[it][task1]}).")

            lastEnd = endTime
            i++
        }
    }
    val finish = add(sch, times).map { it.max()!! }.max()!!
    if (length < finish) {
        println("Reported finish time is too early (reported $length, should be at least $finish).")
        return
    }
    if (length < target) println("Impossibly Fast")
    else if (length == target) println("Perfect")
    else println("Not guaranteed perfect ($target < $length)")

    println("Done")
    println()
    println()
}
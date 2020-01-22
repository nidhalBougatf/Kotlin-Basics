package mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

fun evaluateGuess(secret: String, guess: String): Evaluation {
    var g = guess
    var s=secret
    if (secret == guess)
        return Evaluation(secret.length,0)
    var right=0
    var wrong=0
    for (  index in 0..g.length-1) {
        if (g.get(index) == s.get(index) ) {
            g = g.replaceFirst(g.get(index).toString(),".",false)
            s = s.replaceFirst(s.get(index).toString(),"!",false)
            right++
        }
    }
    for ( (j,d) in g.withIndex())
    {
        for ((k,f) in s.withIndex()) {
            if (f == d ) {
                g = g.replaceFirst(g.get(j).toString(),",",false)
                s = s.replaceFirst(s.get(k).toString(),"?",false)
                wrong++
                break
            }
        }

    }
    return Evaluation(right,wrong)
}

import org.graalvm.polyglot.*


fun main(args: Array<String>) {
    val lines = "day13.txt"
        .readFile()
        .lines()
        .map { it.trim() }
        .toMutableList()

    lines.run {
        add("")
        add("[[2]]")
        add("[[6]]")
    }

    val js = org.graalvm.polyglot.Context.newBuilder("js").allowAllAccess(true).build()

    js.eval(
        "js", """
        function compareElements(left, right) {
            let leftIsArray = Array.isArray(left);
            let rightIsArray = Array.isArray(right);
            
            if (!leftIsArray && !rightIsArray) {
              if (left < right) {
                return -1;
              }                                                                            
              
              if (left === right) {
                return 0;              
              }
              
              return 1; 
            }
            
            if (!leftIsArray) left = [left];        
            if (!rightIsArray) right = [right];
            
            for (var i = 0; i < left.length; i++) {
              if (i === right.length) {
                return 1;
              }
            
              let comp = compareElements(left[i], right[i]);
              console.log('  left', JSON.stringify(left[i]), 'right', JSON.stringify(right[i]), comp);
              
              if (comp !== 0) {
                return comp; 
              }              
            }
            
            if (right.length > left.length) {
              return -1;
            }
                    
            return 0;        
        }       

        function compareLeftAndRight(left, right) {
            console.log('!!left', JSON.stringify(left), 'right', JSON.stringify(right));
            
            var res = compareElements(left, right);
            console.log("res", res);
            console.log();
            
            return res;
        }
    """
    )

    fun compare(left: Value, right: Value): Int {
        val bindings = js.getBindings("js")
        bindings.putMember("left", left);
        bindings.putMember("right", right);

        return js.eval("js", "compareLeftAndRight(left, right)").asInt();
    }

    val signals = lines
        .split(includePassingLine = false) { it.isBlank() }
        .flatMap { strings ->
            val (left, right) = strings.map {
                js.eval(Source.create("js", it))
            }

            Pair(left, right).toList()
        }

    val sorted = signals.sortedWith(Comparator(::compare))
    sorted
        .forEachIndexed { i, s ->
            println("#${i + 1} $s")
        }
}

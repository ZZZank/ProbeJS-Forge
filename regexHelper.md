
## String format
"(.+)".formatted\((.+)\)
->
String.format("$1", $2)

" "\.repeat\((.+)\)
->
PUtil.indent($1)

com.probejs
->
com.probejs

dev.latvian.kubejs
->
dev.latvian.kubejs
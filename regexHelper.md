
## String format
"(.+)".formatted\((.+)\)
->
String.format("$1", $2)

" "\.repeat\((.+)\)
->
PUtil.indent($1)

com.prunoideae.probejs
->
com.probejs

dev.latvian.mods.kubejs
->
dev.latvian.kubejs
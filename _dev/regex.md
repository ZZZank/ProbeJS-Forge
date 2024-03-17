
```
case (.+) -> (.+);

case $1: $2;break;
```

```
(\s+)case (.+), (.+)

case $1:
case $2
```

```
(\s+)if \((.+) instanceof (.+) (.+)\) \{

$1if ($2 instanceof $3) {
$1    $3 $4 = ($3) $2;
```

```
(\s+)if \((.+) instanceof (.+) (.+) && (.+)\) \{

$1if ($2 instanceof $3 && $5) {
$1    $3 $4 = ($3) $2;
```

```
(\s+)\} else if \((.+) instanceof (.+) (.+)\) \{

$1} else if ($2 instanceof $3) {
$1    $3 $4 = ($3) $2;
```
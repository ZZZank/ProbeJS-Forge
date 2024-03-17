

lines = ['String.join("\\n",']
line = input()
while line != "e":
    if line == 'e':
        break
    elif line == 'c':
        lines[-1] = lines[-1].removesuffix(',')
        lines.append(')')
        for line in lines:
            print(line)
        lines = ['String.join("\\n",']
    else:
        lines.append('"' + line.strip().replace('"', '\\"') + '",')
    line = input()

lines[-1] = lines[-1].removesuffix(',')
lines.append(')')

for line in lines:
    print(line)
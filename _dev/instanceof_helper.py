

line = input()
while True:
    if line == 'exit':
        break
    # v instanceof Callable f
    elements = line.split(' ')
    if len(elements) != 4:
        break
    orig = elements[0]
    typeTo = elements[2]
    target = elements[3]
    print(f'{typeTo} {target} = ({typeTo}) {orig};')
    line = input()

import typing

def main() -> int:
    add = (lambda a,b: a + b)
    print("add(5, 7) = ", add(5,7), "\n", sep="", end="")
    square = (lambda x: x * x)
    print("square(6) = ", square(6), "\n", sep="", end="")
    return 0

if __name__ == "__main__":
    main()

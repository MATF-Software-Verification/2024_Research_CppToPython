import typing

def square(x) -> int:
    return x * x

def main() -> int:
    print("Square: ", square(5), sep="")
    print()
    t: str = "world"
    print(t, sep="")
    print()
    x: int = 10
    _t_0 = x
    x += 1
    _t_1 = x
    x -= 1
    print("x++: ", _t_0, ", x--: ", _t_1, sep="")
    print()
    ci = 7
    return 0

if __name__ == "__main__":
    main()

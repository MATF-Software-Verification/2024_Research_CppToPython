import typing

def f(x) -> int:
    return (1 if x == 0 else x * f(x-1))

def main() -> int:
    print(f(4), sep="")
    print()
    return 0

if __name__ == "__main__":
    main()

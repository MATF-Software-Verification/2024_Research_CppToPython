import typing

def main() -> int:
    x: int = 10 // 2
    z: float = 11 / 4
    y: int = 10 % 3
    print(x, " ", y, " ", z, sep="")
    print()
    return 0

if __name__ == "__main__":
    main()

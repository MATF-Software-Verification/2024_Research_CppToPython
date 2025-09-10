import typing

def main() -> int:
    x: int = -3
    for i in range(0, 3):
        x += i
        print(i, sep="")
        print()
    return x

if __name__ == "__main__":
    main()

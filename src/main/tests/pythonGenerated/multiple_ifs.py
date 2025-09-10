import typing

def main() -> int:
    a: int = 5
    b: int = 10
    if a > 0:
        print("a ok", sep="")
        print()
    if b > 0:
        print("b ok", sep="")
        print()
    return 0

if __name__ == "__main__":
    main()

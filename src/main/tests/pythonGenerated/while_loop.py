import typing

def main() -> int:
    i: int = 0
    while i < 3:
        print(i, sep="")
        print()
        i += 1
    return 0

if __name__ == "__main__":
    main()

import typing

def main() -> int:
    x: int = 10
    if x > 5:
        print("greater", sep="")
        print()
    else:
        print("smaller", sep="")
        print()
    return 0

if __name__ == "__main__":
    main()

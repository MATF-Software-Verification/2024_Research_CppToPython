import typing

def main() -> int:
    x: int = 0
    y: int = 5
    if x > 0:
        print("True", sep="")
        print()
    elif x - y > 0:
        x = y
    else:
        print("False", sep="")
        print()
    return 0

if __name__ == "__main__":
    main()

import typing

def factorial(n) -> int:
    if n <= 1:
        return 1
    return n * (factorial(n-1))

def main() -> int:
    num: int = 5
    res = factorial(num)
    print("Factorial of ", num, " is ", res, sep="")
    print()
    return 0

if __name__ == "__main__":
    main()

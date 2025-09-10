import typing

def main() -> int:
    for i in range(1, (100 + 1)):
        if i % 3 == 0  and  i % 5 == 0:
            print("FizzBuzz", sep="")
            print()
        elif i % 3 == 0:
            print("Fizz", sep="")
            print()
        elif i % 5 == 0:
            print("Buzz", sep="")
            print()
        else:
            print(i, sep="")
            print()
    return 0

if __name__ == "__main__":
    main()

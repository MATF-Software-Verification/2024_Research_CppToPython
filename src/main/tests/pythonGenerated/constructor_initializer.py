import typing

class Example:
    def __init__(self, v):
        self.value = v

def main() -> int:
    e = Example(42)
    print("Constructor set value = ", e.value, "\n", sep="", end="")

if __name__ == "__main__":
    main()

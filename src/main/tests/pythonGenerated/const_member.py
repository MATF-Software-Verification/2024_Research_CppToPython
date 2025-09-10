import typing

class Example:
    def __init__(self, v):
        self.value = v
    
    def getValue(self) -> int:
        return self.value

def main() -> int:
    e = Example(100)
    print("getValue() = ", e.getValue(), "\n", sep="", end="")

if __name__ == "__main__":
    main()

import typing

class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age
    
    def getName(self) -> str:
        return self.name
    
    def setName(self, name):
        self.name = name
    
    def getAge(self) -> int:
        return self.age
    
    def setAge(self, newAge):
        if newAge >= 0:
            self.age = newAge
    
    def display(self):
        print("Name: ", self.name, ", age: ", self.age, sep="")
        print()

def main() -> int:
    p1 = Person("Neko", 25)
    p1.display()
    p2 = Person("Alice", 30)
    p2.display()
    p2.setName("Bob")
    p2.setAge(35)
    p2.display()
    return 0

if __name__ == "__main__":
    main()

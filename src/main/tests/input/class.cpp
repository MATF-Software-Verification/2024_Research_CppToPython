#include <iostream>
#include <string>


class Person {
private:
    std::string name;
    int age;

public:

    Person(const std::string& name, int age) {
        this->name = name;
        this->age = age;
    }

    std::string getName() const {
        return this->name;
    }

    void setName(const std::string& name) {
        this->name = name;
    }

    int getAge() const {
        return this->age;
    }

    void setAge(int newAge) {
        if (newAge >= 0) {
            this->age = newAge;
        }
    }
    void display(){
        std::cout << "Name: " << this->name << ", age: " << this->age <<std::endl;
    }

};

int main() {
    Person *p1= new Person("Neko",25);
    p1->display();

    Person p2("Alice", 30);
    p2.display();

    p2.setName("Bob");
    p2.setAge(35);
    p2.display();

    return 0;
}

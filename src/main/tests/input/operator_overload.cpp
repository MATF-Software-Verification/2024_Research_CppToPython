#include <iostream>

class Example {
    int value;
public:
    Example(int v) : value(v) {}
    Example operator+(const Example& other) const {
        return Example(value + other.value);
    }
    int getValue() const { return value; }
};

int main() {
    Example e1(5), e2(7);
    Example e3 = e1 + e2;
    std::cout << "e1 + e2 = " << e3.getValue() << "\n";
}

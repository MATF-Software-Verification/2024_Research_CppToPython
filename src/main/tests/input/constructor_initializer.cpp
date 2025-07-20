#include <iostream>

class Example {
public:
    int value;
    Example(int v) : value(v) {}
};

int main() {
    Example e(42);
    std::cout << "Constructor set value = " << e.value << "\n";
}

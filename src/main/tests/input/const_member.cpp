#include <iostream>

class Example {
    int value;
public:
    Example(int v) : value(v) {}
    int getValue() const noexcept { return value; }
};

int main() {
    Example e(100);
    std::cout << "getValue() = " << e.getValue() << "\n";
}

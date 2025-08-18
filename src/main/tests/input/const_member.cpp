#include <iostream>

class Example {
private:
    int value;
public:
    Example(int v) : value(v) {}
    int getValue() const noexcept { return this->value; }
};

int main() {
    Example e(100);
    std::cout << "getValue() = " << e.getValue() << "\n";
}

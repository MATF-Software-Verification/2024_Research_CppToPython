#include <iostream>

class Example {
    int value;
public:
    Example(int v) : value(v) {}
    friend void inspect(const Example& ex);
};

void inspect(const Example& ex) {
    std::cout << "Inspecting Example: " << ex.value << "\n";
}

int main() {
    Example e(42);
    inspect(e);
}
#include <iostream>

class Example {
public:
    int value{0};
    Example() = default;           // defaulted
    Example(const Example&) = default;
    Example(Example&&) = delete;   // deleted
};

int main() {
    Example e1;
    Example e2(e1); // copy works
    std::cout << "Defaulted copy created, value = " << e2.value << "\n";
}

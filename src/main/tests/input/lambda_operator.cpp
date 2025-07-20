#include <iostream>

auto makeLambda() {
    return [](int x, int y) { return x * y; };
}

int main() {
    auto multiply = makeLambda();
    std::cout << "multiply(6, 7) = " << multiply(6, 7) << "\n";
}

#include <iostream>

int main() {
    auto add = [](int a, int b) { return a + b; };

    std::cout << "add(5, 7) = " << add(5, 7) << "\n";

    auto square = [](int x) { return x * x; };

    std::cout << "square(6) = " << square(6) << "\n";

    return 0;
}

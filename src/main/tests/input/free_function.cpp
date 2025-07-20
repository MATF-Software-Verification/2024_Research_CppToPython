#include <iostream>

[[nodiscard]] inline int add(int a, int b) {
    return a + b;
}

int main() {
    std::cout << "add(3, 5) = " << add(3, 5) << "\n";
}
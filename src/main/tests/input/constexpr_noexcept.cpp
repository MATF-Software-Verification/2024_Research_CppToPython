#include <iostream>

constexpr int square(int x) noexcept {
    return x * x;
}

int main() {
    std::cout << "square(7) = " << square(7) << "\n";
}

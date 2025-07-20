#include <iostream>

template<typename T>
T max_value(T a, T b) {
    return (a > b) ? a : b;
}

int main() {
    std::cout << "max_value(4, 9) = " << max_value(4, 9) << "\n";
}

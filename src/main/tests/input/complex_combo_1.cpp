#include <iostream>

int square(int x) {
    return x * x;
}

int main() {
    for (int i = 0; i < 3; ++i) {
        if (i % 2 == 0)
            std::cout << square(i) << std::endl;
    }
    return 0;
}
#include <iostream>
int main() {
    int x = -3;
    for (int i = 0; i < 3; ++i) {
        x += i;
        std::cout << i << std::endl;
    }
    return x;
}
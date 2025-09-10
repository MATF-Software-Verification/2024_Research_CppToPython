#include <iostream>
int f(int x) {
    return x == 0 ? 1 : x * f(x - 1);
}
int main() {
    std::cout << f(4) << std::endl;
    return 0;
}
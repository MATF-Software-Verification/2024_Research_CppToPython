#include <iostream>
int main() {
    int x = 10;
    if (x < 5)
        std::cout << "small" << std::endl;
    else if (x == 10)
        std::cout << "equal" << std::endl;
    else
        std::cout << "big" << std::endl;
    return 0;
}
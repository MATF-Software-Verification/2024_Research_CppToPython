#include <iostream>
int main() {
    int val = 2;
    switch (val) {
        case 1: std::cout << "One" << std::endl; break;
        case 2: std::cout << "Two" << std::endl; break;
        default: std::cout << "Other" << std::endl;
    }
    return 0;
}
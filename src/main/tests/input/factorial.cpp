#include <iostream>

int factorial(int n) {
    if (n <= 1){
        return 1;
    }
    return n * (factorial (n - 1));
}

int main() {
    int num = 5;
    auto res = factorial(num);
    std::cout << "Factorial of " << num << " is " << res << std::endl;
    return 0;
}
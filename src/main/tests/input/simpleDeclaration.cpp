#include <iostream>
#include <string>
#include <vector>
#include <typeinfo>


int square(int x) { return x * x; }

int main() {

//    // Function call
    std::cout << "Square: " << square(5) << std::endl;

    // Explicit type construction
    std::string t{"world"};
    std::cout  << t << std::endl;

    // Post-increment and post-decrement
    int x = 10;
    std::cout << "x++: " << x++ << ", x--: " << x-- << std::endl;

    const int ci = 7;
    return 0;
}

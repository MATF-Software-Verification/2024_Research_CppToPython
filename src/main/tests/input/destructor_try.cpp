#include <iostream>
#include <stdexcept>

class Example {
public:
    ~Example() try {
        std::cout << "Destructor running\n";
        throw std::runtime_error("Oops");
    } catch (...) {
        std::cerr << "Destructor caught exception\n";
    }
};

int main() {
    Example e; // triggers destructor
    std::cout << "Object created and destroyed.\n";
}

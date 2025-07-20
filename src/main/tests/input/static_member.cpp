#include <iostream>
#include <string>

class Logger {
public:
    static void log(const std::string& msg) {
        std::cout << "[Log] " << msg << "\n";
    }
};

int main() {
    Logger::log("Hello from static member function");
}
